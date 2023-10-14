package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.ChatItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class MUCReceiveBubbleController {
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

    private ChatItem chatItem;


    @FXML
    void senderInfoAction(MouseEvent event) {
        Stage infoStage = new Stage();
        infoStage.setAlwaysOnTop(true);
        infoStage.initStyle(StageStyle.UNDECORATED);
        infoStage.setResizable(false);
        infoStage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/UserInformation.fxml")));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        infoStage.setScene(scene);
        infoStage.show();
        UserInformationController controller = fxmlLoader.getController();
        try {
            controller.setChatItem(chatItem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    public void initialize() throws IOException {
        messageFlow.setMaxWidth(300);
    }

    public void setChatItem(ChatItem chatItem){
        this.chatItem = chatItem;
    }


}
