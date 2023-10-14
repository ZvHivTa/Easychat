package com.ezchat;

import com.ezchat.controller.MainController;
import com.ezchat.pojo.ConnectionContainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.Objects;

public class EZChatApplication extends Application {
    /***
     * 登录起始界面
     * @param stage
     * @throws IOException
     */

    public static MainController mainController = null;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EZChatApplication.class.getResource("fxml/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("EZChat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(EZChatApplication.class.getResourceAsStream("img/EZchat.png"))));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        XMPPTCPConnection connection = ConnectionContainer.getConnection();
        if(connection!=null&&connection.isConnected()){
            //PresenceBuilder.buildPresence().setMode(Presence.Mode.xa)
            connection.removeAllStanzaAcknowledgedListeners();
            connection.disconnect();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}