package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class Controller implements Initializable{
    @FXML
    protected TextField playerNameField;
    @FXML
    protected TextField serverIPField;
    @FXML
    protected Button connectButton;
    @FXML
    protected HBox cardBox;
    @FXML
    protected HBox enemyCardBox;
    @FXML
    protected HBox placedCardBox;
    @FXML
    protected TabPane tabPane;
    protected String playerName = null;
    
    @FXML
    protected void connectButtonClicked(MouseEvent me) {
	if(playerNameField.getText().length() <= 3) {
	    alert("Player name must be at least 4 characters long!");
	    playerNameField.clear();
	}
    }
    
    protected void alert(final String message) {
	Alert alert = new Alert(AlertType.INFORMATION);
	alert.setHeaderText(null);
	alert.setContentText(message);
	alert.showAndWait();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
	Thread thread = new Thread(new Runnable() {
	    
	    @Override
	    public void run() {
		while(playerName == null) {
		    if(serverIPField.getText().length() == 0 || playerNameField.getText().length() == 0) {
			connectButton.setDisable(true);
		    }else {
			connectButton.setDisable(false);
		    }
		}
	    }
	});
	
	thread.start();
	
    }
}
