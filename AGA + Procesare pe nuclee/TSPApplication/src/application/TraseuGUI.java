package application;

import java.io.IOException;
import java.util.Random;

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

public class TraseuGUI {
	private int nrOrase;
	private Text actiontarget;
	private Stage stage;
	private JSONObject traseu;
	TraseuGUI(Stage stage){
		this.stage = stage;
		this.traseu = new JSONObject();
	}
	
	public void setInitialScene() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 400, 400);
		scene.getStylesheets().add(
				GraphicUserInterface.class.getResource("application.css")
						.toExternalForm());

		Text sceneTitle = new Text("Generare Dataset");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox hBoxTitle = new HBox(10);
		hBoxTitle.getChildren().add(sceneTitle);
		hBoxTitle.setAlignment(Pos.CENTER);
		grid.add(hBoxTitle, 0, 0, 2, 1);

		Text sceneSubtitle = new Text("Detectarea de traseu minim");
		sceneSubtitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 11));
		HBox hBoxSubtitle = new HBox(10);
		hBoxSubtitle.getChildren().add(sceneSubtitle);
		hBoxSubtitle.setAlignment(Pos.TOP_CENTER);
		grid.add(hBoxSubtitle, 0, 1, 2, 1);

		Label ip = new Label("Numar orase:");
		grid.add(ip, 0, 5);
		TextField orase = new TextField();
		grid.add(orase, 1, 5);
		
		Button btn = new Button("Generare dataset");
		HBox hbBtn = new HBox(10);
		hbBtn.getChildren().add(btn);
		hbBtn.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtn, 0, 7, 2, 1);
		
		Button vizualizare = new Button("Vizualizare traseu generat");
		HBox hbVizual = new HBox(10);
		hbVizual.getChildren().add(vizualizare);
		hbVizual.setAlignment(Pos.TOP_CENTER);
		grid.add(hbVizual, 0, 8, 2, 1);
		
		Button next = new Button("Pasul urmator");
		HBox hbNext = new HBox(10);
		hbNext.getChildren().add(next);
		hbNext.setAlignment(Pos.TOP_CENTER);
		grid.add(hbNext, 0, 11, 2, 1);
		
		actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		HBox hbActionTarget = new HBox(10);
		hbActionTarget.getChildren().add(actiontarget);
		hbActionTarget.setAlignment(Pos.BOTTOM_CENTER);
        grid.add(hbActionTarget, 0, 10, 2, 1);
        
		btn.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				String nr = orase.getText();
				generareTraseu(Integer.parseInt(nr));
				actiontarget.setText("Traseul a fost generat!");
			}
		});
		
		vizualizare.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				startVizualizareTraseu();
			}
		});
		
		next.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				if(trimiteTraseuCatreAlgoritm() == true){
					initializareGUI2();	
				}
			}
		});
        
		this.stage.setScene(scene);
	}
	public boolean trimiteTraseuCatreAlgoritm(){
		try {
			this.traseu.put("dimensiune" ,this.nrOrase);
			Helpful.write(ConversatieAlgoritm.out, this.traseu.toJSONString());
			String data = Helpful.read(ConversatieAlgoritm.in);
			JSONObject json = Helpful.stringToJsonObject(data);
			System.out.println("Traseu:" + json.get("code").toString());
			if(Integer.parseInt(json.get("code").toString()) == 200)
				return true;
		} catch (IOException e) {
			System.out.println("[Eroare] Eroare la trimiterea traseului");
		}
		return false;
	}
	public void initializareGUI2(){
		InitializareGaGUI gui2 = new InitializareGaGUI(this.stage, this.traseu);
		gui2.setSecondScene();
	}

	public void startVizualizareTraseu(){
		(new Thread(new VizualizareTraseuGUI(this.traseu, this.nrOrase, 1))).start();
	}
	
	public void generareTraseu(int nr){
		this.nrOrase = nr;
		Random rn = new Random();
		for(int i = 0; i < nr; i++){
			JSONObject oras = new JSONObject();
			oras.put("cx", Math.abs(rn.nextInt() % 300 + 3));
			oras.put("cy", Math.abs(rn.nextInt() % 300 + 3));
			this.traseu.put(i, oras);
		}
		System.out.println(traseu.toJSONString());
	}
}
