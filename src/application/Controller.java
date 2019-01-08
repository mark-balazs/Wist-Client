package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Controller {
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
    @FXML
    protected Label playerNameLabel;
    @FXML
    protected Label playerScoreLabel;
    @FXML
    protected Label playerStreakLabel;
    @FXML
    protected Label playerWonLabel;
    @FXML
    protected Label playerPredictionLabel;
    @FXML
    protected Label turnLabel;
    @FXML
    protected TextField answerField;
    @FXML
    protected Label phaseLabel;
    @FXML
    protected Label numberOfCardsLabel;
    @FXML
    protected Label dealerLabel;
    @FXML
    protected SplitPane splitPane;

    protected String playerName = null;
    protected int playerIndex;
    protected String serverIp;

    protected ArrayList<Card> cards;
    protected ArrayList<Card> placedCards;

    protected Stage stage;

    protected void alert(final String message) {
	Platform.runLater(new Runnable() {

	    @Override
	    public void run() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	    }
	});
    }

    public void setStage(Stage stage) {
	this.stage = stage;
    }

    public Stage getStage() {
	return stage;
    }

    protected void printEnemyCardBox(ClientPackage clientPackage) {
	enemyCardBox.getChildren().clear();
	if (clientPackage.getNumberOfCards() == 1 && !clientPackage.getEnemyCards().isEmpty()
		&& clientPackage.isPredictionPhase()) {
	    for (int i = 0; i < clientPackage.getPlayerNames().size(); i++) {
		if (i != playerIndex) {
		    VBox vbox = new VBox();
		    vbox.setAlignment(Pos.CENTER);
		    Text text = new Text();
		    text.setText(clientPackage.getPlayerNames().get(i));
		    text.setFont(Font.font("Lucida Fax", 12));
		    text.setFill(Color.WHITE);
		    vbox.getChildren().add(text);
		    vbox.getChildren().add(new Card(clientPackage.getEnemyCards().get(i).getKey(),
			    clientPackage.getEnemyCards().get(i).getValue()));
		    cardBox.getChildren().add(vbox);
		}
	    }
	}
	Text text1 = new Text();
	text1.setText("Enemy scores:  ");
	text1.setFont(Font.font("Lucida Fax", 16));
	text1.setFill(Color.WHITE);
	enemyCardBox.getChildren().add(text1);
	for (int i = 0; i < clientPackage.getPlayerNames().size(); i++) {
	    if (i != playerIndex) {
		Enemy enemy = new Enemy(clientPackage.getPlayerNames().get(i), clientPackage.getPlayerScores().get(i),
			clientPackage.getPlayerStreaks().get(i), clientPackage.getPredictions().get(i),
			clientPackage.getWon().get(i));
		if (clientPackage.getDealerNumber() == i) {
		    enemy.setBackground(new Background(new BackgroundFill(
			    new Color(173.0 / 255, 113.0 / 255, 214.0 / 255, 0.6), CornerRadii.EMPTY, Insets.EMPTY)));
		}
		if (clientPackage.getTurn() == i) {
		    enemy.setBackground(new Background(new BackgroundFill(
			    new Color(112.0 / 255, 211.0 / 255, 163.0 / 255, 0.6), CornerRadii.EMPTY, Insets.EMPTY)));
		}
		enemyCardBox.getChildren().add(enemy);
	    }
	}
	if (clientPackage.getTromph().getValue() != 0) {
	    Text text = new Text();
	    text.setText("   Tromph:  ");
	    text.setFont(Font.font("Lucida Fax", 14));
	    text.setFill(Color.WHITE);
	    enemyCardBox.getChildren().add(text);
	    enemyCardBox.getChildren()
		    .add(new Card(clientPackage.getTromph().getValue(), clientPackage.getTromph().getKey()));
	}

    }

    protected void printPlacedCardBox(ClientPackage clientPackage) {
	placedCardBox.getChildren().clear();

	for (Pair<Integer, Integer> p : clientPackage.getPlayedCards()) {
	    Card card = new Card(p.getKey(), p.getValue());
	    placedCardBox.getChildren().add(card);
	}
    }

    protected void printLabels(ClientPackage clientPackage) {
	String cssLayout = "-fx-border-color: white;\n" + "-fx-border-width: 1;\n";
	playerNameLabel.setText(playerName);
	playerScoreLabel.setText("" + clientPackage.getPlayerScores().get(playerIndex));
	playerStreakLabel.setText("" + clientPackage.getPlayerStreaks().get(playerIndex));
	playerPredictionLabel.setText("" + clientPackage.getPredictions().get(playerIndex));
	playerWonLabel.setText("" + clientPackage.getWon().get(playerIndex));
	dealerLabel.setBackground(new Background(new BackgroundFill(
		    new Color(173.0 / 255, 113.0 / 255, 214.0 / 255, 0.6), CornerRadii.EMPTY, Insets.EMPTY)));
	dealerLabel.setText(clientPackage.getPlayerNames().get(clientPackage.getDealerNumber()));
	turnLabel.setBackground(new Background(new BackgroundFill(
		    new Color(112.0 / 255, 211.0 / 255, 163.0 / 255, 0.6), CornerRadii.EMPTY, Insets.EMPTY)));
	turnLabel.setText(clientPackage.getPlayerNames().get(clientPackage.getTurn()));
	turnLabel.setStyle(cssLayout);
	dealerLabel.setStyle(cssLayout);
	numberOfCardsLabel.setText("" + clientPackage.getNumberOfCards());
	if (clientPackage.isPredictionPhase()) {
	    phaseLabel.setText("Prediction phase");
	} else {
	    phaseLabel.setText("Pick phase");
	}

	if (clientPackage.isWaitingPhase()) {
	    phaseLabel.setText("Waiting phase");
	}
    }
}
