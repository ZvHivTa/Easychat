package com.ezchat.controller;

import com.ezchat.utils.AlertWindowUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.apache.oro.text.regex.*;
import org.controlsfx.control.textfield.CustomTextField;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.ezchat.utils.OpenfireConnectUtil.SERVER_IP;
import static com.ezchat.utils.OpenfireConnectUtil.SERVER_NAME;

public class NewAccountController {
    //电子邮箱文本框
    @FXML
    private CustomTextField emailTxt;
    //昵称文本框
    @FXML
    private CustomTextField nicknameTxt;
    //密码文本框
    @FXML
    private CustomTextField passwordTxt;
    //用户名文本框
    @FXML
    private CustomTextField usernameTxt;

    //电子邮件格式错误提示
    @FXML
    private Label WrongEmailFormatLabel;
    //非法用户名错误提示
    @FXML
    private Label illegalUsernameLabel;

    //密码格式错误提示
    @FXML
    private Label illegalPasswordFormatLabel;
    //建立账号专用的连接对象
    private XMPPTCPConnection xmpptcpConnection;

    /***
     * @param event 创建用户事件
     */
    @FXML
    void AccountCreateAction(ActionEvent event)  {
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();
        String nickname = nicknameTxt.getText();
        String email = emailTxt.getText();
        //验证格式,格式正确再执行
        if(validateForm(username,password,email)) {
            AccountManager accountManager = AccountManager.getInstance(this.xmpptcpConnection);
            //允许敏感操作
            accountManager.sensitiveOperationOverInsecureConnection(true);
            Map<String, String> attributes = new HashMap<>(2);
            if(nickname!=null){
                attributes.put("name", nickname);
            }
            attributes.put("email", email);


            //根据信息建立账号
            try {
                accountManager.createAccount(Localpart.from(username), password, attributes);
            } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException |
                     SmackException.NoResponseException | XmppStringprepException | InterruptedException e) {
                //异常处理
                String s = "Register Error";
                //属于xmpp异常，代表连接没有问题，处理请求数据包产生错误
                if(e instanceof XMPPException.XMPPErrorException){
                    //获取类型
                    StanzaError.Type type = ((XMPPException.XMPPErrorException) e).getStanzaError().getType();
                    //cancel代表请求送达，但用户名重复被取消注册
                    if(type== StanzaError.Type.CANCEL){
                        AlertWindowUtil.setAlertWindowAndShow(Alert.AlertType.ERROR,"Username is duplicated !",s);
                    }else{
                        //其他类型基本不会在环节发生
                        AlertWindowUtil.setAlertWindowAndShow(Alert.AlertType.ERROR,"Unknown server error!",s);
                    }
                } else if (e instanceof SmackException.NotConnectedException) {
                    //未连接
                    AlertWindowUtil.setAlertWindowAndShow(Alert.AlertType.ERROR,"Your have not connected to openfire server!",s);
                }else if(e instanceof SmackException.NoResponseException){
                    //连接超时
                    AlertWindowUtil.setAlertWindowAndShow(Alert.AlertType.ERROR,"Connection timeout!",s);
                }else{
                    AlertWindowUtil.setAlertWindowAndShow(Alert.AlertType.ERROR,"Unknown server error!",s);
                }
                //throw new RuntimeException(e);
            }
            //成功登录
            AlertWindowUtil.setAlertWindowAndShow(Alert.AlertType.INFORMATION,"Create success! Now you can sign in to chat!","Register info");
            this.xmpptcpConnection.disconnect();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        }
    }

    /***
     * 回到登陆界面
     * @param event
     */
    @FXML
    void BacktoSigninPageAction(ActionEvent event) {
        if(this.xmpptcpConnection.isConnected()){
            this.xmpptcpConnection.disconnect();
            this.xmpptcpConnection = null;
        }
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    /***
     * 验证表单
     * @param username 用户名
     * @param password 密码
     * @param email 电子邮箱
     * @return
     */

    private boolean validateForm(String username, String password, String email){
        boolean isLegal;
        //正则匹配工具
        PatternCompiler compiler = new Perl5Compiler();
        PatternMatcher matcher=new Perl5Matcher();
        //pattern01用于匹配：只允许字母＋数字,6-30长度
        Pattern pattern01 = null;
        //pattern02用于匹配：邮箱格式
        Pattern pattern02 = null;
        try {
            pattern01=compiler.compile("^[A-Za-z0-9]{6,30}");
            pattern02=compiler.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        } catch (MalformedPatternException e) {
            throw new RuntimeException(e);
        }
        
        boolean a = matcher.matches(username,pattern01);
        boolean b = matcher.matches(password,pattern01);
        boolean c = matcher.matches(email,pattern02);

        if(!a){
            this.illegalUsernameLabel.setVisible(true);
        }
        if(!b){
            this.illegalPasswordFormatLabel.setVisible(true);
        }
        if(!c){
            this.WrongEmailFormatLabel.setVisible(true);
        }
        isLegal = a&&b&&c;
        return isLegal;
    }

    //一些恢复错误信息的方法

    @FXML
    void emailTxtClickedAction(MouseEvent event) {
        if(this.WrongEmailFormatLabel.isVisible()){
            this.WrongEmailFormatLabel.setVisible(false);
        }
    }

    @FXML
    void passwordTxtClickedAction(MouseEvent event) {
        if(this.illegalPasswordFormatLabel.isVisible()){
            this.illegalPasswordFormatLabel.setVisible(false);
        }
    }

    @FXML
    void usernameTxtClickedAction(MouseEvent event) {
        if(this.illegalUsernameLabel.isVisible()){
            this.illegalUsernameLabel.setVisible(false);
        }
    }

    //窗体初始化
    @FXML
    public void initialize() throws IOException {
        // 配置XMPP连接
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setHost(SERVER_IP)
                .setPort(5222)
                .setXmppDomain(SERVER_NAME)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .build();

        // 建立XMPP连接
        XMPPTCPConnection connection = new XMPPTCPConnection(config);
        try {
            connection.connect();
            this.xmpptcpConnection = connection;
        } catch (SmackException | IOException | InterruptedException | XMPPException e) {
            e.printStackTrace();
        }

    }


}
