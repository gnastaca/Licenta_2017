package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.simple.JSONObject;

import utils.Helpful;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class RezolvareProblemaGUI {
	public static Text actiontarget;
	public static Text actiontarget2;
	public static ProgressBar pb;
	public static ProgressIndicator pin;
	private Stage stage;
	private JSONObject traseu;
	private InputStream in;
	private OutputStream out;
	private int epochs;
	public RezolvareProblemaGUI(Stage stage, JSONObject traseu, InputStream in, OutputStream out, int ep){
		this.stage = stage;
		this.traseu = traseu;
		this.in = in;
		this.out = out;
		this.epochs = ep;
	}
	
	public void setFinalScene() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 400, 400);
		scene.getStylesheets().add(
				GraphicUserInterface.class.getResource("application.css")
						.toExternalForm());

		Text sceneTitle = new Text("Generare Solutie");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox hBoxTitle = new HBox(10);
		hBoxTitle.getChildren().add(sceneTitle);
		hBoxTitle.setAlignment(Pos.CENTER);
		grid.add(hBoxTitle, 0, 0, 2, 1);

		Text sceneSubtitle = new Text("Start algoritm genetic");
		sceneSubtitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 11));
		HBox hBoxSubtitle = new HBox(10);
		hBoxSubtitle.getChildren().add(sceneSubtitle);
		hBoxSubtitle.setAlignment(Pos.TOP_CENTER);
		grid.add(hBoxSubtitle, 0, 1, 2, 1);
		
		Button startGA = new Button("Start Rezolvare");
		HBox hbVizual = new HBox(10);
		hbVizual.getChildren().add(startGA);
		hbVizual.setAlignment(Pos.TOP_CENTER);
		grid.add(hbVizual, 0, 8, 2, 1);
		
		pb = new ProgressBar();
        pb.setProgress(0.0);
        pin = new ProgressIndicator();
        pin.setProgress(0.0);
        HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(pb, pin);
		
        grid.add(hb, 0, 9, 2, 1);
		Button pasulInitial = new Button("Inapoi la pasul initial");
		HBox hbNext = new HBox(10);
		hbNext.getChildren().add(pasulInitial);
		hbNext.setAlignment(Pos.TOP_CENTER);
		grid.add(hbNext, 0, 11, 2, 1);
		
		Button restart = new Button("Force Restart");
		HBox hbfs = new HBox(10);
		hbfs.getChildren().add(restart);
		hbfs.setAlignment(Pos.TOP_CENTER);
		grid.add(hbfs, 0, 10, 2, 1);
		
		actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		HBox hbActionTarget = new HBox(10);
		hbActionTarget.getChildren().add(actiontarget);
		hbActionTarget.setAlignment(Pos.BOTTOM_CENTER);
        grid.add(hbActionTarget, 0, 12, 2, 1);
		
        actiontarget2 = new Text();
		actiontarget2.setFill(Color.FIREBRICK);
		actiontarget2.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		HBox hbActionTarget2 = new HBox(10);
		hbActionTarget2.getChildren().add(actiontarget2);
		hbActionTarget2.setAlignment(Pos.BOTTOM_CENTER);
        grid.add(hbActionTarget2, 0, 13, 2, 1);
        
		pasulInitial.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				if(StartGA.flag == false){
					JSONObject json = new JSONObject();
					json.put("actiune", 1);
					SelectInputTypeScene inScene = new SelectInputTypeScene(stage, in, out);
					inScene.setScene();
				}else{
					actiontarget.setText("Algotitmul inca lucreaza...");
				}
			}
		});
		
		startGA.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				if(StartGA.flag == false){
					startGeneticAlgorithm();
					actiontarget.setText("Algoritmul ruleaza...");
				}else{
					actiontarget.setText("Algoritmul este deja in actiune...");
				}
			}
		});
		
		restart.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				JSONObject json = new JSONObject();
				json.put("cluster_orchestration_functions", 3);
				json.put("function_code", 8);
				try {
					Helpful.write(out, json.toJSONString());
				}catch(IOException e){
					System.out.println("[Eroare]Eroare la trimitere force restart");
				}
			}
		});
		
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if(StartGA.flag == false){
					System.out.println("[STOP]Am inchis din interfata Rezolvare!!!");
					JSONObject json  =  new JSONObject();
					json.put("cluster_orchestration_functions", 500);
					try {
						Helpful.write(out, json.toJSONString());
					} catch (IOException e) {
						//e.printStackTrace();
						System.out.println("[RezolvareGUI ERROR]Eroare la inchidere");
					}
				}else{
					actiontarget.setText("Algotiymul nu a finalizat...");
				}
			}
		});
		
		
		this.stage.setScene(scene);
	}
	
	public JSONObject getTraseu(){
		return this.traseu;
	}
	
	public int getDimensiuneTraseu(){
		return Integer.parseInt(this.traseu.get("dimensiune").toString());
	}
	
	public void startGeneticAlgorithm(){
		new Thread(new StartGA(this.in, this.out, this.traseu, this.epochs)).start();
	}
}
