//By Ahmad Aleiadeh, Kristina Zarudna
package MVC;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SongLib extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/MVC/ui.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		Controller controller = loader.getController(); controller.start();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song Library");
		primaryStage.setResizable(false);  
		primaryStage.show();
		
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
