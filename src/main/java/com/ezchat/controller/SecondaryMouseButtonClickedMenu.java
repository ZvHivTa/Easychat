package com.ezchat.controller;

import com.ezchat.pojo.ChatItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class SecondaryMouseButtonClickedMenu extends ContextMenu {
    private static SecondaryMouseButtonClickedMenu menu = null;
    private static ChatItem selectedChatItem;

    private MainController mainController;
    private SecondaryMouseButtonClickedMenu(MainController mainController){
        this.mainController = mainController;
        MenuItem multiWindow = new MenuItem("Open in another window…");
        getItems().add(multiWindow);
        multiWindow.setOnAction(e -> {
            try {
                mainController.createChatWindow(selectedChatItem);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static SecondaryMouseButtonClickedMenu getMenu(MainController mainController){
        if(menu == null){
            menu = new SecondaryMouseButtonClickedMenu(mainController);
        }
        return menu;
    }

    public static void setSelectedChatItem(ChatItem chatItem){
        selectedChatItem = chatItem;
    }

}
