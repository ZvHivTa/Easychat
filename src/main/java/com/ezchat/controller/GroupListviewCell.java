package com.ezchat.controller;

import com.ezchat.pojo.GroupItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class GroupListviewCell extends ListCell<GroupItem> {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label groupname;

    @FXML
    private Label grouptype;
    @FXML
    void groupListViewCellMouseEnteredAction(MouseEvent event) {
        setCursor(Cursor.HAND);
        setStyle("-fx-background-color:#2f3740");
    }


    @FXML
    void groupListViewCellMouseExitedAction(MouseEvent event) {
        setStyle("-fx-background-color:transparent");
    }
    /***
     * @param item ChatItem类型的实例，聊天表格pojo类
     * @param empty Listview是否为空
     */
    @Override
    protected void updateItem(GroupItem item, boolean empty) {
        super.updateItem(item, empty);
        //由于注释中表明的特性，我们只在非空的时候调用该操作去初始化列表。
        if (item != null && !empty) {
            groupname.setText(item.getGroupName());
            String type = null;
            if(item.isSharedGroup()){
                type = "shared";
            }else{
                type = "private";
            }
            grouptype.setText(type);
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

    @FXML
    public void initialize(){

    }

    public GroupListviewCell(){
        try {
            FXMLLoader loader = new FXMLLoader(GroupListviewCell.class.getResource("/com/ezchat/fxml/GroupListviewCell.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
