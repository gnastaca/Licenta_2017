package application;

import javax.net.ssl.*;

import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.io.*;
import java.security.*;

import org.json.simple.JSONObject;

import utils.Helpful;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ConnectScene {
	Stage stage;
	SSLSocket socket;
	InputStream in;
	Text actiontarget = new Text();
	OutputStream out;
	
	public ConnectScene(Stage stage, SSLSocket socket){
		this.stage = stage;
		this.socket = socket;
	}

	public Scene getInitialScene() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 400, 400);
		scene.getStylesheets().add(
				GraphicUserInterface.class.getResource("application.css")
						.toExternalForm());

		Text sceneTitle = new Text("TSP Cluster Manager");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox hBoxTitle = new HBox(10);
		hBoxTitle.getChildren().add(sceneTitle);
		hBoxTitle.setAlignment(Pos.CENTER);
		grid.add(hBoxTitle, 0, 0, 2, 1);

		Text sceneSubtitle = new Text("Connect to cluster server");
		sceneSubtitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 11));
		HBox hBoxSubtitle = new HBox(10);
		hBoxSubtitle.getChildren().add(sceneSubtitle);
		hBoxSubtitle.setAlignment(Pos.TOP_CENTER);
		grid.add(hBoxSubtitle, 0, 1, 2, 1);

		Label ip = new Label("Server ip:");
		grid.add(ip, 0, 5);
		TextField ipTextField = new TextField();
		grid.add(ipTextField, 1, 5);

		Label port = new Label("Server port:");
		grid.add(port, 0, 6);
		TextField portField = new TextField();
		grid.add(portField, 1, 6);

		Button btn = new Button("Connect to server");
		HBox hbBtn = new HBox(10);
		hbBtn.getChildren().add(btn);
		hbBtn.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtn, 0, 9, 2, 1);
		
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		HBox hbActionTarget = new HBox(10);
		hbActionTarget.getChildren().add(actiontarget);
		hbActionTarget.setAlignment(Pos.BOTTOM_CENTER);
        grid.add(hbActionTarget, 0, 10, 2, 1);
        
        
        //Butonul se activeaza la conectare
		btn.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				String ip = ipTextField.getText();
				String port = portField.getText();
				//daca conectarea a reusit se genereaza urmatoarea interfata
				if(connectToClusterServer(port, ip) == true){
					SelectInputTypeScene inScene = new SelectInputTypeScene(stage, in, out);
					inScene.setScene();
				}
				
			}
		});
		
		return scene;
	}
	
	public boolean connectToClusterServer(String port, String ip){
		try {
			int p =  Integer.parseInt(port);
			System.setProperty("javax.net.ssl.trustStore", "Serverkey");
			System.setProperty("javax.net.ssl.trustStorePassword", "12345678");

			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
					.getDefault();
			this.socket = (SSLSocket) sslsocketfactory.createSocket(
					ip, Integer.parseInt(port));
			this.socket.setKeepAlive(true);
			this.in = this.socket.getInputStream();
			this.out = this.socket.getOutputStream();
			JSONObject json =  new JSONObject();
			do{
				String data = Helpful.read(in);
				System.out.println(data);
				json = Helpful.stringToJsonObject(data);
				if(Integer.parseInt(json.get("function_code").toString()) == 1){
					this.actiontarget.setText("Se reseteaza conexiunile!!!");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}while(Integer.parseInt(json.get("function_code").toString()) != 0);
			
			if(Integer.parseInt(json.get("function_code").toString()) == 0)
				if (this.checkIfIamClusterBoss(json) == false){
					this.actiontarget.setText("Clusterul este deja in functiune!!!");
					return false;
				}
				else{
					return true;
				}
			
		} catch (ConnectException e) {
				actiontarget.setText(e.getMessage());
		} catch (UnknownHostException e) {
			actiontarget.setText("Unknown host exception:" + e.getMessage());
		} catch (IOException e) {
			actiontarget.setText(e.getMessage());
		} catch (NumberFormatException e) {
			actiontarget.setText("[PORT]" + "Contine doar cifre!");
		}
		return false;
	}
	
	public boolean checkIfIamClusterBoss(JSONObject json){
		if(Integer.parseInt(json.get("msg").toString()) == 0)
			return false;
		return true;
	}
}
