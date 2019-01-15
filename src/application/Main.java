package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
    /**
     * Execution starts here
     * */
    
    @Override
    public void start(Stage primaryStage) {
	/** 
	     * Loads the FXML file for the UI
	     * Opens the window
	     * */
	try {
	    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WistClient.fxml"));
	    Parent root = fxmlLoader.load();
	    Scene scene = new Scene(root);
	    Client client = (Client) fxmlLoader.getController();
	    client.setStage(primaryStage);
	    primaryStage.setScene(scene);
	    primaryStage.setOnCloseRequest(e -> {
		try {
		    client.closeConnection();
		} catch (IOException e1) {
		    e1.printStackTrace();
		} catch (NullPointerException nullPointerException) {

		}
		// primaryStage.close();
	    });
	    primaryStage.show();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	launch(args);
    }
}
