package com.ezchat.pojo;

import com.ezchat.EZChatApplication;
import com.ezchat.controller.FileReceiveBubbleController;
import com.ezchat.utils.ChatUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.sharedgroups.SharedGroupManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 容器类
 *
 *
 */
public class ConnectionContainer {
    //用户的jid
    private static EntityFullJid jid;

    //用户名
    private static String userName;



    //连接对象
    private static XMPPTCPConnection connection;

    //账户管理器
    private static AccountManager accountManager;

    //聊天管理器
    private static ChatManager chatManager;

    //MultiUserChatManager 群聊管理器
    private static MultiUserChatManager multiUserChatManager;

    //BookmarkManager
    private static BookmarkManager bookmarkManager;

    //花名册
    private static Roster roster;
    /*  共享组
       花名册中存储的用户可以理解为一个私有组Friends，它是只对你自己可见的组。
       共享组是自己或他人创建的组别，可以理解成群聊组。
     */
    private static List<String> sharedGroups;


    //VCard管理器，用于获取他人的身份证
    private static VCardManager vCardManager;

    //VCard，自己的“身份证”，无需反复获取
    private static VCard vCard;

    //UserSearchManager,搜索用户的引擎
    private static UserSearchManager userSearchManager;

    private static OfflineMessageManager offlineMessageManager;

    private static FileTransferManager fileTransferManager;


    //MamManager 历史消息管理器
    private static MamManager mamManager;






    private ConnectionContainer() {

    }


    /***
     *
     * @param connection 获取的连接对象
     *
     */
    public ConnectionContainer(XMPPTCPConnection connection) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException, InterruptedException {
        //初始化所有类属性
        ConnectionContainer.connection = connection;
        jid = connection.getUser();
        userName = jid.getLocalpart().toString();
        accountManager = AccountManager.getInstance(connection);
        chatManager = ChatManager.getInstanceFor(connection);
        roster = Roster.getInstanceFor(connection);
        //设置成手动处理订阅模式
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        vCardManager = VCardManager.getInstanceFor(connection);
        //默认的Vcard设置成登录用户的
        vCard = vCardManager.loadVCard(jid.asEntityBareJid());
        multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        bookmarkManager = BookmarkManager.getBookmarkManager(connection);
        sharedGroups = SharedGroupManager.getSharedGroups(connection);
        offlineMessageManager = OfflineMessageManager.getInstanceFor(connection);
        mamManager = MamManager.getInstanceFor(connection);
        fileTransferManager = FileTransferManager.getInstanceFor(connection);
        userSearchManager = new UserSearchManager(connection);
    }



    /***
     * 初始化，给记录窗体的map加聊天服务的监听器
     * @param chatWindowConcurrentHashMap 记录创建窗体的Map
     * @param chatItemConcurrentHashMap   记录chatItem的Map
     * @param chatListView   联系人列表
     */
    public static void addChatManagerListener(ConcurrentHashMap<Jid,ChatWindow> chatWindowConcurrentHashMap,
                                              ConcurrentHashMap<Jid,ChatItem> chatItemConcurrentHashMap,
                                              ObservableList<ChatItem> CHAT_ITEMS,
                                              ListView<ChatItem> chatListView){
        //添加监听
        chatManager.addIncomingListener((from, message, chat) -> {
            ChatWindow chatWindow = chatWindowConcurrentHashMap.get(from);
            FXMLLoader fxmlLoader;
            ObservableMap<String, Object> namespace;
            ChatItem chatItem = chatItemConcurrentHashMap.get(from);

            if(chatWindow==null){
                try {
                    chatWindow = new ChatWindow("fxml/ChatSubwindow.fxml");
                    fxmlLoader = chatWindow.getFxmlLoader();
                    namespace = fxmlLoader.getNamespace();
                    Label usernameLabel =(Label) namespace.get("usernameLabel");
                    usernameLabel.setText(String.valueOf(from.getLocalpart()));
                    VBox vbox_messages = (VBox) namespace.get("vbox_messages");
                    if(!chatItem.isGotHistoryMessages()) {
                        showHistoryMessage(from,vbox_messages);
                    }
                    chatWindowConcurrentHashMap.put(from,chatWindow);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                fxmlLoader = chatWindow.getFxmlLoader();
                namespace = fxmlLoader.getNamespace();
            }

            ChatItem selectedItem = chatListView.getSelectionModel().getSelectedItem();
            AnchorPane pane = chatWindow.getPane();

            //1，未显示过，null 需要显示未读信息                                //不是chatListView选择的目标
            //2,正在显示，StackPane[id=BasicChatStackPane] 不需要显示              //是chatListView选择的目标
            //3,未显示但创建过，StackPane[id=BasicChatStackPane] 需要显示未读信息    //不是chatListView选择的目标
            //4，弹出窗口，null 不需要显示                                         //未必是chatListView选择的目标

            if((!chatItem.equals(selectedItem)&&pane.getParent()!=null)
                    ||(pane.getParent()==null&&pane.getScene()==null)){
                CHAT_ITEMS.remove(chatItem);
                CHAT_ITEMS.set(0,chatItem);
                chatItem.increment();
            }else{
                chatItem.setZero();
            }
            VBox vbox_messages = (VBox) namespace.get("vbox_messages");
            ChatUtil.receiveMessageAction(message,vbox_messages);
        });
    }

    /***
     * 展示历史消息。有两个时机可以触发这个操作：
     * 1，当jid为from的用户向你发送了离线消息;
     * 2，当jid为from的用户向你发送了在线消息;
     * 3，当你点开了jid为from的用户的聊天界面;
     *
     * @param from 对应聊天用户的jid
     * @param vbox_messages 界面对应的vbox
     *
     */

    public static void showHistoryMessage(Jid from,VBox vbox_messages) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException, SmackException.NotLoggedInException, InterruptedException {
        MamManager.MamQueryArgs queryArgs = MamManager.MamQueryArgs.builder().
                limitResultsToJid(from).
                setResultPageSize(10).
                queryLastPage().
                build();
        MamManager.MamQuery r = null;
        if(mamManager.isSupported()) {
            r = ConnectionContainer.getMamManager().queryArchive(queryArgs);
        }else{
            throw new NullPointerException("MamManager service is not available!") ;
        }
        List<Message> messages = r.getMessages();
        for (Message historyMessage : messages) {
            Jid sender = historyMessage.getFrom().asBareJid();
            if (sender.equals(from)) {
                ChatUtil.receiveHistoryMessageAction(historyMessage, vbox_messages);
            } else {
                ChatUtil.sendMessage(historyMessage.getBody(), vbox_messages);
            }
        }
    }

    public static void addFileTransferListener(ConcurrentHashMap<Jid,ChatWindow> chatWindowConcurrentHashMap){
        fileTransferManager.addFileTransferListener(request -> {
            //前端显示文件传输请求消息
            Jid requestorFullJid = request.getRequestor();
            EntityBareJid requestor = requestorFullJid.asEntityBareJidIfPossible();
            ChatWindow chatWindow = chatWindowConcurrentHashMap.get(requestor);
            FXMLLoader fxmlLoader = chatWindow.getFxmlLoader();
            ObservableMap<String, Object> namespace = fxmlLoader.getNamespace();
            try {
                FXMLLoader fxmlLoader1 = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/FileReceiveBubble.fxml")));
                HBox receiveFileBubble = ChatUtil.createReceiveFileBubble(fxmlLoader1,request);
                VBox vbox_messages = (VBox) namespace.get("vbox_messages");
                Platform.runLater(() -> vbox_messages.getChildren().add(receiveFileBubble));

                //执行接收操作
                FileReceiveBubbleController controller = fxmlLoader1.getController();
                controller.fileReceiveAction(request);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }


    public static void addRosterrListener(){
        if(roster!=null){
            roster.addRosterListener(new RosterListener() {
                @Override
                public void entriesAdded(Collection<Jid> collection) {

                }

                @Override
                public void entriesUpdated(Collection<Jid> collection) {

                }

                @Override
                public void entriesDeleted(Collection<Jid> collection) {

                }

                @Override
                public void presenceChanged(Presence presence) {

                }
            });
        }
    }






    //**********************************************
    //获取成员变量的方法

    public static XMPPTCPConnection getConnection(){
        return connection;
    }

    public static EntityFullJid getUserJid(){
        return jid;
    }

    public static Roster getRoster(){
        return roster;
    }

    public static AccountManager getAccountManager(){return accountManager;}

    public static ChatManager getChatManager(){return chatManager;}

    public static VCardManager getVCardManager(){return vCardManager;}

    public static VCard getVCard(){return vCard;}

    public static UserSearchManager getUserSearchManager(){return userSearchManager;}

    public static MultiUserChatManager getMultiUserChatManager(){return multiUserChatManager;}

    public static BookmarkManager getBookmarkManager(){return bookmarkManager;}

    public static List<String> getSharedGroups(){return sharedGroups;}

    public static MamManager getMamManager() {
        return mamManager;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        ConnectionContainer.userName = userName;
    }

    public static FileTransferManager getFileTransferManager() {
        return fileTransferManager;
    }

    public static void setFileTransferManager(FileTransferManager fileTransferManager) {
        ConnectionContainer.fileTransferManager = fileTransferManager;
    }

    public static void removeListener(){
        //fileTransferManager.removeFileTransferListener();
    }



}
