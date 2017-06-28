package Connection;

import Management.ConnectToCluster;
import Population.Map;

/**
 * 
 * @author Geo
 * Se initializeaza Serverul
 * Serverul are rolul de a coordona subpopulatiile
 */
public class Main {
	public static void main(String[] args) {
		//Aici se creaza un thread care asteapta sa se conecteze cluster managerul
		ConnectToCluster oc = new ConnectToCluster();
		oc.start();
		RestoreConnections rc = new RestoreConnections();
		rc.start();
		
		//Aici se creaza un server care asteapta conexiuni
		//Serverul care gestioneaza clusterul
		ClusterServer server = new ClusterServer();
	}
}
