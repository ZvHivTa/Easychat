package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.utils.EntryUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AdminListViewCell extends ListCell<Affiliate> {
    @FXML
    private Label adminname;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Circle avatarCircle;

    @FXML
    private Text rolename;

    private Affiliate chatItem;




    /***
     * updateItem不仅在Listview初始化时被调用，在滚动Listview中的items时也会被调用。
     * @param item
     * @param empty Listview是否为空
     */
    @Override
    protected void updateItem(Affiliate item, boolean empty) {
        super.updateItem(item, empty);
        //由于注释中表明的特性，只在非空的时候调用该操作去初始化列表。
        if (item != null && !empty) {
            this.chatItem = item;
            Image userImage = EntryUtil.getUserImage(item.getJid());
            if(userImage==null) {
                try (InputStream resourceAsStream = EZChatApplication.class.getResourceAsStream("img/unknown user.png")) {
                    userImage = new Image(Objects.requireNonNull(resourceAsStream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            avatarCircle.setFill(new ImagePattern(userImage));
            MUCAffiliation affiliation = item.getAffiliation();
            UserSearchManager userSearchManager = ConnectionContainer.getUserSearchManager();


            adminname.setText(item.getJid().getLocalpartOrNull().toString());
            rolename.setText(affiliation.name());
            setText(null);
            setGraphic(anchorPane);
            setStyle("-fx-background-color:transparent");
        } else {
            //什么都不显示，把表格的显示类型置为空
            setText(null);
            setGraphic(null);
            setStyle("-fx-background-color:transparent");
        }
    }
    @FXML
    void chatListViewCellMouseEnteredAction(MouseEvent event) {

    }

    @FXML
    void chatListViewCellMouseExitedAction(MouseEvent event) {

    }

    public AdminListViewCell(){
        try {
            FXMLLoader loader = new FXMLLoader(ChatListviewCell.class.getResource("/com/ezchat/fxml/AdminListviewCell.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
