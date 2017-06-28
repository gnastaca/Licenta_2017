package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.simple.JSONObject;

import utils.Helpful;

public class StartGA  implements Runnable{
	InputStream in;
	OutputStream out;
	public static boolean flag = false;
	private JSONObject traseu;
	private int epochs;
	
	public StartGA(InputStream i, OutputStream o, JSONObject t, int ep){
		this.in = i;
		this.out = o;
		this.traseu = t;
		this.epochs = ep;
	}
	
	public void run(){
		flag = true;
		JSONObject json = new JSONObject();
		json.put("cluster_orchestration_functions", 1);
		json.put("function_code", 4);
		try {
			long tStart = System.currentTimeMillis();
			Helpful.write(this.out, json.toJSONString());
			int n = 0;
			String rezultat;
			do{
				rezultat = Helpful.read(this.in);
				json = Helpful.stringToJsonObject(rezultat);
				n = Integer.parseInt(json.get("msg").toString());
				if(n > 1){
					float progres = Float.parseFloat(json.get("progres").toString());
					System.out.println("[Progres]"+ progres);
					progres = (float) ((1.0 / this.epochs) * progres);
					RezolvareProblemaGUI.pb.setProgress(progres);
					RezolvareProblemaGUI.pin.setProgress(progres);
				}else{
					RezolvareProblemaGUI.pb.setProgress(1.0);
					RezolvareProblemaGUI.pin.setProgress(1.0);
				}
			}while(n > 1);
			flag = false;
			long tEnd = System.currentTimeMillis();
			long tDelta = tEnd - tStart;
			double elapsedSeconds = tDelta / 1000.0;
			if(n != -1){
				RezolvareProblemaGUI.actiontarget.setText("Astept sa primesc solutia!");
				rezultat = Helpful.read(this.in);
				RezolvareProblemaGUI.actiontarget.setText("Am primit solutia!");
				json = Helpful.stringToJsonObject(rezultat);
				RezolvareProblemaGUI.actiontarget.setText("Am transformat solutia!");
				new Thread(new VizualizareTraseuGUI(json, Integer.parseInt(this.traseu.get("dimensiune").toString()), 1)).start();
				RezolvareProblemaGUI.actiontarget.setText("Algoritmul a finalizat procesarea!");
				RezolvareProblemaGUI.actiontarget.setText("Timpul total de procesare:"+ elapsedSeconds);
				RezolvareProblemaGUI.actiontarget2.setText("Distanta traseu rezultat:"+ Float.parseFloat(json.get("distanta").toString()));
			}
		} catch (IOException e) {
			System.out.println("[Eroare]Eroare la rezolvare problema");
		}
	}

}
