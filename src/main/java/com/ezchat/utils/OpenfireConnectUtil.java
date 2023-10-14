package com.ezchat.utils;

import com.ezchat.pojo.ConnectionContainer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

/***
 * 连接配置类
 */
public class OpenfireConnectUtil {
    //服务器ip
    public static final String SERVER_IP = "124.222.113.225";
    //服务器名称
    public static final String SERVER_NAME = "vm-4-9-centos";
    //客户端Resource名称
    public static final String RESOURCE_NAME = "ezchat";

    //连接配置工厂
    private static final XMPPTCPConnectionConfiguration.Builder connectionBuilder;

    static{
        connectionBuilder = XMPPTCPConnectionConfiguration.builder();
    }

    /***
     *
     * @param username 用户名
     * @param password 密码
     * @return XMPP连接对象
     */
    public static XMPPTCPConnection initOpenfireConnect(String username, String password) throws XmppStringprepException {
        XMPPTCPConnection connection;
        String jid = username+"@"+SERVER_IP;
        //资源名：用户名@ezchat
        String resourceName = username+"@"+RESOURCE_NAME;
        //设置jid和密码
        connectionBuilder.setXmppAddressAndPassword(jid,password)
                //不进行安全验证，openfire的安全验证不允许服务器端和客户端在同一台机器上。
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setResource(resourceName)//设置配置好的资源名
                .setSendPresence(false)//登录前不上线，为了获取离线消息
                .setHost(SERVER_IP)//设置服务器的IP地址
                .setXmppDomain(SERVER_NAME)//设置服务器名称，在openfire的主管理界面上显示
                //.enableDefaultDebugger()//启用默认的数据包监测。
                .setPort(5222);
        connection = new XMPPTCPConnection(connectionBuilder.build());
        // 连接服务器
        try {
            connection.connect();
        }catch (Exception e){
            return null;
        }
        return connection;
    }

    public static ConnectionContainer connectionLogin(XMPPTCPConnection connection)
            throws IOException,
            SmackException,
            XMPPException,
            InterruptedException {
        //添加好友消息设置为人工处理
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
        //登录
        connection.login();

        return new ConnectionContainer(connection);
    }

    public static XMPPTCPConnection initUnloginOpenfireConnect() throws IOException {
        XMPPTCPConnection xmpptcpConnection = initOpenfireConnect("admin", "admin");
        return xmpptcpConnection;
    }


    private OpenfireConnectUtil(){

    }

}
