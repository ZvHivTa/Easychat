package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.utils.FileTransferUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Desktop;
import java.util.Objects;

public class FileReceiveBubbleController {
    @FXML
    private Circle FileIconCircle;

    @FXML
    private Label Filename;

    @FXML
    private Text Filesize;

    @FXML
    private Hyperlink acceptFile;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Hyperlink rejectFile;

    @FXML
    private Pane acceptChoicePane;

    @FXML
    private Pane openChoicePane;

    @FXML
    private Hyperlink openFile;

    @FXML
    private Hyperlink openFolder;


    private volatile boolean receiveMark = false;

    private boolean isAccepted = false;

    private String path;

    @FXML
    void acceptFileAction(MouseEvent event) {
        receiveMark = true;
        isAccepted = true;
    }
    @FXML
    void rejectFileAction(MouseEvent event) {
        receiveMark = true;
        isAccepted = false;
    }

    public void fileReceiveAction(FileTransferRequest request) throws SmackException, InterruptedException, XMPPException.XMPPErrorException, IOException {
        //receiveMark = false 说明文件接收没有被用户响应,该线程阻塞
        while (!receiveMark) {
            Thread.onSpinWait();
        }
        //说明此处已经经事件处理过，判断是否接收文件。
        if(isAccepted){
            this.path = FileTransferUtil.receiveFiles(request);
            //样式处理
            acceptChoicePane.setVisible(false);
        }else{
            request.reject();
            acceptChoicePane.setVisible(false);
        }
        openChoicePane.setVisible(isAccepted);
    }

    @FXML
    void openFileAction(MouseEvent event) {
        File file = new File(path);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void openFolderAction(MouseEvent event) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        try {
            Desktop.getDesktop().open(parentFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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
