package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.json.simple.JSONObject;

import utils.Helpful;

public class LoadMap {
	private Stage stage;
	private File file = null;
	private JSONObject traseu = null;
	private int nrOrase = 0;
	
	public LoadMap(Stage st) {
		this.stage = st;
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

		Text sceneTitle = new Text("Load Map");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox hBoxTitle = new HBox(10);
		hBoxTitle.getChildren().add(sceneTitle);
		hBoxTitle.setAlignment(Pos.CENTER);
		grid.add(hBoxTitle, 0, 0, 2, 1);

		Text sceneSubtitle = new Text("Load map from file");
		sceneSubtitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 11));
		HBox hBoxSubtitle = new HBox(40);
		hBoxSubtitle.getChildren().add(sceneSubtitle);
		hBoxSubtitle.setAlignment(Pos.TOP_CENTER);
		grid.add(hBoxSubtitle, 0, 1, 2, 1);

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select map file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("File", "*.txt"));
		
		Button btn1 = new Button("Select file");
		HBox hbBtn = new HBox(20);
		hbBtn.getChildren().add(btn1);
		hbBtn.setAlignment(Pos.TOP_CENTER);
		grid.add(hbBtn, 0, 5, 2, 1);
		
		Button btn2 = new Button("Load file");
		HBox hbbtn2 = new HBox(100);
		hbbtn2.setMinWidth(60);
		hbbtn2.getChildren().add(btn2);
		hbbtn2.setAlignment(Pos.TOP_CENTER);
		grid.add(hbbtn2, 0, 6, 2, 1);
		
		Text actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		HBox hbActionTarget = new HBox(10);
		hbActionTarget.getChildren().add(actiontarget);
		hbActionTarget.setAlignment(Pos.BOTTOM_CENTER);
        grid.add(hbActionTarget, 0, 9, 2, 1);
        
		Button vizualizare = new Button("Vizualizare traseu incarcat");
		HBox hbVizual = new HBox(10);
		hbVizual.getChildren().add(vizualizare);
		hbVizual.setAlignment(Pos.TOP_CENTER);
		grid.add(hbVizual, 0, 7, 2, 1);
		
		
		Button next = new Button("Pasul urmator");
		HBox hbNext = new HBox(10);
		hbNext.getChildren().add(next);
		hbNext.setAlignment(Pos.TOP_CENTER);
		grid.add(hbNext, 0, 11, 2, 1);
        
		btn1.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
                file = fileChooser.showOpenDialog(stage);
                if(file != null)
                	actiontarget.setText("Fisierul a fost selectat!");
                else
                	actiontarget.setText("Fisierul nu a fost selectat!");
			}
		});
		
		btn2.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				if(file == null){
					actiontarget.setText("Fisierul nu a fost selectat inca!");
				}else{
					FileInputStream fis;
					boolean flag = true;
					try {
						fis = new FileInputStream(file);
						//Construct BufferedReader from InputStreamReader
						BufferedReader br = new BufferedReader(new InputStreamReader(fis));
						String line = null;
						double x, y;
						
						traseu = new JSONObject();
						while ((line = br.readLine()) != null) {
							String [] coordinate = line.split(" ");
							if(coordinate.length != 2){
								actiontarget.setText("Fisierul incarcat contine date gresite!");
								flag = false;
								traseu = null;
								nrOrase = 0;
								break;
							}
							
							x = Double.parseDouble(coordinate[0]);
							y = Double.parseDouble(coordinate[1]);
							
							JSONObject oras = new JSONObject();
							oras.put("cx", x);
							oras.put("cy", y);
							traseu.put(nrOrase, oras);
							nrOrase++;
						}
					 
						br.close();
					} catch (FileNotFoundException e) {
						actiontarget.setText("Fisierul selectat nu a fost gasit");
						traseu = null;
						nrOrase = 0;
						flag = false;
					} catch (IOException e) {
						traseu = null;
						nrOrase = 0;
						actiontarget.setText("Eroare la citirea fisierului selectat!");
						flag = false;
					} catch (NumberFormatException e){
						traseu = null;
						nrOrase = 0;
						actiontarget.setText("Fisierul contine caractere necorespunzatoare!");
						flag = false;
					}
					if(flag == true){
						actiontarget.setText("Fisierul a fot procesat cu succes!");
					}
				}
			}
		});
		
		vizualizare.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				if(startVizualizareTraseu() == false)
					actiontarget.setText("Nu exista traseu care sa fie vizualizat.");
			}
		});
		
		next.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				if(trimiteTraseuCatreAlgoritm() == true){
					System.out.println("Trebuie sa afisez interfata de setare a parametrilor");
					//afisare interfata pentru parametrii
					initializareGUI2();	
				}else{
					actiontarget.setText("Traseul nu a putut fi setat!");
				}
			}
		});
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println("[STOP]Am inchis din interfata selectare metoda input!!!");
				JSONObject json  =  new JSONObject();
				json.put("cluster_orchestration_functions", 500);
				try {
					Helpful.write(ConversatieAlgoritm.out, json.toJSONString());
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("[TraseuGUI ERROR]Eroare la inchidere");
				}
			}
		});
        
		
		this.stage.setScene(scene);
	}
	
	public boolean startVizualizareTraseu(){
		if(traseu != null){
			(new Thread(new VizualizareTraseuGUI(this.traseu, this.nrOrase, 1))).start();
			return true;
		}
		return false;	
	}
	
	public boolean trimiteTraseuCatreAlgoritm(){
		try {
			//dimensiunea traseului
			this.traseu.put("dimensiune" ,this.nrOrase);
			//functia din nod care se apeleaza 
			this.traseu.put("function_code" , 2);
			//functia care se apeleaza din cluster manager
			this.traseu.put("cluster_orchestration_functions", 1);
			Helpful.write(ConversatieAlgoritm.out, this.traseu.toJSONString());
			String data = Helpful.read(ConversatieAlgoritm.in);
			JSONObject json = Helpful.stringToJsonObject(data);
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
}
