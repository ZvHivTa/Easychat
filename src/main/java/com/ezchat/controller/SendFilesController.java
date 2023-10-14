package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.utils.ChatUtil;
import com.ezchat.utils.FileTransferUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jxmpp.jid.Jid;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SendFilesController {
    @FXML
    private ListView<File> FilesListView;

    @FXML
    private ScrollPane FilesScollpane;

    @FXML
    private Hyperlink addFiles;

    @FXML
    private Hyperlink closeFilewindow;

    @FXML
    private Hyperlink sendFiles;

    private Jid jid;

    private OutgoingFileTransfer transfer;

    private ChatSubwindowController chatSubwindowController;

    private static final ObservableList<File> FILE_ITEMS = FXCollections.observableArrayList();

    public void setJid(Jid jid) {
        this.jid = jid;
        setTransfer(jid);
    }

    /***
     * 创建文件发送器
     * @param jid 对象
     */
    private void setTransfer(Jid jid){
        FileTransferManager transferManager = ConnectionContainer.getFileTransferManager();
        this.transfer = transferManager.createOutgoingFileTransfer(jid.asEntityFullJidOrThrow());

    }

    public void setChatSubwindowController(ChatSubwindowController chatSubwindowController) {
        this.chatSubwindowController = chatSubwindowController;
    }



    @FXML
    void CancelSendFilesAction(MouseEvent event) {
        Window thisWindow = ((Node) (event.getSource())).getScene().getWindow();
        FILE_ITEMS.clear();
        thisWindow.hide();
    }

    @FXML
    void addFilesAction(MouseEvent event) {
        try {
            List<File> files = FileTransferUtil.showFileChooser();
            FILE_ITEMS.addAll(files);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     *
     * @param event 发送文件处理
     */
    @FXML
    void sendFilesAction(MouseEvent event) throws IOException, SmackException, InterruptedException {
        //Spark测试用
        //EntityBareJid entityBareJid = jid.asEntityBareJidIfPossible();
//        String s = entityBareJid.toString();
//        s = s+"/"+"Spark";
//        OutgoingFileTransfer transfer = ConnectionContainer.getFileTransferManager().createOutgoingFileTransfer(JidCreate.entityFullFrom(s));

        //将chooser关闭
        addFiles.getScene().getWindow().hide();
        HBox fileBubble = ChatUtil.createSendFileBubble(FILE_ITEMS.get(0));
        chatSubwindowController.addFileBubble(fileBubble);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送文件给选定的对象
                    String s = FileTransferUtil.sendFiles(transfer, FILE_ITEMS);
                    chatSubwindowController.addResultBubble(s);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    @FXML
    public void initialize(){
        FilesScollpane.getStylesheets().add(Objects.requireNonNull(EZChatApplication.class.getResource("css/FileScrollpaneCss.css")).toExternalForm());
        FilesScollpane.getStylesheets().add(Objects.requireNonNull(EZChatApplication.class.getResource("css/ScrollbarCss.css")).toExternalForm());
        FilesScollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        FilesScollpane.setBorder(Border.EMPTY);
        FilesListView.setItems(FILE_ITEMS);
        FilesListView.setCellFactory(listView -> new FileListviewCell());
    }

    public static void setFiles(List<File> files) {
        FILE_ITEMS.setAll(files);
    }

    public static void removeFile(File file) {
        FILE_ITEMS.remove(file);
    }

}
