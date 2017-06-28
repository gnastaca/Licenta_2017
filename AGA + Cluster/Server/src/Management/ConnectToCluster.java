package Management;

import java.io.IOException;
import java.io.OutputStream;





import javax.net.ssl.*;
import java.net.ServerSocket;
import java.io.*;
import java.security.*;

import org.json.simple.JSONObject;

import Connection.ClusterServer;

public class ConnectToCluster extends Thread{
	static public boolean flag = false;
	
	public void run(){
		System.out.println("[SERVER Orchestrare] Astept sa se conecteze utilizatorul!");
		try {
			//Se ceraza un socket prin care se stabileste legatura cu managerul de cluster
			System.setProperty("javax.net.ssl.keyStore", "Serverkey");
			System.setProperty("javax.net.ssl.keyStorePassword", "12345678");
			SSLServerSocket sslServerSocket = (SSLServerSocket)((SSLServerSocketFactory)SSLServerSocketFactory.getDefault()).createServerSocket(8888);
			while(true){
				SSLSocket clientSocket = (SSLSocket)sslServerSocket.accept();
				
				//Daca exista deja un manager anuntam faptul ca nu mai este posibil sa fie condus de altcineva
				if(flag == false){
					//Se ofera orchestrarea clusterului
					ClusterOrchestration co = new ClusterOrchestration(clientSocket);
					co.start();
				}else{
					JSONObject json = new JSONObject();
					json.put("function_code", 0);
					json.put("msg", 0);
					OutputStream out = clientSocket.getOutputStream();
					Helpful.write(out, json.toJSONString());
				}
				System.out.println("[SERVER Orchestrare]S-a conectat un utilizator");
				
			}
		} catch (IOException e) {
			System.out.println("[ConnectToCluster]Eroare la conectare");
		}
	}

}
