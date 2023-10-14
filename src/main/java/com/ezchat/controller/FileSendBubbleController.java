package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileSendBubbleController {
    @FXML
    private Circle FileIconCircle;

    @FXML
    private Label Filename;

    @FXML
    private Text Filesize;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public void initialize(){
        anchorPane.setId("pane");
        anchorPane.getStylesheets().add(Objects.requireNonNull(EZChatApplication.class.getResource("css/FileBubbleCss.css")).toExternalForm());
        Image fileImage;
        try (InputStream resourceAsStream = EZChatApplication.class.getResourceAsStream("img/fileIcon.png")) {
            fileImage = new Image(Objects.requireNonNull(resourceAsStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileIconCircle.setFill(new ImagePattern(fileImage));
    }

}
