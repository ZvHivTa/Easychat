package com.ezchat.controller;

import com.ezchat.pojo.ChatItem;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class ChatListviewCell extends ListCell<ChatItem> {

    @FXML
    private ImageView avatar;

    @FXML
    private Label username;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextFlow newMessageNumberTextFlow;

    @FXML
    private Text newMessageNumberTxt;

    private ChatItem chatItem;

    public static int num = 0;


    public ChatListviewCell(){
        try {
            FXMLLoader loader = new FXMLLoader(ChatListviewCell.class.getResource("/com/ezchat/fxml/ChatListviewCell.fxml"));
            loader.setController(this);
            //loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void chatListViewCellMouseEnteredAction(MouseEvent event) {
        setCursor(Cursor.HAND);
        setStyle("-fx-background-color:#2f3740");
    }


    @FXML
    void chatListViewCellMouseExitedAction(MouseEvent event) {
        setStyle("-fx-background-color:transparent");
    }
    /***
     * updateItem不仅在Listview初始化时被调用，在滚动Listview中的items时也会被调用。
     * @param item ChatItem类型的实例，聊天表格pojo类
     * @param empty Listview是否为空
     */
    @Override
    protected void updateItem(ChatItem item, boolean empty) {
        super.updateItem(item, empty);
        //由于注释中表明的特性，只在非空的时候调用该操作去初始化列表。
        if (item != null && !empty) {
            this.chatItem = item;
            avatar.setImage(item.getAvatar());
            username.setText(item.getUsername());
            StringProperty newMessageNumber = chatItem.getNewMessageNumber();
            newMessageNumberTxt.textProperty().bind(newMessageNumber);
            newMessageNumberTextFlow.setVisible(false);
            newMessageNumberTxt.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if(!newMessageNumberTextFlow.isVisible()){
                        newMessageNumberTextFlow.setVisible(true);
                    }
                    //未来的值要变成0，只能说明有动作重置了它，因而要把气泡隐藏。
                    if("0".equals(t1)){
                        newMessageNumberTextFlow.setVisible(false);
                    }
                }
            });
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
        newMessageNumberTextFlow.setStyle("-fx-background-color:rgb(255,0,0);"
                +"-fx-background-radius:50px");
    }
}
