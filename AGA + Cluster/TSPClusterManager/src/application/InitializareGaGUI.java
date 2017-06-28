package application;

import java.io.IOException;


import java.io.InputStream;
import java.io.OutputStream;

import org.json.simple.JSONObject;

import utils.Helpful;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class InitializareGaGUI {
	private Stage stage;
	private Text actiontarget;
	private boolean flagParametrii = false;
	private JSONObject traseu;
	private InputStream in;
	private OutputStream out;
	
	int populationSizeT;
	int mutationRateT;
	int tournamentSizeT;
	boolean elitismT;
	int epochsT;
	int stopCondition;
	
	InitializareGaGUI(Stage stage, JSONObject traseu, InputStream in, OutputStream out){
		this.stage = stage;
		this.traseu = traseu;
		this.in = in;
		this.out = out;
	}

	public void setSecondScene(){
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 700, 500);
		scene.getStylesheets().add(
				GraphicUserInterface.class.getResource("application.css")
						.toExternalForm());

		Text sceneTitle = new Text("Cluster initialization for solve TSP");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox hBoxTitle = new HBox(10);
		hBoxTitle.getChildren().add(sceneTitle);
		hBoxTitle.setAlignment(Pos.CENTER);
		grid.add(hBoxTitle, 0, 0, 3, 1);

		Label popSize = new Label("Population size:");
		grid.add(popSize, 0, 3);
		TextField popSizeField = new TextField();
		grid.add(popSizeField, 1, 3);
		Label popSizeEX = new Label("* (number)");
		grid.add(popSizeEX, 2, 3);
		
		
		Label mutationRate = new Label("Mutation rate:");
		grid.add(mutationRate, 0, 4);
		TextField mutationRateField = new TextField();
		grid.add(mutationRateField, 1, 4);
		Label mutationRateEX = new Label("* (number)");
		grid.add(mutationRateEX, 2, 4);
		
		Label tournamentSize = new Label("Tournament size:");
		grid.add(tournamentSize, 0, 5);
		TextField tournamentSizeField = new TextField();
		grid.add(tournamentSizeField, 1, 5);
		Label tournamentSizeEX = new Label("* (number):");
		grid.add(tournamentSizeEX, 2, 5);
		
		Label bestIndividul = new Label("Elitism:");
		grid.add(bestIndividul, 0, 6);
		ListView<String> bestIndividulField = new ListView<String>();
		ObservableList<String> items = FXCollections.observableArrayList ("True", "False");
		bestIndividulField.setItems(items);
		bestIndividulField.setPrefWidth(100);
		bestIndividulField.setPrefHeight(30);
		grid.add(bestIndividulField, 1, 6);
		Label bestIndividulEX = new Label("* (true/false)");
		grid.add(bestIndividulEX, 2, 6);
		
		Label numberOfGeneration = new Label("Number of generation:");
		grid.add(numberOfGeneration, 0, 7);
		TextField numberOfGenerationField = new TextField();
		grid.add(numberOfGenerationField, 1, 7);
		Label numberOfGenerationEX = new Label("* (number)");
		grid.add(numberOfGenerationEX, 2, 7);
		
		Label stopCondition = new Label("Stop condition:");
		grid.add(stopCondition, 0, 8);
		TextField stopConditionField = new TextField();
		grid.add( stopConditionField, 1, 8);
		Label  stopConditionEX = new Label("* (number)");
		grid.add( stopConditionEX, 2, 8);
		
		HBox hbBtn = new HBox(10);
		Button initialization = new Button("Initializare");
		hbBtn.getChildren().add(initialization);
		hbBtn.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtn, 0, 11, 3, 1);
		
		Button next = new Button("Urmatorul pas");
		HBox hbBtSolve = new HBox(10);
		hbBtSolve.getChildren().add(next);
		hbBtSolve.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtSolve, 0, 13, 3, 1);
		
		HBox hBoxAT = new HBox();
		actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		hBoxAT.getChildren().add(actiontarget );
		actiontarget.setText("");
		hBoxAT.setAlignment(Pos.CENTER);
		grid.add(hBoxAT, 0, 14, 3,1);
		
		initialization.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0){
				String populationSize = popSizeField.getText();
				String mutationRate = mutationRateField.getText();
				String tournamentSize = tournamentSizeField.getText();
				int elitism = bestIndividulField.getSelectionModel().getSelectedIndex();
				String epochs = numberOfGenerationField.getText();
				String stopCond = stopConditionField.getText();
				
					if(checkFieldsAreCompleted(populationSize, mutationRate, tournamentSize, epochs, stopCond, elitism))
						if(checkFieldsAreNumber(populationSize, mutationRate, tournamentSize, epochs, stopCond)){
							actiontarget.setText("OK");
							setFlagParametrii(true);
							setParametrii(populationSize, mutationRate, tournamentSize, elitism, epochs, stopCond);
						}else{
							setFlagParametrii(false);
							actiontarget.setText("Parametrii nu au fost setati corect!");
						}
				}
		});
		
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0){
				if(getFlagParametrii() == true){
					System.out.println("OK, trimit parametrii");
					trimiteParametrii();
				}
			}
		});
		
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println("[STOP]Am inchis din interfata Initializare!!!");
				JSONObject json  =  new JSONObject();
				json.put("cluster_orchestration_functions", 500);
				try {
					Helpful.write(out, json.toJSONString());
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("[InitializareGUI ERROR]Eroare la inchidere");
				}
			}
		});
		
		this.stage.setScene(scene);
	}
	
	public boolean checkFieldsAreNumber(String populationSize, String mutationRate, String tournamentSize, String epochs, String stopCond){
		try{
			Integer.parseInt(populationSize);
		}catch(NumberFormatException e){
			actiontarget.setText("Population size must be numeric!!!");
			return false;
		}
		
		try{
			Integer.parseInt(mutationRate);
		}catch(NumberFormatException e){
				actiontarget.setText("Mutation rate must be numeric!!!");
			return false;
		}
		
		try{
			Integer.parseInt(tournamentSize);
		}catch(NumberFormatException e){
			actiontarget.setText("Tournament size must be numeric!!!");
			return false;
		}
		
		try{
			Integer.parseInt(epochs);
		}catch(NumberFormatException e){
			actiontarget.setText("Number of generation must be numeric!!!");
			return false;
		}
		
		try{
			Integer.parseInt(stopCond);
		}catch(NumberFormatException e){
			actiontarget.setText("Stop condition must be numeric!!!");
			return false;
		}
		
		if(Integer.parseInt(mutationRate) < 0 ||  Integer.parseInt(mutationRate) > (Integer.parseInt(populationSize) / 2)){
			actiontarget.setText("Mutation rate must be between 0 and populationSize / 2!!!");
			return false;
		}
		
		if(Integer.parseInt(populationSize) <= 0){
			actiontarget.setText("Population size must be greater that 0!!!");
			return false;
		}
		
		if(Integer.parseInt(tournamentSize) <= 0){
			actiontarget.setText("Tournament size must be greater that 0!!!");
			return false;
		}
		
		if(Integer.parseInt(epochs) <= 0){
			actiontarget.setText("Number of generation must be greater that 0!!!");
			return false;
		}
		return true;
	}

	public boolean checkFieldsAreCompleted(String populationSize, String mutationRate, String tournamentSize, String epochs, String stopCond, int elitism){
		int ok = 0;
		if(populationSize.length() == 0 ){
			actiontarget.setText("Population size field is necessary!!!");
			ok = 1;
		}
		if(mutationRate.length() == 0 ){
			actiontarget.setText("Mutation field is necessary!!!");
			ok = 1;
		}
		if(tournamentSize.length() == 0 ){
			actiontarget.setText("Tournament size field is necessary!!!");
			ok = 1;
		}
		if(epochs.length() == 0 ){
			actiontarget.setText("Number of generation field is necessary!!!");
			ok = 1;
		}
		if(stopCond.length() == 0 ){
			actiontarget.setText("Stop condition field is necessary!!!");
			ok = 1;
		}
		if(elitism != 0 && elitism != 1){
			actiontarget.setText("Elitism selection is necessary!!!");
			ok = 1;
		}
		if(ok == 1)
			return false;
		return true;
	}
	
	public void setFlagParametrii(boolean flag){
		this.flagParametrii = flag;
	}
	
	public boolean getFlagParametrii(){
		return this.flagParametrii;
	}
		
	public void setParametrii(String popSize, String mutation, String turn, int elitism, String epochs, String stopCond){
		this.populationSizeT = Integer.parseInt(popSize);
		this.mutationRateT = Integer.parseInt(mutation);
		this.tournamentSizeT = Integer.parseInt(turn);
		if(elitism == 1)
			this.elitismT = true;
		else
			this.elitismT = false;
		this.epochsT = Integer.parseInt(epochs);
		this.stopCondition = Integer.parseInt(stopCond);
	}
	
	public JSONObject getJsonDupaParametrii(){
		JSONObject paramJsonObject = new JSONObject();
		paramJsonObject.put("populationSize", this.populationSizeT);
		paramJsonObject.put("mutation", this.mutationRateT);
		paramJsonObject.put("tournamentSize", this.tournamentSizeT);
		paramJsonObject.put("elitism", this.elitismT);
		paramJsonObject.put("epochs", this.epochsT);
		paramJsonObject.put("stopCond", this.stopCondition);
		return paramJsonObject;
	}
	
	public JSONObject getTraseu(){
		return this.traseu;
	}
	
	public boolean trimiteParametrii(){
		try {
			JSONObject json = getJsonDupaParametrii();
			json.put("cluster_orchestration_functions", 1);
			json.put("function_code", 3);
			Helpful.write(this.out, json.toJSONString());
			String rezultat = Helpful.read(this.in);
			json = Helpful.stringToJsonObject(rezultat);
			System.out.println("[Initializare]" + Integer.parseInt(json.get("msg").toString()));
			if(Integer.parseInt(json.get("msg").toString()) == 1){
//				rezultat = Helpful.read(ConversatieAlgoritm.in);
//				json = Helpful.stringToJsonObject(rezultat);
//				
//				System.out.println("Json"+json.toJSONString());
//				(new Thread(new VizualizareTraseuGUI(json, Integer.parseInt(json.get("dimensiune").toString())))).start();
				RezolvareProblemaGUI rez = new RezolvareProblemaGUI(stage, getTraseu(), this.in, this.out, this.epochsT);
				rez.setFinalScene();
			}else{
				actiontarget.setText("Parametrii nu au putut fi setati.");
			}
		} catch (IOException e) {
			actiontarget.setText("[ER]Parametrii nu au putut fi setati.");
			System.out.println("[Eroare]Eroare la trimiterea parametrilor");
		}
		return true;
	}
}
