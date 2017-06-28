package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import javafx.stage.WindowEvent;

import org.json.simple.JSONObject;

import utils.Helpful;

public class SelectInputTypeScene {
	private Stage stage;
	private InputStream in;
	private OutputStream out;
	
	public SelectInputTypeScene(Stage st, InputStream in, OutputStream out) {
		this.stage = st;
		this.in = in;
		this.out = out;
	}

	public void setScene() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 400, 400);
		scene.getStylesheets().add(
				GraphicUserInterface.class.getResource("application.css")
						.toExternalForm());

		Text sceneTitle = new Text("Select Input Method");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox hBoxTitle = new HBox(10);
		hBoxTitle.getChildren().add(sceneTitle);
		hBoxTitle.setAlignment(Pos.CENTER);
		grid.add(hBoxTitle, 0, 0, 2, 1);

		Text sceneSubtitle = new Text("Chose a method to add input");
		sceneSubtitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 11));
		HBox hBoxSubtitle = new HBox(40);
		hBoxSubtitle.getChildren().add(sceneSubtitle);
		hBoxSubtitle.setAlignment(Pos.TOP_CENTER);
		grid.add(hBoxSubtitle, 0, 1, 2, 1);

		
		Button btn1 = new Button("Generare dataset");
		HBox hbBtn = new HBox(20);
		hbBtn.getChildren().add(btn1);
		hbBtn.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtn, 0, 5, 2, 1);
		
		Button btn2 = new Button("Load citys from file");
		HBox hbVizual = new HBox(100);
		hbVizual.setMinWidth(60);
		hbVizual.getChildren().add(btn2);
		hbVizual.setAlignment(Pos.TOP_CENTER);
		grid.add(hbVizual, 0, 6, 2, 1);
		
		Button btn3 = new Button("Create input");
		HBox hbNext = new HBox(100);
		hbNext.getChildren().add(btn3);
		hbNext.setAlignment(Pos.TOP_CENTER);
		grid.add(hbNext, 0, 7, 2, 1);
		
        
		btn1.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				TraseuGUI tg =  new TraseuGUI(stage, in, out);
				tg.setInitialScene();
			}
		});
		
		btn2.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				LoadMap lM = new LoadMap(stage, in, out);
				lM.setScene();
			}
		});
		
		btn3.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {

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
}
