package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import utils.Helpful;

public class ConversatieAlgoritm {
	String ip;
	int port;
	
	public static InputStream in;
	public static OutputStream out;
	public static Socket socket;
	
	public ConversatieAlgoritm(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public boolean createConnection() throws UnknownHostException, IOException{
				this.socket = new Socket(this.ip, this.port);
				
				this.in = this.socket.getInputStream();
				this.out = this.socket.getOutputStream();

		
		String info;
		try {
			info = Helpful.read(in);
			JSONObject json = Helpful.stringToJsonObject(info);
			System.out.println(json.get("code").toString());
			if(Integer.parseInt(json.get("code").toString()) == 200)
				return true;
		} catch (IOException e) {
			System.out.println("[Eroare] Eroare la conexiune. se asetpa 200 ok.");
		}
		return false;
	}
	
	static InputStream getInputStream(){
		return in;
	}
	
	static OutputStream getOuInputStream(){
		return out;
	}
}
