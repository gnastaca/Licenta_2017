package Management;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Set;

import javax.net.ssl.SSLSocket;

import net.sf.json.JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Connection.ClusterServer;
import Connection.RestoreConnections;

public class ClusterOrchestration extends Thread {
	private SSLSocket socket;
	private InputStream in;
	private OutputStream out;
	private JSONObject traseu = new JSONObject();

	public ClusterOrchestration(SSLSocket socket) {
		this.socket = socket;
		try {
			this.in = this.socket.getInputStream();
			this.out = this.socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		if(RestoreConnections.contorReset == 300){
			System.out.println("Se realizeaza resetarea!");
			try {
				JSONObject json = new JSONObject();
				System.out.println("[Orchestrare]Trimit ca resetez");
				json.put("function_code", 1);
				System.out.println("Trimir faptul ca se realizaeaza resetare.");
				Helpful.write(out, json.toJSONString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//setFlag(ConnectToCluster.flag);
		try {
			JSONObject json = new JSONObject();
			json.put("function_code", 0);
			//Se creaza clusterul prin trimiterea listelor de ip-uri catre toate nodurile
			checkAllConnectionAreUP();
			ConnectToCluster.flag = true;
			if(this.sendToAllNodeslistOfIP())
				json.put("msg", 1);
			else
				json.put("msg", 0);
			//Se instiinteaza administratorul de cluster
			Helpful.write(out, json.toJSONString());
		} catch (IOException e1) {
			System.out.println("A aparut o eroare la verificarea conexiunilor si la trimiterea listei de ip!");
			ConnectToCluster.flag = false;
		}
		
		System.out.println("Eu orchestrez clusterul");
		System.out.println("Nr pc cluster:"+ ClusterServer.pcOfCluster.size());
		
		while (true) {
			try {
				String str = Helpful.read(this.in);
				JSONObject json1 = stringToJsonObject(str);
				//Doresc sa inchid fara sa finalizez operatia
				if(Integer.parseInt(json1.get("cluster_orchestration_functions").toString()) == 500){
					System.out.println("[CLUSTER ORCHESTRATION]OK, inchid conexiunea!");
					this.socket.close();
					ConnectToCluster.flag = false;
					//setFlag(ConnectToCluster.flag);
					break;
				}
				
				if(Integer.parseInt(json1.get("cluster_orchestration_functions").toString()) == 1 && Integer.parseInt(json1.get("function_code").toString()) != 4){
					System.out.println("[Orchestrare]Trimit mesaj brodcast!");
					boolean result = this.sendMessageToAll(json1);
					if(result == false){
						json1.put("msg", "0");
						Helpful.write(this.out, json1.toJSONString());
					}else{
						json1.put("msg", "1");
						Helpful.write(this.out, json1.toJSONString());
					}
				}
				
				if(Integer.parseInt(json1.get("function_code").toString()) == 4){
					StartAlgorithm SA = new StartAlgorithm(in, out, json1);
					SA.start();
				}
				
				if(Integer.parseInt(json1.get("cluster_orchestration_functions").toString()) == 3){
					System.out.println("Am primit force restart!!!");
					JSONObject restartJson = new JSONObject();
					restartJson.put("cod", 777);
					RestoreConnections.contorReset = 350;
					for(int port : ClusterServer.pcOfCluster.keySet()){
						Socket s = new Socket(ClusterServer.pcOfCluster.get(port).getInetAddress().getHostAddress(), port);
						Helpful.write(s.getOutputStream(), restartJson.toJSONString());
					}
					restartJson.put("msg", -1);
					Helpful.write(out, restartJson.toJSONString());
				}
				
			} catch (IOException e) {
				ConnectToCluster.flag = false;
				System.out.println("[ClusterOrchestration ERROR]BRODCAST SEND ERROR");
			}
		}
	}
	
	//For test
	public boolean sendMessageToAll(JSONObject json) {
		Collection<Socket> v = ClusterServer.pcOfCluster.values();
		for (Socket socket : v) {
			try {
				System.out.println("Am trimis mesaj catre:" + socket.getInetAddress().getHostAddress());
				Helpful.write(socket.getOutputStream(), json.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String str;
		boolean flag = true;

		for (Socket socket : v) {
			try {
				System.out.println("Astept mesajul de confirmare de la:" +socket.getInetAddress().getHostAddress());
				str = Helpful.read(socket.getInputStream());
				json = stringToJsonObject(str);
				System.out.println("Mesajul de confirmare este:" + json.get("msg").toString());
				if(Integer.parseInt(json.get("msg").toString()) != 200){
					if(Integer.parseInt(json.get("msg").toString()) == 99){
						System.out.println("[Eroare]Eroare la decodificare mesaj in node!");
					}
					flag = false;
				}
				
			} catch (IOException e) {
				flag = false;
				System.out.println("[Orchestrare]Eroare la citire brodcast");
			}
		}
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


	public boolean sendToAllNodeslistOfIP(){
		Set<Integer> v = ClusterServer.pcOfCluster.keySet();
		JSONObject json = new JSONObject();
		json.put("function_code", 5);
		int j = 0;
		for (Integer key1  : v) {
			int c = 0;
			for(Integer key2 : v){
				if(key1 != key2){
					String ip = ClusterServer.pcOfCluster.get(key2).getInetAddress().getHostAddress();
					json.put(c, ip);
					json.put("port_" + c, key2);
					c++;
				}
			}
			json.put("nrPcCluster",  ClusterServer.pcOfCluster.size()-1);
			try {
				Helpful.write(ClusterServer.pcOfCluster.get(key1).getOutputStream(), json.toString());
			} catch (IOException e) {
				System.out.println("Lista de ip-uri nu a putut fi trimisa");
			}
			j++;
		}
		String str;
		boolean flag = true;

		for (Integer key : v) {
			try {
				System.out.println("Asteptam confirmare precum ca lista de ip-uri a fost primita");
				str = Helpful.read(ClusterServer.pcOfCluster.get(key).getInputStream());
				json = stringToJsonObject(str);
				System.out.println("[Code]" + json.get("msg").toString());
				if(Integer.parseInt(json.get("msg").toString()) != 200){
					flag = false;
				}
				
			} catch (IOException e) {
				System.out.println("Eroarea la verificare daca lista de ip-uri a fost primita");
				flag = false;
			}
		}
		return flag;
	}
	
	public boolean readProgressBar(){
		Collection<Socket> v = ClusterServer.pcOfCluster.values();
		boolean ok = true;
		boolean flag = true;
		String str;
		System.out.println("[ORCHESTRARE]Citesc progress");
		float time = (float) 0.0;
		JSONObject json  = new JSONObject();
		while(ok){
			int s = 20;
			for(Socket socket : v){
				try {
					str =  Helpful.read(socket.getInputStream());
					System.out.println(str);
					json = stringToJsonObject(str);
					if(json.containsKey("cod") == true){
						if(Integer.parseInt(json.get("cod").toString()) == 10){
							ok = false;
							time += Float.parseFloat(json.get("time").toString());
						}else {
							s += Integer.parseInt(json.get("cod").toString());
						}
					}else{
						flag = false;
						System.out.println("[Eroare]Eroare la progessbar" + Integer.parseInt(json.get("msg").toString()));
					}
				} catch (IOException e) {
					System.out.println("[ORCHESTRARE]Eroare la citire socket progres");
				}catch(Exception e){
					System.out.println("[ORCHESTRARE]Eroare progressbar");
				}
			}
			float rez = s / v.size();
			System.out.println("Rezultatul este:" + rez);
			json.put("msg", 321);
			json.put("progres", rez);
			if(ok  == false){
				json.put("time", time/v.size());
			}
			try {
				Helpful.write(out, json.toJSONString());
			} catch (IOException e) {
				System.out.println("[Eroare]Eroare la trimiterea progresului catre interfata");
			}
		}
		return flag;
	}
	
	public boolean sendStartAndGetSolution(JSONObject json) {
		traseu.put("distanta", -1);
		
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
							traseu = stringToJsonObject(str);
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
		System.out.println("[Orchestrare]Flag solutie:" + flag);
		return flag;
	}
	
	public boolean setFlag(boolean flag){
		Collection<Socket> v = ClusterServer.pcOfCluster.values();
		JSONObject json = new JSONObject();
		json.put("function_code", 6);
		json.put("status", flag);
		for (Socket socket : v) {
			try {
				System.out.println("Am trimis setare flag catre:" + socket.getInetAddress().getHostAddress());
				Helpful.write(socket.getOutputStream(), json.toString());
			} catch (IOException e) {
				System.out.println("[Orchestrare Eroare]Eroare la trimitere status");
			}
		}
		
		boolean f = true;
		for (Socket socket : v) {
			try {
				System.out.println("Astept mesajul de confirmare set flag:" +socket.getInetAddress().getHostAddress());
				String str = Helpful.read(socket.getInputStream());
				System.out.println("Mesajul de confirmare este:" + json.get("msg").toString());
				json = stringToJsonObject(str);
				if(Integer.parseInt(json.get("msg").toString()) != 200){
					f = false;
				}
			} catch (IOException e) {
				System.out.println("[Orchestrare Eroare]Eroare la citire status");
			}
		}
		return f;
	}
	
	public static void resetConnection(){
		Collection<Socket> v = ClusterServer.pcOfCluster.values();
		JSONObject json = new JSONObject();
		json.put("function_code", 6);
		for (Socket socket : v) {
			try {
				System.out.println("Am trimis resetare conexiune" + socket.getInetAddress().getHostAddress());
				Helpful.write(socket.getOutputStream(), json.toString());
			} catch (IOException e) {
				System.out.println("[Orchestrare Eroare]Resetare conexiuni eroare");
			}
		}
	}
	
	public void checkAllConnectionAreUP(){
		Set<Integer> keys = ClusterServer.pcOfCluster.keySet();
		JSONObject json = new JSONObject();
		json.put("cod", 777);
		for(int port : keys){
			try{
				Socket s = new Socket(ClusterServer.pcOfCluster.get(port).getInetAddress().getHostAddress(), port);
				Helpful.write(s.getOutputStream(), json.toJSONString());
				s.close();
			}catch(Exception e){
				System.out.println("[Deconectare] Calculatorul cu adresaip:" + ClusterServer.pcOfCluster.get(port).getInetAddress().getHostAddress() +  " s-a deconectat.");
				ClusterServer.pcOfCluster.remove(port);
			}
		}
	}
}
