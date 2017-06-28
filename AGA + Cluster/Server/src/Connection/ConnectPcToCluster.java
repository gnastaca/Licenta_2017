package Connection;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Management.Helpful;
/**
 * 
 * @author Geo
 *Conexiunea efectiva cu clientul
 *Receptionarea si trimiterea de mesaje
 */
public class ConnectPcToCluster extends Thread{
	Socket clientSocket;
	boolean clientInfo;
	String clientName;
	
	
	public ConnectPcToCluster(Socket socket, String clientName){
		this.clientSocket = socket;
		this.clientInfo = true;
		this.clientName = clientName;
	}
	
	//conversia de la string la json
	public JSONObject stringToJsonObject(String str){
	   	try {
	       	JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(str);
				return json;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	return null;
	}
	
	//Afisarea rezultatului primit de la nod
	public void PrintRezult(JSONObject json){
		System.out.println(this.clientName + ":" + json.get("msg"));
	}
	
	//Verificarea conexiunii cu nodul
	public void CheckConnectionStatus(InputStream in,  OutputStream out) throws IOException{
		JSONObject json = new JSONObject();
		String str = "";
		json.put("function_code", 0);
		json.put("msg", "ok putem incepe");
		Helpful.write(out, json.toString());
    	str = Helpful.read(in);
    	json = stringToJsonObject(str);
    	if(json != null && Integer.parseInt(json.get("function_code").toString()) == 0)
    		this.PrintRezult(json);
	}
	
	//Stare nume nod
	private void SetNameClient(InputStream in,  OutputStream out) throws IOException{
		JSONObject json = new JSONObject();
		String str = "";
		json.put("function_code", 1);
		json.put("msg", this.clientName);
		Helpful.write(out, json.toString());
		
    	str = Helpful.read(in);
    	json = stringToJsonObject(str);
    	if(json != null && Integer.parseInt(json.get("function_code").toString()) == 0)
    		this.PrintRezult(json);
	}
	
	
	public void run(){
		InputStream in = null; 
        OutputStream out = null; 
         System.out.println("Accepted Client Address - " + this.clientSocket.getInetAddress().getHostName());
        
         try {
        	 in = this.clientSocket.getInputStream();
        	 out = this.clientSocket.getOutputStream();
        	 
        	 boolean flag = true;
        	 String input = null;
        	 JSONObject json = new JSONObject();
    
        	 System.out.println("Am ajuns aici!!!");
        	 this.CheckConnectionStatus(in, out);
        	 this.SetNameClient(in, out);
         } catch (IOException e) {
        	 System.out.println("Eroare la conexiunea cu clientul!");
 
//         }finally{
//        	 try{
//        		 in.close();
//        		 out.close();
//        		 this.clientSocket.close();
//        		 System.out.println("Conexiunea a fost intrerupta!");
//        	 }catch(IOException e){
//        		 System.out.println("Eroare la inchiderea conexiunii cu clientul!");
//        	 }
        }   
	}
}
