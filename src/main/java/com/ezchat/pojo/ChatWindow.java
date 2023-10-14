package com.ezchat.pojo;

import com.ezchat.EZChatApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

/***
 * 聊天窗口类
 * 封装用
 * @pane 对应fxml文件加载后的pane
 * @fxmlLoader FXML加载器，通过它在其他非controller类中通过fx:id获取到pane的组件
 */
public class ChatWindow {
    private AnchorPane pane;
    private FXMLLoader fxmlLoader;

    public ChatWindow() {

    }

    public ChatWindow(String resourcePath) throws IOException {
        this.fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource(resourcePath)));
        this.pane = fxmlLoader.load();
    }
    public ChatWindow(AnchorPane pane, FXMLLoader fxmlLoader) {
        this.pane = pane;
        this.fxmlLoader = fxmlLoader;
    }

    public AnchorPane getPane() {
        return pane;
    }

    public void setPane(AnchorPane pane) {
        this.pane = pane;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public void setFxmlLoader(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }
}
