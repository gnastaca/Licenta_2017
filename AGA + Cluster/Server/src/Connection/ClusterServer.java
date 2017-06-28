package Connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.omg.CORBA.SystemException;

import Management.Helpful;
/**
 * 
 * @author Geo
 *Se initializeaza si se porneste serverul
 *Pentru fiecare client conectat(subpopulatie) se creaza un nou thread
 */
public class ClusterServer{
	//Socketul serverului
	ServerSocket serverSocket;
	public static boolean serverInfo;
	//Numarul de calculatoare conectate la cluster
	int clients = 0;
	//Se pastreaza conexiunile cu fiecare calculator (brodcast)
	public static Map<Integer, Socket> pcOfCluster = new ConcurrentHashMap<Integer, Socket>();
	public static ArrayList<Socket> nodes = new ArrayList<Socket>();
	//Se creaza o lista cu ip-urile calculatoarelor
	public static ArrayList<String>listOfIP = new ArrayList<String>();
	public static ArrayList<Integer>listOfPort = new ArrayList<Integer>();
	
	//Constructorul serverului
	public ClusterServer(){
		try {
			//Se creaza socketul
			this.serverSocket = new ServerSocket(7777, 5);
			//Acest flag spune daca s-a conectat vreun manager de cluster
			this.serverInfo = true;
		} catch (IOException e) {
			System.out.println("[SERVER Cluster]Am creat socketul ce ascult la portul 7777");
			System.exit(-1);
		}	
		
		System.out.println("-------SERVER CLUSTER ON-------");
		String clientName;
		//primim calculatoare cat timp nu s-a conectat nici un manager
		while(this.serverInfo){
			try {
				Socket clientSocket = this.serverSocket.accept();
				//obtinem adresa ip a nodului care s-a conectat
				String ip = clientSocket.getInetAddress().getHostAddress().toString();
				String data = Helpful.read(clientSocket.getInputStream());
				System.out.println(data);
				JSONObject json =  Helpful.stringToJsonObject(data);
				
				//Adaugm ip-ul la lista
				if(listOfIP.contains(ip) == false)
					listOfIP.add(ip);
				int  port = Integer.parseInt(json.get("port").toString());
				if(listOfPort.contains(port) == false)
					listOfPort.add(port);
				System.out.println("Port:" +port);
				System.out.println("ip:" + ip);
				System.out.println("Accept!!!");
				
				//Adaugam nodul la cluster
				if(this.pcOfCluster.containsKey(port) == false){
					//Se verifica conexiunea si se seteaza numele
					//ii oferim un nume noului nod din cluster
					clientName = "Client_" + this.clients;
					this.clients++;
					ConnectPcToCluster client =  new ConnectPcToCluster(clientSocket, clientName);
					client.start();
				}
				this.nodes.add(clientSocket);
				this.pcOfCluster.put(port, clientSocket);
			} catch (IOException e) {
				System.out.println("[SERVER Cluster]Eroare la crearea conexiunii cu nodul!");
				System.exit(-2);
			}
		}
	}
}