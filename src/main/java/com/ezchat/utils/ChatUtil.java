package com.ezchat.utils;

import com.ezchat.EZChatApplication;
import com.ezchat.controller.MUCReceiveBubbleController;
import com.ezchat.controller.MainController;
import com.ezchat.pojo.ChatItem;
import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.pojo.MucItem;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Domainpart;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ChatUtil {
    /***
     * 创建消息气泡
     * @param message 消息体
     * @param jid 消息的发出者
     * @return 包裹消息气泡的hbox
     */
    private static final String cssStyleForSendBubble = "-fx-background-color:rgb(233,233,235);"
            +"-fx-background-radius:10px";
    private static final String cssStyleForReceiveBubble = "-fx-color:rgb(239,242,255);"
            +"-fx-background-color:rgb(15,125,242);"
            +"-fx-background-radius:10px";

    public static HBox createMessageBubble(String message, EntityFullJid jid){
        HBox bubble = null;
        if(jid.equals(ConnectionContainer.getUserJid())){
            bubble = createMessageBubble(message, Pos.CENTER_RIGHT, cssStyleForSendBubble, null);
        }else{
            bubble = createMessageBubble(message,Pos.CENTER_LEFT,cssStyleForReceiveBubble,Color.color(0.934,0.945,0.996));
        }
        return bubble;
    }

    /***
     *
     * @param message 消息体
     * @param pos hBox的对齐方式
     * @param style 消息气泡的样式
     * @param color 消息气泡的颜色
     * @return 包裹消息气泡的hbox
     */
    private static HBox createMessageBubble(String message, Pos pos, String style, Color color){
        HBox hBox = new HBox();
        hBox.setAlignment(pos);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle(style);
        textFlow.setPadding(new Insets(5,10,5,10));
        textFlow.setMaxWidth(300);
        if(color!=null) {
            text.setFill(color);
        }
        hBox.getChildren().add(textFlow);
        return hBox;
    }

    /***
     * 创建接收文件气泡
     * @param file 文件对象
     * @return 返回包裹文件气泡的hBox
     */

    public static HBox createSendFileBubble(File file) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/FileSendBubble.fxml")));
        AnchorPane anchorPane = fxmlLoader.load();
        HBox hBox = new HBox();

        ObservableMap<String, Object> namespace = fxmlLoader.getNamespace();

        Label filename = (Label) namespace.get("Filename");
        Text filesize = (Text) namespace.get("Filesize");
        filename.setText(file.getName());
        filesize.setText(FileTransferUtil.fileSizeCalculator(file));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(anchorPane);
        return hBox;
    }

    public static HBox createReceiveFileBubble(FXMLLoader FileReceiveBubbleLoader,FileTransferRequest request) throws IOException {

        AnchorPane anchorPane = FileReceiveBubbleLoader.load();
        HBox hBox = new HBox();
        ObservableMap<String, Object> namespace = FileReceiveBubbleLoader.getNamespace();
        Label filename = (Label) namespace.get("Filename");
        Text filesize = (Text) namespace.get("Filesize");
        filename.setText(request.getFileName());
        filesize.setText(FileTransferUtil.fileSizeCalculator(request.getFileSize()));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(anchorPane);
        return hBox;
    }

    public static HBox createMUCReceiveBubble(Message message, ChatItem chatItem) throws IOException {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/MUCReceiveBubble.fxml")));
        AnchorPane pane = fxmlLoader.load();

        MUCReceiveBubbleController controller = fxmlLoader.getController();
        controller.setChatItem(chatItem);

        ObservableMap<String, Object> namespace = fxmlLoader.getNamespace();
        Circle avatarCircle = (Circle) namespace.get("avatarCircle");
        avatarCircle.setFill(new ImagePattern(chatItem.getAvatar()));

        Label userName = (Label) namespace.get("userName");
        userName.setText(chatItem.getUsername());

        TextFlow textFlow = (TextFlow) namespace.get("messageFlow");
        textFlow.setPadding(new Insets(5,10,5,10));
        textFlow.setStyle(cssStyleForReceiveBubble);
        Text text = (Text) namespace.get("messageBody");
        text.setText(message.getBody());
        text.setFill(Color.color(0.934,0.945,0.996));

        hBox.getChildren().add(pane);
        return hBox;
    }
    public static HBox createMUCSendBubble(Message message) throws IOException {
        String body = message.getBody();
        return createMUCSendBubble(body);
    }
    public static HBox createMUCSendBubble(String message) throws IOException {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5,5,5,10));

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/MUCSendBubble.fxml")));
        AnchorPane pane = fxmlLoader.load();
        pane.setMaxWidth(250);

        ObservableMap<String, Object> namespace = fxmlLoader.getNamespace();
        Circle avatarCircle = (Circle) namespace.get("avatarCircle");
        VCard vCard = ConnectionContainer.getVCard();
        avatarCircle.setFill(new ImagePattern(EntryUtil.getUserImage(vCard.getAvatar())));

        Label userName = (Label) namespace.get("userName");
        userName.setText(ConnectionContainer.getUserName());

        //原始
        TextFlow textFlow = (TextFlow) namespace.get("messageFlow");
        textFlow.setStyle(cssStyleForSendBubble);
        textFlow.setPadding(new Insets(5,10,5,10));
        Text messageBody = (Text) namespace.get("messageBody");
        messageBody.setText(message);




        hBox.getChildren().add(pane);
        return hBox;
    }

    /***
     * 发消息
     * @param message 消息内容
     * @param jid 要发送的目标对象
     *
     */
    public static void sendMessage(String message, EntityBareJid jid) throws SmackException.NotConnectedException, InterruptedException {
        Chat chat = ConnectionContainer.getChatManager().chatWith(jid);
        chat.send(message);
    }
    /***
     * 接收发过的历史消息
     * @param message 消息内容
     * @param vbox_messages 界面内的vbox
     *
     *
     */
    public static void sendMessage(String message, VBox vbox_messages){
        HBox hBox = ChatUtil.createMessageBubble(message, ConnectionContainer.getUserJid());
        vbox_messages.getChildren().add(hBox);
    }

    /***
     * 接收消息
     * @param message 消息内容
     * @param vBox 消息展示Scrollpane中的vBox
     */
    public static void receiveMessageAction(Message message, VBox vBox){
        HBox hBox = ChatUtil.createMessageBubble(message.getBody(), message.getFrom().asEntityFullJidIfPossible());
        Platform.runLater(() -> vBox.getChildren().add(hBox));
    }

    /***
     * 接收历史消息
     * @param message 消息对象
     * @param vBox vbox_message
     */
    public static void receiveHistoryMessageAction(Message message, VBox vBox){
        HBox hBox = ChatUtil.createMessageBubble(message.getBody(), message.getFrom().asEntityFullJidIfPossible());
        vBox.getChildren().add(hBox);
    }

    /***
     *
     * @param message 消息对象
     * @param jid 群聊JID
     *
     */
    public static void sendMUCmessage(String message, Jid jid) throws SmackException.NotConnectedException, InterruptedException {
        ConcurrentHashMap<DomainBareJid, ConcurrentHashMap<Jid, MucItem>> MUC_ITEM_CONCURRENT_HASH_MAP = MainController.getMucItemConcurrentHashMap();
        Domainpart domain = jid.getDomain();
        ConcurrentHashMap<Jid, MucItem> MucItemsMap = MUC_ITEM_CONCURRENT_HASH_MAP.get(JidCreate.domainBareFrom(domain));
        if(MucItemsMap.isEmpty()||MucItemsMap==null){
            throw new RuntimeException("Domain :"+domain.toString()+" is not an MUC domain!");
        }
        MucItem mucItem = MucItemsMap.get(jid);
        if(mucItem==null){
            throw new RuntimeException("MUC :"+jid.toString()+" does not exist!");
        }
        MultiUserChat muc = mucItem.getMUC();
        muc.sendMessage(message);
    }


}
