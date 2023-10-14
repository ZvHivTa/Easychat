package com.ezchat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class MUCSendBubbleController {

    @FXML
    private Circle avatarCircle;

    @FXML
    private Text messageBody;

    @FXML
    private TextFlow messageFlow;

    @FXML
    private AnchorPane mucBubblePane;

    @FXML
    private Label userName;

    @FXML
    public void initialize() throws IOException {
        messageFlow.setMaxWidth(300);
    }


}
