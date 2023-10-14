package com.ezchat.controller;

import com.ezchat.pojo.MucItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.IOException;


public class MucListviewCell extends ListCell<MucItem> {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label mucDescription;

    @FXML
    private Label mucName;

    @FXML
    void mucListViewCellMouseEnteredAction(MouseEvent event) {
        setCursor(Cursor.HAND);
        setStyle("-fx-background-color:#2f3740");
    }

    @FXML
    void mucListViewCellMouseExitedAction(MouseEvent event) {
        setStyle("-fx-background-color:transparent");
    }

    @Override
    protected void updateItem(MucItem item, boolean empty) {
        super.updateItem(item, empty);
        //由于注释中表明的特性，只在非空的时候调用该操作去初始化列表。
        if (item != null && !empty) {
            MultiUserChat muc = item.getMUC();
            this.mucName.setText(item.getMUCName());
            this.mucDescription.setText(muc.getSubject());
            //最终要更改默认的item类型，Listview默认设置列表中的item是纯文本TEXT类型。
            //这里设置为Graphic类型，相当于把组件外部包裹的Pane（root）作为客制化的items植入进Listview中
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

    public MucListviewCell(){
        try {
            FXMLLoader loader = new FXMLLoader(ChatListviewCell.class.getResource("/com/ezchat/fxml/MUCListviewCell.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
