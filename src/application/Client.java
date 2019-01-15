package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class Client extends Controller {
    /**
     * Contains the UI's client part of the controller
     * */
    protected Socket connection;
    protected ObjectInputStream input;
    protected ObjectOutputStream output;

    ClientPackage cp = new ClientPackage();

    @FXML
    protected void connectButtonClicked(MouseEvent me) {
	/**
	     * "Connect" button's "Mouse Clicked" event handler
	     * Checks the validity of the contents, then attempts to connect
	     * */
	if (playerNameField.getText().length() < 1 || serverIPField.getText().length() < 1) {
	    alert("Invalid inputs");
	    playerNameField.clear();
	    serverIPField.clear();
	} else {
	    playerName = playerNameField.getText();
	    serverIp = serverIPField.getText();
	    SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	    tabPane.getTabs().get(0).setDisable(true);
	    selectionModel.select(1);
	    startRunning();
	}
    }

    @FXML
    protected void answerFieldEnterPressed(KeyEvent ke) {
	/**
	     * "Answer" textfield's "Key Pressed" event handler
	     * */
	try {
	    if (ke.getCode() == KeyCode.ENTER) {

		if (cp.isPredictionPhase()) {
		    answerInPredictionPhase();
		} else if (cp.isWaitingPhase()) {
		    answerField.clear();
		    output.writeObject(new Integer(1));
		    output.flush();
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    protected void answerInPredictionPhase() throws IOException {
	/**
	 * Checks the validity of the answer field's input then sends it to the server
	 * */
	if (answerField.getText().matches("[0-8]")) {
		Integer ans = Integer.parseInt(answerField.getText());
		answerField.clear();
		if(cp.getNumberOfCards() < ans) {
		    alert("Your prediction must be a number between 0 and your number of cards.");
		    return;
		}
		if (playerIndex == cp.getDealerNumber()) {
		    int sum = 0;
		    for (int i = 0; i < cp.getPredictions().size(); i++) {
			if (i != playerIndex) {
			    sum += cp.getPredictions().get(i);
			}
		    }
		    if (sum <= cp.getNumberOfCards()) {
			if (ans == cp.getNumberOfCards() - sum) {
			    alert("The predictions' sum cannot be equal to the number of cards in the round.");
			    return;
			}
		    }
		}
		output.writeObject(ans);
		output.flush();
	    } else {
		alert("Your prediction must be a number between 0 and your number of cards.");
		answerField.clear();
	    }
    }

    protected void startRunning() {
	try {
	    connectToServer();
	    setupStreams();
	    createInputThread();
	    sendName();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    protected void connectToServer() throws UnknownHostException, IOException {
	/**
	 * Attempts to conenct to the server's socket
	 * */
	connection = new Socket(InetAddress.getByName(serverIp), 4444);
    }

    protected void sendName() {
	/**
	 * Sends the player's name to the server
	 * */
	try {
	    output.writeObject(playerName);
	    output.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    protected void setupStreams() throws IOException {
	
	input = new ObjectInputStream(connection.getInputStream());
	output = new ObjectOutputStream(connection.getOutputStream());
	output.flush();
    }

    protected void createInputThread() {
	/**
	 * Creates a thread which is responsible for receiving packages from the server
	 * */
	Thread thread = new Thread(new Runnable() {

	    @Override
	    public void run() {

		try {
		    playerIndex = (int) input.readObject();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		try {
		    while (connection.isConnected()) {
			ClientPackage clientPackage = (ClientPackage) input.readObject();
			printInfo(clientPackage);
		    }
		} catch (IOException | ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (Exception e) {

		} finally {

		    try {
			closeConnection();
		    } catch (IOException e) {
			e.printStackTrace();
		    } catch (Exception e) {

		    }
		}

	    }
	});
	thread.setDaemon(true);
	thread.start();
    }

    protected void printInfo(ClientPackage clientPackage) {
	/**
	 * Visualizes the information about the game state
	 * */
	cp = clientPackage;
	Platform.runLater(new Runnable() {

	    @Override
	    public void run() {
		if (clientPackage.getWinners() != null) {
		    printWinners(clientPackage);
		} else {
		    if (clientPackage.getNumberOfPlayers() == clientPackage.getPlayerNames().size()) {
			printOwnCardBox(clientPackage);
			printPlacedCardBox(clientPackage);
			printEnemyCardBox(clientPackage);
			printLabels(clientPackage);
			setDealerColor(clientPackage);
			setTurnColor(clientPackage);
		    }
		}

	    }
	});
    }

    protected void printWinners(ClientPackage clientPackage) {
	/**
	 * Visualizes the winners at the end of the match
	 * */
	placedCardBox.getChildren().clear();
	ListView<String> listView = new ListView<>();
	for (String s : clientPackage.getWinners()) {
	    listView.getItems().add(s);
	}
	VBox vbox = new VBox();
	vbox.setAlignment(Pos.CENTER);
	Text text = new Text();
	text.setText("Winners:\n");
	text.setFont(Font.font("Lucida Fax", 16));
	text.setFill(Color.WHITE);
	vbox.getChildren().add(text);
	vbox.getChildren().add(listView);
	placedCardBox.getChildren().add(vbox);
    }

    protected void setDealerColor(ClientPackage clientPackage) {
	/**
	 * Highloghts the dealer
	 * */
	if (playerIndex == clientPackage.getDealerNumber()) {
	    cardBox.setBackground(new Background(new BackgroundFill(
		    new Color(173.0 / 255, 113.0 / 255, 214.0 / 255, 0.6), CornerRadii.EMPTY, Insets.EMPTY)));
	} else {
	    cardBox.setBackground(splitPane.getBackground());
	}
    }

    protected void setTurnColor(ClientPackage clientPackage) {
	/**
	 * Highlights the player on turn
	 * */
	if (playerIndex == clientPackage.getTurn()) {
	    cardBox.setBackground(new Background(new BackgroundFill(
		    new Color(112.0 / 255, 211.0 / 255, 163.0 / 255, 0.6), CornerRadii.EMPTY, Insets.EMPTY)));
	}
    }

    protected void printOwnCardBox(ClientPackage clientPackage) {
	/**
	 * Visualizes the player's cards
	 * */
	cardBox.getChildren().clear();
	if (clientPackage.getNumberOfCards() == 1) {
	    if (!clientPackage.isWaitingPhase()) {
		Text text = new Text();
		text.setText("   Own cards:  ");
		text.setFont(Font.font("Lucida Fax", 16));
		text.setFill(Color.WHITE);
		cardBox.getChildren().add(text);
	    }
	    for (Pair<Integer, Integer> p : clientPackage.getOwnCards()) {
		Card card = new Card(0, 0);
		Integer index = cardBox.getChildren().size() - 1;
		card.setOnMouseClicked(e -> {
		    try {
			if (clientPackage.getTurn() == playerIndex && !clientPackage.isPredictionPhase()
				&& !clientPackage.isWaitingPhase()) {
			    output.writeObject(index);
			    output.flush();
			}
		    } catch (IOException e1) {
			e1.printStackTrace();
		    }
		});
		card.setPickOnBounds(false);
		card.setCursor(Cursor.HAND);
		cardBox.getChildren().add(card);
	    }

	    if (clientPackage.isPredictionPhase()) {
		Text text = new Text();
		text.setText("   Enemy cards:  ");
		text.setFont(Font.font("Lucida Fax", 16));
		text.setFill(Color.WHITE);
		cardBox.getChildren().add(text);
	    }
	} else {
	    for (Pair<Integer, Integer> p : clientPackage.getOwnCards()) {
		Card card = new Card(p.getKey(), p.getValue());
		Integer index = cardBox.getChildren().size();
		card.setOnMouseClicked(e -> {
		    try {
			if (clientPackage.getTurn() == playerIndex && !clientPackage.isPredictionPhase()
				&& !clientPackage.isWaitingPhase()) {
			    output.writeObject(index);
			    output.flush();
			}
		    } catch (IOException e1) {
			e1.printStackTrace();
		    }
		});
		card.setPickOnBounds(false);
		card.setCursor(Cursor.HAND);
		cardBox.getChildren().add(card);
	    }
	}
    }

    public void closeConnection() throws IOException {
	/**
	 * Closes the streams and the socket
	 * */
	input.close();
	output.close();
	connection.close();
    }

}
