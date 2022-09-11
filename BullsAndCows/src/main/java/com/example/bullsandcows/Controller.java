package com.example.bullsandcows;

import Game.Game;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button newGameButton;
    @FXML
    private Button showStatisticButton;
    @FXML
    private Button exitButton;

    @FXML
    private Button guessButton;
    @FXML
    private TextField guessField;
    @FXML
    private TextArea guessesShowList;
    @FXML
    private Label gameResult;

    @FXML
    private CheckBox clearTextFieldAfterGuess;
    @FXML
    private Button showAnswerButton;
    @FXML
    private Label answerLabel;

    @FXML
    private Pane statisticsPane;
    @FXML
    private Label statisticsInfo;

    private final Game game = new Game();

    @FXML
    protected void onGuessButtonClick() {
        String guessResult;
        String text = guessesShowList.getText();
        String guess = guessField.getText();
        System.out.println("You guessed: " + guess);

        if (game.isGuessCorrect(guess)) {
            String result = game.guess(Game.stringToArrayListOfIntegers(guess));
            guessResult = game.getCountOfTries() + ".   " + guess +
                    "   »»»   " + result;
            text += guessResult + "\n";
            guessesShowList.setText(text);
            gameResult.setText("");
        }
        else {
            gameResult.setTextFill(Color.color(0.95, 0.75, 0));
            gameResult.setText("Expected four-digit number like XXXX");
        }

        if (clearTextFieldAfterGuess.isSelected()) {
            guessField.clear();
        }

        if (Game.isEnded) {
            gameResult.setTextFill(Color.color(0.0, 0.90, 0.30));
            gameResult.setText("You win! Attempts count: " + game.getCountOfTries());
            guessButton.setDisable(true);
            statisticsInfo.setText(game.getGameResults());
        }
    }
    @FXML
    protected void onNewGameButtonClick() {
        gameResult.setText("");
        answerLabel.setText("");
        guessesShowList.clear();
        guessField.clear();
        guessButton.setDisable(false);
        showAnswerButton.setDisable(false);
        game.restart();
    }
    @FXML
    protected void onShowStatisticButtonClick() {
        if (statisticsPane.isVisible()) {
            statisticsInfo.setText("");
            statisticsPane.setVisible(false);
            showStatisticButton.setText("Show statistics");
        } else {
            statisticsInfo.setText(game.getGameResults());
            statisticsPane.setVisible(true);
            showStatisticButton.setText("Hide statistics");
        }
    }
    @FXML
    protected void onExitButtonClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    protected void onShowAnswerButtonClick() {
        showAnswerButton.setDisable(true);
        answerLabel.setText(game.getAnswer().toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        guessField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                onGuessButtonClick();
            }
        });
    }
}