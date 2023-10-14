package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.utils.OpenfireConnectUtil;
import com.leewyatt.rxcontrols.controls.RXFillButton;
import com.leewyatt.rxcontrols.controls.RXPasswordField;
import com.leewyatt.rxcontrols.controls.RXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.Objects;

/***
 * 登陆页面控制器
 *
 */
public class LoginController {
    //登录按钮

    @FXML
    private RXFillButton loginButton;
    //用户名文本框
    @FXML
    private RXTextField usernameRXTxt;
    //密码文本框
    @FXML
    private RXPasswordField passwordRXTxt;

    @FXML
    private Label loginErrorLabel;




    /***
     * 登录事件处理
     * @param event 登录按钮事件
     */
    @FXML
    void loginAction(ActionEvent event) {
        String username = usernameRXTxt.getText();
        String password = passwordRXTxt.getText();
        ConnectionContainer connectionContainer = null;
        //登录窗体
        Window thisWindow = ((Node) (event.getSource())).getScene().getWindow();
        //判断用户合法性
        try {
            connectionContainer = OpenfireConnectUtil.connectionLogin(Objects.requireNonNull(OpenfireConnectUtil.initOpenfireConnect(username, password)));

        } catch (Exception e) {
            e.printStackTrace();
        }


        //登录成功，加载聊天窗体，关闭登录窗体。
        if(connectionContainer!=null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(EZChatApplication.class.getResource("fxml/main.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.getIcons().add(new Image(Objects.requireNonNull(EZChatApplication.class.getResourceAsStream("img/EZchat.png"))));
                stage.setTitle("ezchat");
                stage.setScene(scene);
                stage.show();
                EZChatApplication.mainController = fxmlLoader.getController();
                //关闭登录窗体
                thisWindow.hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //用户不合法，登录失败
            //展示报错效果
            passwordRXTxt.setStyle("-fx-border-color:FF0000");
            usernameRXTxt.setStyle("-fx-border-color:FF0000");
            loginErrorLabel.setVisible(true);
        }

    }

    /***
     *
     * @param event 点击新建账号超链接事件
     */
    @FXML
    void createAccountAction(MouseEvent event) throws SmackException, IOException, XMPPException, InterruptedException {
        //建立新建账号窗体
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(EZChatApplication.class.getResource("fxml/NewAccount.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("New Account");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            //使该窗口弹出时，原登录窗口不能使用
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /***
     *
     * @param event 点击忘记密码超链接事件
     */
    @FXML
    void forgetPasswordAction(MouseEvent event) {
        //建立忘记密码的窗体

    }

    /***
     *
     * @param event 报错信息消除
     */
    @FXML
    void RXTxtMouseClickedAction(MouseEvent event) {
        passwordRXTxt.setStyle("-fx-border-color:FFFFFF");
        usernameRXTxt.setStyle("-fx-border-color:FFFFFF");
        loginErrorLabel.setVisible(false);
    }




}
