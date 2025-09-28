package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.MucItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;
import java.util.Objects;

public class GroupInformationController {

    @FXML
    private Label description;

    @FXML
    private ListView<Affiliate> adminListview;

    @FXML
    private Circle avatarCircle;

    @FXML
    private ImageView closeWindow;

    @FXML
    private Label groupname;

    @FXML
    private ScrollPane infoScrollPane;

    @FXML
    private Label jidLabel;

    @FXML
    private Label memberNumbers;

    @FXML
    private AnchorPane viewAnchorPane;


    private MucItem mucItem;


    private static final ObservableList<Affiliate> CHAT_ITEMS = FXCollections.observableArrayList();

    @FXML
    void closeWindowAction(MouseEvent event) {
        infoScrollPane.getScene().getWindow().hide();
    }

    @FXML
    public void initialize(){
        jidLabel.setMaxWidth(250);
        jidLabel.setWrapText(true);
        CHAT_ITEMS.clear();
        infoScrollPane.getStylesheets().add(Objects.requireNonNull(EZChatApplication.class.getResource("css/ScrollbarCss.css")).toExternalForm());
        infoScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        infoScrollPane.setFitToWidth(true);
        infoScrollPane.setFitToHeight(true);
        infoScrollPane.setHvalue(0);

    }

    public void setMucItem(MucItem mucItem) throws Exception{
        this.mucItem = mucItem;
        MultiUserChat muc = mucItem.getMUC();
        String size = String.valueOf(muc.getMembers().size());
        memberNumbers.setText(size);
        jidLabel.setText(muc.getMyRoomJid().asEntityBareJidString());
        groupname.setText(mucItem.getMUCName());
        description.setText(muc.getSubject());
        List<Affiliate> owners = muc.getOwners();
        CHAT_ITEMS.add(owners.get(0));
        List<Affiliate> admins = muc.getAdmins();
        CHAT_ITEMS.addAll(admins);
        adminListview.setItems(CHAT_ITEMS);
        adminListview.setCellFactory(listView -> new AdminListViewCell());

    }
}
