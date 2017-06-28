package application;

import java.io.IOException;

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
	public static ProgressBar pb;
	public static ProgressIndicator pin;
	private Stage stage;
	private JSONObject traseu;
	public RezolvareProblemaGUI(Stage stage, JSONObject traseu){
		this.stage = stage;
		this.traseu = traseu;
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
		
		actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		HBox hbActionTarget = new HBox(10);
		hbActionTarget.getChildren().add(actiontarget);
		hbActionTarget.setAlignment(Pos.BOTTOM_CENTER);
        grid.add(hbActionTarget, 0, 10, 2, 1);
        
		pasulInitial.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				JSONObject json = new JSONObject();
				json.put("actiune", 1);
				try {
					Helpful.write(ConversatieAlgoritm.getOuInputStream(), json.toJSONString());
					SelectInputTypeScene input = new SelectInputTypeScene(stage);
					input.setScene();
				} catch (IOException e) {
					System.out.println("[Eroare]Eroare la continuare");
				}
				//actiontarget.setText("Traseul a fost generat!");
			}
		});
		
		startGA.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				JSONObject json = new JSONObject();
				json.put("start", 1);
				try {
					Helpful.write(ConversatieAlgoritm.getOuInputStream(), json.toJSONString());
					String rezultat = Helpful.read(ConversatieAlgoritm.in);
					json = Helpful.stringToJsonObject(rezultat);
					if( Integer.parseInt(json.get("code").toString()) == 200){
						(new Thread(new VizualizareTraseuGUI(getTraseu(), getDimensiuneTraseu(), 2))).start();
					}else{
						System.out.println("[EROARE]A aparut o eroare la start");
					}
				} catch (IOException e) {
					System.out.println("[Eroare]Eroare la rezolvare problema");
				}
			}
		});
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println("[STOP]Am inchis din interfata selectare metoda input!!!");
				JSONObject json  =  new JSONObject();
				json.put("actiune", 0);
				try {
					Helpful.write(ConversatieAlgoritm.getOuInputStream(), json.toJSONString());
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("[RP]Eroare la inchidere");
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
}
