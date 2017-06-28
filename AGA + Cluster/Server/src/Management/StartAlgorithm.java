package Management;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Vector;

import net.sf.json.JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Connection.ClusterServer;

public class StartAlgorithm extends Thread{

	private InputStream in; 
	private OutputStream out;
	private JSONObject json1;
	JSONObject traseu;
	
	public StartAlgorithm(InputStream in, OutputStream out, JSONObject json) {
		this.in = in;
		this.out = out;
		this.json1 = json;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("[Orchestrare]Trimit mesaj brodcast si primesc solutia!");
			boolean result = this.sendStartAndGetSolution(json1);
			System.out.println("[Orchestrare]Trimit flagul primit de la solutie!");
			if(result == false){
				json1.put("msg", "0");
				
					Helpful.write(this.out, json1.toJSONString());
			}else{
				json1.put("msg", "1");
				Helpful.write(this.out, json1.toJSONString());
			}
			System.out.println("[Orchestrare]Am trimis flagul");
			System.out.println("[Orchestrare]Trimit traseul");
			Helpful.write(this.out, traseu.toJSONString());
			System.out.println("[Orchestrare]Am trimis traseul");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean sendStartAndGetSolution(JSONObject json) {
		float max = 1000000;
		float maxTime = 0;
		Collection<Socket> v = ClusterServer.pcOfCluster.values();
		for (Socket socket : v) {
			try {
				Helpful.write(socket.getOutputStream(), json.toString());
			} catch (IOException e) {
				System.out.println("[Orchestrare Eroare]Eroare la pornire Algoritm genetic!");
			}
		}
		String str;
		boolean flag = true;
		
		flag = readProgressBar();
		for (Socket socket : v) {
			try {
				System.out.println("[Orchestrare]Astept mesaj de confirmare pentru solutie!");
				str = Helpful.read(socket.getInputStream());
				System.out.println("[Orchestrare]Mesajul este:"+str);
				json = stringToJsonObject(str);
				if(json.containsKey("msg") == false){
					flag = false;
				}else if(Integer.parseInt(json.get("msg").toString()) != 200){
					flag = false;
				}else{
						System.out.println("[Orchestrare]Astept saprimesc solutia depistata!");
						str =  Helpful.read(socket.getInputStream());
						System.out.println("[Orchestrare]Am primit solutia depistata!");
						json = stringToJsonObject(str);
						if(json.containsKey("msg") == false){
							if(max > Float.parseFloat(stringToJsonObject(str).get("distanta").toString())){
								traseu = stringToJsonObject(str);
								max = Float.parseFloat(traseu.get("distanta").toString());
							}
							if(maxTime < Float.parseFloat(stringToJsonObject(str).get("time").toString())){
								maxTime = Float.parseFloat(stringToJsonObject(str).get("time").toString());
							}
						}else{
							flag = false;
						}
				}
				
			} catch (IOException e) {
				System.out.println("[Orchestrare Solutie]Eroare la citire brodcast");
				flag = false;
			}catch(Exception e){
				System.out.println("[Orchestrare]Eroare la citiresolutie");
				flag = false;
			}
		}
		traseu.put("time", maxTime);
		System.out.println("[Orchestrare]Flag solutie:" + flag);
		return flag;
	}
	
	public JSONObject stringToJsonObject(String str) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			return json;
		} catch (ParseException e) {
			System.out.println("[EROARE] EROARE LA TRANSFORMARE DIN STRING IN JSON");
			JSONObject json = new JSONObject();
			json.put("msg", 678);
			return json;
		}catch(Exception e){
			System.out.println("[EROARE] EROARE Necunoscuta json");
			JSONObject json = new JSONObject();
			json.put("msg", 677);
			return json;
		}
	}
	
	public boolean readProgressBar(){
		Collection<Socket> v = ClusterServer.pcOfCluster.values();
		Vector<Boolean> bv = new Vector<Boolean>();
		for(int i = 0; i < v.size(); i++){
			bv.add(true);
		}
		
		boolean ok = true;
		boolean flag = true;
		String str;
		System.out.println("[ORCHESTRARE]Citesc progress");
		float time = (float) 0.0;
		JSONObject json  = new JSONObject();
		while(ok){
			int s = 20;
			int cont = 0;
			for(Socket socket : v){
				try {
					if(bv.get(cont) == true){
						str =  Helpful.read(socket.getInputStream());
						System.out.println(str);
						json = stringToJsonObject(str);
						if(json.containsKey("cod") == true){
							if(Integer.parseInt(json.get("cod").toString()) == 10){
								bv.set(cont, false);
								//time += Float.parseFloat(json.get("time").toString());
							}else {
								s += Integer.parseInt(json.get("cod").toString());
							}
						}else{
							flag = false;
							System.out.println("[Eroare]Eroare la progessbar" + Integer.parseInt(json.get("msg").toString()));
						}
					}
					cont++;
				} catch (IOException e) {
					System.out.println("[ORCHESTRARE]Eroare la citire socket progres");
				}catch(Exception e){
					System.out.println("[ORCHESTRARE]Eroare progressbar");
				}
			}
			
			ok = false;
			int ct = 0;
			for(int k = 0; k < bv.size(); k++){
				if(bv.get(k) == true){
					ct++;
				}
			}
			
			if(ct > 0)
				ok = true;
			
			if(ok == true){
				float rez = s / ct;
				System.out.println("Rezultatul este:" + rez);
				json.put("msg", 321);
				json.put("progres", rez);
				try {
					Helpful.write(out, json.toJSONString());
				} catch (IOException e) {
					System.out.println("[Eroare]Eroare la trimiterea progresului catre interfata");
				}
			}
		}
		return flag;
	}
}
