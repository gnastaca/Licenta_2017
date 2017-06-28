package application;
	
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;

import org.json.simple.JSONObject;

import utils.Helpful;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class ClusterManager extends Application {
	private SSLSocket socket;
	private InputStream in;
	private OutputStream out;
	private boolean inint = false;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			ClusterManager clusterManager =  new ClusterManager();
			primaryStage.setTitle("TSP Genetic Algorithm Solve with Parallel Processing");
			primaryStage.getIcons().add(new Image("file:src\\application\\logo_fii3.png"));
			ConnectScene cn = new ConnectScene(primaryStage, this.socket);
			primaryStage.setScene(cn.getInitialScene());
			primaryStage.show();
			
			
			GraphicUserInterface.initialization.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0){
					if(clusterManager.inint == false){
					String populationSize = GraphicUserInterface.popSizeField.getText();
					String mutationRate = GraphicUserInterface.mutationRateField.getText();
					String tournamentSize = GraphicUserInterface.tournamentSizeField.getText();
					int elitism = GraphicUserInterface.bestIndividulField.getSelectionModel().getSelectedIndex();
					String epochs = GraphicUserInterface.numberOfGenerationField.getText();
					
					if(clusterManager.checkFieldsAreCompleted(populationSize, mutationRate, tournamentSize, epochs, elitism))
						if(clusterManager.checkFieldsAreNumber(populationSize, mutationRate, tournamentSize, epochs)){
							GraphicUserInterface.actiontarget.setText("");
							JSONObject json = new JSONObject();
							json.put("cluster_orchestration_functions", 1);
							json.put("function_code", 100);
							json.put("msg", "");
							
			
							try {
								Helpful.write(clusterManager.out, json.toJSONString());
								String str  = Helpful.read(clusterManager.in);
								json = Helpful.stringToJsonObject(str);
								if(json.get("msg").toString().equals("OK")){
									System.out.println("Toate conexiunile sunt pornite!!!");
									if(clusterManager.sendInitGA(populationSize, mutationRate, tournamentSize, elitism, epochs)){
										GraphicUserInterface.actiontarget.setText("Clusterul a fost initializat cu succes!!!");
										clusterManager.inint = true;
									}
									else
										GraphicUserInterface.actiontarget.setText("Clusterul nu a fost initializat!!!");
								}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else{
						GraphicUserInterface.actiontarget.setText("Clusterul a fost initializat deja!!!");
					}
				}
			});
			
			GraphicUserInterface.startSolve.setOnAction(new EventHandler<ActionEvent>() {			
				@Override
				public void handle(ActionEvent arg0) {
					if(clusterManager.inint == false)
						GraphicUserInterface.actiontarget.setText("Clusterul nu a fost initializat inca!!!");
					else{
						JSONObject json = new JSONObject();
						json.put("function_code",4);
						json.put("cluster_orchestration_functions", 1);
						json.put("msg", "Start genetic algorithm!!!");
						try {
							Helpful.write(clusterManager.out, json.toJSONString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkIfIamClusterBoss(JSONObject json){
		if(Integer.parseInt(json.get("msg").toString()) == 0)
			return false;
		return true;
	}
	
	public boolean checkFieldsAreNumber(String populationSize, String mutationRate, String tournamentSize, String epochs){
		try{
			Integer.parseInt(populationSize);
		}catch(NumberFormatException e){
			GraphicUserInterface.actiontarget.setText("Population size must be numeric!!!");
			return false;
		}
		
		try{
			Double.parseDouble(mutationRate);
		}catch(NumberFormatException e){
			GraphicUserInterface.actiontarget.setText("Mutation rate must be floate!!!");
			return false;
		}
		
		try{
			Integer.parseInt(tournamentSize);
		}catch(NumberFormatException e){
			GraphicUserInterface.actiontarget.setText("Tournament size must be numeric!!!");
			return false;
		}
		
		try{
			Integer.parseInt(epochs);
		}catch(NumberFormatException e){
			GraphicUserInterface.actiontarget.setText("Number of generation must be numeric!!!");
			return false;
		}
		
		if(Double.parseDouble(mutationRate) < 0 || Double.parseDouble(mutationRate)  > 1){
			GraphicUserInterface.actiontarget.setText("Mutation rate must be between 0 and 1!!!");
			return false;
		}
		
		if(Integer.parseInt(populationSize) <= 0){
			GraphicUserInterface.actiontarget.setText("Population size must be greater that 0!!!");
			return false;
		}
		
		if(Integer.parseInt(tournamentSize) <= 0){
			GraphicUserInterface.actiontarget.setText("Tournament size must be greater that 0!!!");
			return false;
		}
		
		if(Integer.parseInt(epochs) <= 0){
			GraphicUserInterface.actiontarget.setText("Number of generation must be greater that 0!!!");
			return false;
		}
		return true;
	}
	
	public boolean checkFieldsAreCompleted(String populationSize, String mutationRate, String tournamentSize, String epochs, int elitism){
		int ok = 0;
		if(populationSize.length() == 0 ){
			GraphicUserInterface.actiontarget.setText("Population size field is necessary!!!");
			ok = 1;
		}
		if(mutationRate.length() == 0 ){
			GraphicUserInterface.actiontarget.setText("Mutation field is necessary!!!");
			ok = 1;
		}
		if(tournamentSize.length() == 0 ){
			GraphicUserInterface.actiontarget.setText("Tournament size field is necessary!!!");
			ok = 1;
		}
		if(epochs.length() == 0 ){
			GraphicUserInterface.actiontarget.setText("Number of generation field is necessary!!!");
			ok = 1;
		}
		if(elitism != 0 && elitism != 1){
			GraphicUserInterface.actiontarget.setText("Elitism selection is necessary!!!");
			ok = 1;
		}
		if(ok == 1)
			return false;
		return true;
	}
	
	public boolean sendInitGA(String populationSize, String mutationRate, String tournamentSize, int elitism, String epochs){
		JSONObject json = new JSONObject();
		json.put("populationSize", Integer.parseInt(populationSize));
		json.put("mutationRate", Double.parseDouble(mutationRate));
		json.put("tournamentSize", Integer.parseInt(tournamentSize));
		json.put("elitism", elitism);
		json.put("epochs", Integer.parseInt(epochs));
		json.put("cluster_orchestration_functions", 1);
		json.put("function_code", 3);
		json.put("msg", "");
		
		try {
			Helpful.write(out, json.toJSONString());
			String str  = Helpful.read(in);
			json = Helpful.stringToJsonObject(str);
			if(json.get("msg").toString().equals("OK")){
				System.out.println("Toate calculatoarele au fost initializate cu succes!");
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
