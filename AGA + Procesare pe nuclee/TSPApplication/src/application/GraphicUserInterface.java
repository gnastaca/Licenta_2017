package application;

import java.net.Socket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class GraphicUserInterface {
	public static Button btn;
	public static TextField ipTextField;
	public static TextField portField;
	public static Text actiontarget;
	
	public static TextField popSizeField;
	public static TextField mutationRateField;
	public static TextField tournamentSizeField;
	public static ListView<String>  bestIndividulField;
	public static TextField numberOfGenerationField;
	public static Button initialization = new Button("Genetic Algorithm initialization");
	public static Button startSolve = new Button("Start Genetic Algorithm");
	
	

	public static Scene getInitialScene() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 400, 400);
		scene.getStylesheets().add(
				GraphicUserInterface.class.getResource("application.css")
						.toExternalForm());

		Text sceneTitle = new Text("Cluster Manager");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox hBoxTitle = new HBox(10);
		hBoxTitle.getChildren().add(sceneTitle);
		hBoxTitle.setAlignment(Pos.CENTER);
		grid.add(hBoxTitle, 0, 0, 2, 1);

		Text sceneSubtitle = new Text("Connect to server cluster");
		sceneSubtitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 11));
		HBox hBoxSubtitle = new HBox(10);
		hBoxSubtitle.getChildren().add(sceneSubtitle);
		hBoxSubtitle.setAlignment(Pos.TOP_CENTER);
		grid.add(hBoxSubtitle, 0, 1, 2, 1);

		Label ip = new Label("Server ip:");
		grid.add(ip, 0, 5);
		ipTextField = new TextField();
		grid.add(ipTextField, 1, 5);

		Label port = new Label("Server port:");
		grid.add(port, 0, 6);
		portField = new TextField();
		grid.add(portField, 1, 6);

		btn = new Button("Connect to server");
		HBox hbBtn = new HBox(10);
		hbBtn.getChildren().add(btn);
		hbBtn.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtn, 0, 9, 2, 1);
		
		actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		HBox hbActionTarget = new HBox(10);
		hbActionTarget.getChildren().add(actiontarget);
		hbActionTarget.setAlignment(Pos.BOTTOM_CENTER);
        grid.add(hbActionTarget, 0, 10, 2, 1);
        
        
		return scene;
	}
	
	
	public static Scene getSecondScene(){
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
		popSizeField = new TextField();
		grid.add(popSizeField, 1, 3);
		Label popSizeEX = new Label("* (number)");
		grid.add(popSizeEX, 2, 3);
		
		
		Label mutationRate = new Label("Mutation rate:");
		grid.add(mutationRate, 0, 4);
		mutationRateField = new TextField();
		grid.add(mutationRateField, 1, 4);
		Label mutationRateEX = new Label("* (number [0.0 , 1.0]):");
		grid.add(mutationRateEX, 2, 4);
		
		Label tournamentSize = new Label("Tournament size:");
		grid.add(tournamentSize, 0, 5);
		tournamentSizeField = new TextField();
		grid.add(tournamentSizeField, 1, 5);
		Label tournamentSizeEX = new Label("* (number):");
		grid.add(tournamentSizeEX, 2, 5);
		
		Label bestIndividul = new Label("Elitism:");
		grid.add(bestIndividul, 0, 6);
		bestIndividulField = new ListView<String>();
		ObservableList<String> items = FXCollections.observableArrayList ("True", "False");
		bestIndividulField.setItems(items);
		bestIndividulField.setPrefWidth(100);
		bestIndividulField.setPrefHeight(30);
		grid.add(bestIndividulField, 1, 6);
		Label bestIndividulEX = new Label("* (true/false)");
		grid.add(bestIndividulEX, 2, 6);
		
		Label numberOfGeneration = new Label("Number of generation:");
		grid.add(numberOfGeneration, 0, 7);
		numberOfGenerationField = new TextField();
		grid.add(numberOfGenerationField, 1, 7);
		Label numberOfGenerationEX = new Label("* (number)");
		grid.add(numberOfGenerationEX, 2, 7);
		
		HBox hbBtn = new HBox(10);
		hbBtn.getChildren().add(initialization);
		hbBtn.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtn, 0, 11, 3, 1);
		
		HBox hbBtSolve = new HBox(10);
		hbBtSolve.getChildren().add(startSolve);
		hbBtSolve.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtSolve, 0, 12, 3, 1);
		
		HBox hBoxAT = new HBox();
		hBoxAT.getChildren().add(actiontarget);
		actiontarget.setText("");
		hBoxAT.setAlignment(Pos.CENTER);
		grid.add(hBoxAT, 0, 14, 3,1);
		
		return scene;
	}
}
