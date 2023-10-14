package com.ezchat.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class InitChatAnchorPaneController {
    @FXML
    private AnchorPane ChatAnchorPane;

    @FXML
    private TextFlow InitChatTextFlow;

    /**
     * 初始化方法
     */
    @FXML
    public void initialize(){
        Text text = new Text("Select a chat to start messaging");
        text.setFill(Color.color(0.934,0.945,0.996));
        InitChatTextFlow.getChildren().add(text);
        InitChatTextFlow.setStyle("-fx-color:rgb(239,242,255);"
                +"-fx-background-color: #1D232A;"
                +"-fx-background-radius:20px");
        InitChatTextFlow.setPadding(new Insets(5,10,5,10));

    }
}
