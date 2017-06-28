package Connection;

import Management.ConnectToCluster;

public class RestoreConnections extends Thread{
	public static int contorReset = 0;
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(1000);
				System.out.println("Resetez conexiunile:" + contorReset);
				contorReset ++;
				if(Management.ConnectToCluster.flag == false && contorReset >= 300){
					Management.ClusterOrchestration.resetConnection();
					contorReset = 0;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
