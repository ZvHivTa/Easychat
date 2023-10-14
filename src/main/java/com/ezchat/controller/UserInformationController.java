package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.ChatItem;
import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.pojo.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.Jid;

import java.io.IOException;
import java.util.Objects;

public class UserInformationController {
    @FXML
    private Label Nickname;

    @FXML
    private Button addToFriends;

    @FXML
    private Circle avatarCircle;

    @FXML
    private ImageView closeWindow;

    @FXML
    private Label emailAddress;

    @FXML
    private Label jidLabel;

    @FXML
    private Button sendMessage;

    @FXML
    private Label username;

    @FXML
    private ScrollPane infoScrollPane;

    private Jid jid;

    @FXML
    void closeWindowAction(MouseEvent event) {
        infoScrollPane.getScene().getWindow().hide();
    }
    public void setChatItem(Item item) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException, InterruptedException {
        if(item instanceof ChatItem chatItem){
            Jid jid = chatItem.getJid();
            this.jid = jid;
            username.setText(chatItem.getUsername());
            jidLabel.setText(chatItem.getJid().toString());
            VCard vCard = ConnectionContainer.getVCardManager().loadVCard(jid.asEntityBareJidIfPossible());
            emailAddress.setText(vCard.getEmailHome());
            Nickname.setText(vCard.getNickName());
            avatarCircle.setFill(new ImagePattern(chatItem.getAvatar()));
        }
        infoScrollPane.vvalueProperty().set(0.0);
    }

    @FXML
    void sendMessageClickedAction(MouseEvent event) {
        infoScrollPane.getScene().getWindow().hide();
        EZChatApplication.mainController.setChatListViewSelectedItem(jid);
    }

    @FXML
    public void initialize() throws IOException {
        infoScrollPane.getStylesheets().add(Objects.requireNonNull(EZChatApplication.class.getResource("css/ScrollbarCss.css")).toExternalForm());
        infoScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
