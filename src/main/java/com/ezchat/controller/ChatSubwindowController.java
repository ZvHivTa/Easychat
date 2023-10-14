package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.ChatItem;
import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.pojo.Item;
import com.ezchat.pojo.MucItem;
import com.ezchat.utils.ChatUtil;
import com.ezchat.utils.FileTransferUtil;
import com.ezchat.utils.OpenfireConnectUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jivesoftware.smack.SmackException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ChatSubwindowController {
    @FXML
    private Button send_button;

    @FXML
    private TextField sendmessage_text;

    @FXML
    private ScrollPane sp_chatview;

    @FXML
    private Label usernameLabel;

    @FXML
    private VBox vbox_messages;

    @FXML
    private ImageView attachedFile;

    @FXML
    private Label groupNameLabel;


    private Jid jid;

    private Item item;


    @FXML
    void itemInformationMouseClickedAction(MouseEvent event) {
        Stage infoStage = new Stage();
        infoStage.setAlwaysOnTop(true);
        infoStage.initStyle(StageStyle.UNDECORATED);
        infoStage.setResizable(false);
        infoStage.initModality(Modality.APPLICATION_MODAL);
        if(item instanceof ChatItem) {
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
                controller.setChatItem(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(item instanceof MucItem mucItem){
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/GroupInformation.fxml")));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
                infoStage.setScene(scene);
                infoStage.show();
                GroupInformationController controller = fxmlLoader.getController();
                controller.setMucItem(mucItem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    /***
     * 发送消息给服务器
     * @param event sendButton事件
     */
    @FXML
    public void sendMessageAction(ActionEvent event){
        //处理组件样式
        String message = sendmessage_text.getText();
        String domainPart = jid.getDomain().toString();
        HBox hBox = null;
        if(!message.isEmpty()||message==null){
            if(domainPart.equals(OpenfireConnectUtil.SERVER_NAME)) {
                try {
                    hBox = ChatUtil.createMessageBubble(message, ConnectionContainer.getUserJid());
                    //发送消息给服务器
                    ChatUtil.sendMessage(message,jid.asEntityBareJidIfPossible());
                    vbox_messages.getChildren().add(hBox);
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    ChatUtil.sendMUCmessage(message,jid);
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            sendmessage_text.clear();
        }
    }





    /***
     * 发送文件事件，前端处理部分
     *
     * @param result 发送的结果
     */

    public void addResultBubble(String result) throws IOException, SmackException.NotConnectedException, InterruptedException {
        System.out.println(result);
        EntityBareJid entityBareJid = JidCreate.entityBareFrom(usernameLabel.getText() + "@" + OpenfireConnectUtil.SERVER_NAME);
        ChatUtil.sendMessage(result, entityBareJid);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                HBox hBox = ChatUtil.createMessageBubble(result, ConnectionContainer.getUserJid());
                vbox_messages.getChildren().add(hBox);
            }
        });
    }

    public void addFileBubble(HBox fileBubble){
        vbox_messages.getChildren().add(fileBubble);
    }

    /***
     * 文件选择器事件
     * @param event 文件选择器事件
     */
    @FXML
    void AttachFileClickedAction(MouseEvent event) throws IOException {

        List<File> files = FileTransferUtil.showFileChooser();
        if(files!=null&&!files.isEmpty()) {
            SendFilesController.setFiles(files);
            Stage fileStage = new Stage();
            fileStage.setAlwaysOnTop(true);
            fileStage.getIcons().add(new Image(Objects.requireNonNull(EZChatApplication.class.getResourceAsStream("img/EZchat.png"))));
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/SendFilesPane.fxml")));

            Scene scene = new Scene(fxmlLoader.load());
            fileStage.setScene(scene);
            fileStage.initStyle(StageStyle.UNDECORATED);
            fileStage.setResizable(false);
            fileStage.initModality(Modality.APPLICATION_MODAL);
            fileStage.show();
            SendFilesController controller = fxmlLoader.getController();
            controller.setChatSubwindowController(this);
            String resourceName = usernameLabel.getText()+"@"+OpenfireConnectUtil.RESOURCE_NAME;
            controller.setJid(JidCreate.entityFullFrom(usernameLabel.getText()+"@"+ OpenfireConnectUtil.SERVER_NAME+"/"+resourceName));
        }
    }


    @FXML
    public void initialize(){
        sp_chatview.getStylesheets().add(Objects.requireNonNull(EZChatApplication.class.getResource("css/ChatScrollpaneCss.css")).toExternalForm());
        sp_chatview.getStylesheets().add(Objects.requireNonNull(EZChatApplication.class.getResource("css/ScrollbarCss.css")).toExternalForm());
        sp_chatview.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        vbox_messages.heightProperty().addListener((observableValue, number, t1) -> {
            sp_chatview.setVvalue((Double) t1);
        });
    }


    public Jid getJid() {
        return jid;
    }

    public void setJid(Jid jid) {
        this.jid = jid;
    }

    public void setChatItem(Item item) {
        this.item = item;
    }
}
