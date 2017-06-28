package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		//ConversatieAlgoritm CA = new ConversatieAlgoritm("13.88.177.239", 7777);
	
		try {
			primaryStage.setTitle("TSP Genetic Algorithm");
			primaryStage.getIcons().add(new Image("file:src\\application\\logo_fii3.png"));
			ConnectScene cs = new ConnectScene(primaryStage);
			primaryStage.setScene(cs.getInitialScene());
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
