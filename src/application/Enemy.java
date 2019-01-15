package application;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Enemy extends VBox {
    /**
     * Contains information about an opponent
     * */
    protected int streak;
    protected int score;
    protected String name;
    protected int won;
    protected int prediction;

    public Enemy(String name, int score, int streak, int prediction, int won) {
	setAlignment(Pos.CENTER);
	setPrefHeight(168);
	setPrefWidth(120);
	
	Text text1 = new Text();
	text1.setText(name);
	text1.setFont(Font.font("Lucida Fax",15));
	text1.setFill(Color.WHITE);
	getChildren().add(text1);
	
	Text text2 = new Text();
	text2.setText("Score: " + score);
	text2.setFont(Font.font("Lucida Fax",12));
	text2.setFill(Color.WHITE);
	getChildren().add(text2);
	
	Text text3 = new Text();
	text3.setText("Streak: " + streak);
	text3.setFont(Font.font("Lucida Fax",12));
	text3.setFill(Color.WHITE);
	getChildren().add(text3);
	
	Text text4 = new Text();
	text4.setText("Prediction: " + prediction);
	text4.setFont(Font.font("Lucida Fax",12));
	text4.setFill(Color.WHITE);
	getChildren().add(text4);
	
	Text text5 = new Text();
	text5.setText("Won this round: " + won);
	text5.setFont(Font.font("Lucida Fax",12));
	text5.setFill(Color.WHITE);
	getChildren().add(text5);

	String cssLayout = "-fx-border-color: white;\n" + "-fx-border-width: 1;\n";
	setStyle(cssLayout);
	
	setPrefSize(120, 170);
    }
}
