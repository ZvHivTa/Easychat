package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.pojo.*;
import com.ezchat.utils.EntryUtil;
import com.ezchat.utils.MUCutil;
import com.ezchat.utils.OpenfireConnectUtil;
import com.ezchat.utils.RosterUtil;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.*;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MainController {

    @FXML
    private StackPane BasicChatStackPane;
    @FXML
    private ListView<ChatItem> chatListView;
    @FXML
    private Pane ListViewPane;

    @FXML
    private ListView<GroupItem> groupListView;

    @FXML
    private AnchorPane pane1;

    @FXML
    private AnchorPane sidebarMenu;

    @FXML
    private ImageView menuButton;


    @FXML
    private Circle imageCircle;

    @FXML
    private Label sidebarUsername;

    @FXML
    private ListView<MucItem> MucListview;


    @FXML
    private TextField searchTxt;

    //sidebar动画控制
    private static boolean b = false;


    //储存好友列表中的chatItem
    private static final ObservableList<ChatItem> CHAT_ITEMS = FXCollections.observableArrayList();

    private static final ObservableList<GroupItem> GROUP_ITEMS = FXCollections.observableArrayList();

    private static final ObservableList<MucItem> MUC_ITEMS = FXCollections.observableArrayList();

    //储存列表对象的map，一个聊天对应列表中的一个项。

    private static final ConcurrentHashMap<Jid,ChatItem> CHAT_ITEM_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    //储存子窗对象的map，一个聊天(包括群聊)对应一个窗体。
    private static final ConcurrentHashMap<Jid,ChatWindow> CHAT_WINDOW_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    //储存群聊对象的map，一个聊天服务（群组）对应多个群聊。
    private static final ConcurrentHashMap<DomainBareJid,ConcurrentHashMap<Jid,MucItem>> MUC_ITEM_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();


    /***
     * 聊天主界面样式初始化
     */
    public void chatComponentsInit() throws IOException {
        ChatWindow chatWindow = new ChatWindow("fxml/InitChatAnchorPane.fxml");
        showSelectedPane(chatWindow);
        CHAT_WINDOW_CONCURRENT_HASH_MAP.put(ConnectionContainer.getUserJid(),chatWindow);

        ListViewPane.getStylesheets().add(EZChatApplication.class.getResource("css/ScrollbarCss.css").toExternalForm());
        chatListView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MouseButton button = mouseEvent.getButton();
                if(button==MouseButton.SECONDARY){
                    //点击到的只能是ChatListviewCell的子组件
                    Node node = mouseEvent.getPickResult().getIntersectedNode();
                    Parent parent = node.getParent();
                    //寻找ChatListviewCell类型，如果不是说明点错了位置
                    while(parent!=null&&!(parent instanceof ChatListviewCell)){
                        parent = parent.getParent();
                    }
                    if(parent != null) {
                        //加右键菜单
                        SecondaryMouseButtonClickedMenu.getMenu(MainController.this).show(node, Side.BOTTOM, 0, 0);
                        ChatListviewCell cell = (ChatListviewCell) parent;
                        ChatItem item = cell.getItem();
                        SecondaryMouseButtonClickedMenu.setSelectedChatItem(item);
                    }
                }
            }
        });



        //侧边菜单展示设置

        pane1.setVisible(false);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5),pane1);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        TranslateTransition t = new TranslateTransition(Duration.seconds(0.5), sidebarMenu);
        t.setByX(-200);
        t.play();



        menuButton.setOnMouseClicked(mouseEvent -> {
            if(!b) {
                pane1.setVisible(true);

                FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
                fadeTransition1.setFromValue(0);
                fadeTransition1.setToValue(0.15);
                fadeTransition1.play();
                fadeTransition1.setOnFinished(actionEvent -> {
                    b = true;
                });
                TranslateTransition t1 = new TranslateTransition(Duration.seconds(0.5), sidebarMenu);
                t1.setByX(+200);
                t1.play();
            }
        });

        pane1.setOnMouseClicked(mouseEvent -> {
            if(b) {
                FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
                fadeTransition1.setFromValue(1);
                fadeTransition1.setToValue(0);
                fadeTransition1.play();
                fadeTransition1.setOnFinished(actionEvent -> {
                    b = false;
                    pane1.setVisible(false);
                });
                TranslateTransition t1 = new TranslateTransition(Duration.seconds(0.5), sidebarMenu);
                t1.setByX(-200);
                t1.play();
            }
        });

        sidebarUsername.setText(ConnectionContainer.getUserJid().getLocalpart().toString());
        Image userImage = EntryUtil.getUserImage();
        if(userImage!=null){
            imageCircle.setFill(new ImagePattern(userImage));
        }

        imageCircle.setEffect(new DropShadow(+25d,0d,+2d, Color.DARKSLATEGRAY));

    }

    /**
     * 聊天服务初始化
     */
    public void chatServiceInit(){
        //获取花名册
        Roster roster = ConnectionContainer.getRoster();
        //共享组，也就是公共群组
        List<String> sharedGroups = ConnectionContainer.getSharedGroups();
        //创建组对象，装填组列表
        for (String groupname:sharedGroups) {
            List<RosterEntry> sharedgroupEntries = RosterUtil.getEntriesByGroup(roster,groupname);
            Map<Jid,RosterEntry> map = new HashMap<>();
            for (RosterEntry entry:sharedgroupEntries) {
                map.put(entry.getJid(),entry);
            }
            GROUP_ITEMS.add(new GroupItem(groupname,map,true));
        }


        //得到好友组
        List<RosterEntry> friends = RosterUtil.getEntriesByGroup(roster,"Friends");
        Map<Jid,RosterEntry> map = new HashMap<>();
        for (RosterEntry entry:friends) {
            map.put(entry.getJid(),entry);
        }
        GroupItem friendItem = new GroupItem("Friends", map, false);
        GROUP_ITEMS.add(0,friendItem);
        groupListView.setItems(GROUP_ITEMS);
        groupListView.setCellFactory(listView -> new GroupListviewCell());
        //默认选择第一个，也就是好友组
        if (!groupListView.getItems().isEmpty()) {
            groupListView.getSelectionModel().select(0);
        }

        groupListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GroupItem>() {
            @Override
            public void changed(ObservableValue<? extends GroupItem> observableValue, GroupItem oldGroupItem, GroupItem newGroupItem) {

                Map<Jid,RosterEntry> groupEntries = newGroupItem.getGroupEntries();
                String groupName = newGroupItem.getGroupName();
                showSelectedGroupEntries(groupEntries);
                try {
                    showSelectedGroupMUCs(groupName);
                } catch (XmppStringprepException e) {
                    throw new RuntimeException(e);
                }
                chatListView.getSelectionModel().select(0);
            }
        });

        //装填好友组信息，初始化选择好友组展示
        showSelectedGroupEntries(friendItem.getGroupEntries());
        chatListView.setItems(CHAT_ITEMS);
        FXCollections.sort(CHAT_ITEMS, new Comparator<ChatItem>() {
            @Override
            public int compare(ChatItem item1, ChatItem item2) {
                String username1 = item1.getUsername();
                String username2 = item2.getUsername();
                return username1.compareTo(username2);
            }
        });
        chatListView.setCellFactory(listView -> new ChatListviewCell());


        //给每个聊天创建对应的子窗
        chatListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ChatItem>() {
            @Override
            public void changed(ObservableValue<? extends ChatItem> observableValue, ChatItem oldSelectedItem, ChatItem chatItem) {
                if(chatItem==null){
                    return;
                }
                Jid chatItemJid = chatItem.getJid();
                chatItem.setZero();
                if(!CHAT_WINDOW_CONCURRENT_HASH_MAP.containsKey(chatItemJid)){
                    //不包括对应id的聊天窗体，说明是新聊天。
                    try {
                        ChatWindow window = new ChatWindow("fxml/ChatSubwindow.fxml");
                        FXMLLoader fxmlLoader = window.getFxmlLoader();

                        //初始化窗体
                        ObservableMap<String, Object> namespace = fxmlLoader.getNamespace();
                        Label usernameLabel =(Label) namespace.get("usernameLabel");
                        usernameLabel.setText(chatItem.getUsername());
                        VBox vbox_messages = (VBox) namespace.get("vbox_messages");
                        ConnectionContainer.showHistoryMessage(chatItemJid,vbox_messages);
                        chatItem.setGotHistoryMessages(true);
                        //展示
                        showSelectedPane(window);
                        //设置jid标识
                        ChatSubwindowController controller = fxmlLoader.getController();
                        controller.setJid(chatItemJid);
                        controller.setChatItem(chatItem);
                        //加入map
                        CHAT_WINDOW_CONCURRENT_HASH_MAP.put(chatItemJid,window);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    showSelectedPane(CHAT_WINDOW_CONCURRENT_HASH_MAP.get(chatItem.getJid()));
                }
                MucListview.getSelectionModel().select(-1);
            }
        });

        MultiUserChatManager multiUserChatManager = ConnectionContainer.getMultiUserChatManager();

        for (String groupName:sharedGroups){
            List<MucItem> allMUCbyDomain = null;
            String domainPattern = groupName+"."+ OpenfireConnectUtil.SERVER_NAME;
            try {
                DomainBareJid domainBareJid = JidCreate.domainBareFrom(domainPattern);

                ConcurrentHashMap<Jid, MucItem> MUCMap = MUCutil.getAllMUCbyDomainInMap(groupName);
                allMUCbyDomain = MUCMap.values().stream().toList();
                MUCutil.joinInMucs(allMUCbyDomain,CHAT_WINDOW_CONCURRENT_HASH_MAP,CHAT_ITEM_CONCURRENT_HASH_MAP);
                if(!allMUCbyDomain.isEmpty()) {
                    MUC_ITEM_CONCURRENT_HASH_MAP.put(domainBareJid,MUCMap);
                    for(MucItem item:allMUCbyDomain){
                        ChatWindow window = new ChatWindow("fxml/ChatSubwindow.fxml");
                        FXMLLoader fxmlLoader = window.getFxmlLoader();

                        //初始化窗体
                        ObservableMap<String, Object> namespace = fxmlLoader.getNamespace();
                        Label usernameLabel =(Label) namespace.get("usernameLabel");
                        usernameLabel.setText(item.getMUCName());

                        Label groupNameLabel = (Label) namespace.get("groupNameLabel");
                        groupNameLabel.setVisible(true);
                        groupNameLabel.setText(groupName);

                        ChatSubwindowController controller = fxmlLoader.getController();
                        controller.setJid(item.getMUCjid());
                        controller.setChatItem(item);
                        CHAT_WINDOW_CONCURRENT_HASH_MAP.put(item.getMUCjid(),window);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }


        MucListview.setItems(MUC_ITEMS);
        MucListview.setCellFactory(listView -> new MucListviewCell());
        MucListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MucItem>() {
            @Override
            public void changed(ObservableValue<? extends MucItem> observableValue, MucItem mucItem, MucItem newMUC) {
                if(newMUC==null){
                    return;
                }
                ChatWindow chatWindow = CHAT_WINDOW_CONCURRENT_HASH_MAP.get(newMUC.getMUCjid());
                if(chatWindow!=null){
                    showSelectedPane(chatWindow);
                }
                chatListView.getSelectionModel().select(-1);
            }
        });

//        try {
//            //得到全部群聊域名
//            //域名应和公共组名相同
//            List<DomainBareJid> mucServiceDomains = multiUserChatManager.getMucServiceDomains();
//            String roomName = "public";
//            String roomJid = roomName+"@"+mucServiceDomains.get(1).toString();
//            //根据对应的jid获取muc对象
//            MultiUserChat muc = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom(roomJid));
//            //加入房间
//            muc.join(ConnectionContainer.getUserJid().getResourcepart());
//            //获取jid
//            EntityBareJid room = muc.getRoom();
//            //获取频道拥有者
//            List<Affiliate> owners = muc.getOwners();
//            //获取管理员
//            List<Affiliate> admins = muc.getAdmins();
//            //获取成员
//            List<Affiliate> members = muc.getMembers();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

//        Set<EntityBareJid> joinedRooms = multiUserChatManager.getJoinedRooms();
//        System.out.println(joinedRooms);

//
//        try {
//            RoomInfo roomInfo = multiUserChatManager.getRoomInfo(JidCreate.entityBareFrom("groupchat01@conference.vm-4-9-centos"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        BookmarkManager bookmarkManager = ConnectionContainer.getBookmarkManager();
//        try {
//            List<BookmarkedURL> bookmarkedURLs = bookmarkManager.getBookmarkedURLs();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        ConnectionContainer.addChatManagerListener(CHAT_WINDOW_CONCURRENT_HASH_MAP,CHAT_ITEM_CONCURRENT_HASH_MAP,CHAT_ITEMS,chatListView);
        ConnectionContainer.addFileTransferListener(CHAT_WINDOW_CONCURRENT_HASH_MAP);


        //先离线接收离线消息，后上线。
        Presence presence = PresenceBuilder.buildPresence().build();
        try {
            ConnectionContainer.getConnection().sendStanza(presence);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }




    /**
     * 初始化方法
     */
    @FXML
    public void initialize(){
        this.chatServiceInit();
        try {
            this.chatComponentsInit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * 把特定的聊天转为单独的窗体
     * @param selectedChatItem 被选中的聊天
     *
     */
    public void createChatWindow(ChatItem selectedChatItem) throws IOException {
        Stage stage = new Stage();
        ChatWindow chatWindow = CHAT_WINDOW_CONCURRENT_HASH_MAP.get(selectedChatItem.getJid());
        AnchorPane pane = chatWindow.getPane();
        StackPane stackPane = (StackPane) pane.getParent();
        ChatItem chatItem = CHAT_ITEM_CONCURRENT_HASH_MAP.get(selectedChatItem.getJid());
        chatItem.setZero();

        AnchorPane paneForInit = CHAT_WINDOW_CONCURRENT_HASH_MAP.get(ConnectionContainer.getUserJid()).getPane();
        ObservableList<Node> children = stackPane.getChildren();
        children.remove(pane);
        Platform.runLater(() -> {
            stage.setScene(new Scene(pane));
            for (Node n: children) {
                n.setVisible(n.equals(paneForInit));
            }
        });
        stage.setOnCloseRequest(new EventHandler<>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                children.add(pane);
                pane.setVisible(false);
                //不选择任何一项
                chatListView.getSelectionModel().select(-1);
            }
        });
        stage.setTitle(selectedChatItem.getUsername());
        stage.setResizable(false);
        stage.showAndWait();
    }



    /***
     * 展示对应的界面，看需要是否加入stackpane中
     * @param window 要加入的界面
     */
    public void showSelectedPane(ChatWindow window){
        // pane的父节点是空，可能是以下情况：
        // 1，还没显示过，第一次加入stackpane；
        // 2，已经从stackpane中移除到窗口显示；
        // 3,已经接收到消息，并在后台被创建，但是没有让它显示过；
        AnchorPane pane = window.getPane();
        if(pane.getParent()==null){
            //第一次加入stackpane,mark数组中一定没有该pane;
            if(!CHAT_WINDOW_CONCURRENT_HASH_MAP.contains(window)){
                ObservableList<Node> children = BasicChatStackPane.getChildren();
                children.add(pane);
                pane.setVisible(true);
            }else{
                if(pane.getScene()==null){
                    //虽然pane被创建了，但是它还未显示过，也就是说没有加入到StackPane中去；
                    BasicChatStackPane.getChildren().add(pane);
                    pane.setVisible(true);
                }else{
                    //不是第一次添加，说明已经移出到窗口中显示,把窗口置于顶层。
                    Stage window1 = (Stage) pane.getScene().getWindow();
                    window1.setAlwaysOnTop(true);
                    window1.setAlwaysOnTop(false);
                }
            }
        }else{
            //父节点不是空，说明已经加入了stackpane，只是想让它显示
            ObservableList<Node> children = BasicChatStackPane.getChildren();
            for (Node n : children) {
                n.setVisible(n.equals(pane));
            }
        }
    }

    public void setChatListViewSelectedItem(Jid jid){
        ChatItem chatItem = CHAT_ITEM_CONCURRENT_HASH_MAP.get(jid);
        chatListView.getSelectionModel().select(chatItem);
    }

    /***
     * 展示被选择组的全部成员
     * @param rosterEntries 成员Map
     */
    private void showSelectedGroupEntries(Map<Jid,RosterEntry> rosterEntries) {
        Set<Jid> jids = rosterEntries.keySet();
        //必须对keyset进行深拷贝再操作，否则会联动删除rosterEntries中的元素。
        HashSet<Jid> newjid = new HashSet<>(jids);
        List<Jid> removeList = new ArrayList<>();
        for(ChatItem entry:CHAT_ITEMS){
            Jid jid = entry.getJid();
            //不应存在的item
            if(!rosterEntries.containsKey(jid)) {
                removeList.add(jid);
            }else{
                //应存在但是已经被创建
                newjid.remove(jid);
            }
        }
        //移除
        for (Jid jid:removeList) {
            CHAT_ITEMS.remove(CHAT_ITEM_CONCURRENT_HASH_MAP.get(jid));
        }

        //应存在但是未加入
        for (Jid jid: newjid) {
            RosterEntry entry = rosterEntries.get(jid);
            //没创建
            if(!CHAT_ITEM_CONCURRENT_HASH_MAP.containsKey(jid)) {
                try {
                    String username = entry.getJid().getLocalpartOrNull().toString();
                    Image userImage = EntryUtil.getUserImage(jid.asEntityBareJidIfPossible());
                    ChatItem chatItem = new ChatItem(entry.getJid(), username, userImage);
                    CHAT_ITEMS.add(chatItem);
                    CHAT_ITEM_CONCURRENT_HASH_MAP.put(jid, chatItem);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                //创建了但是被移出
                CHAT_ITEMS.add(CHAT_ITEM_CONCURRENT_HASH_MAP.get(jid));
            }
        }
    }

    /***
     * 展示选中的组的全部群聊
     *
     * @param groupName 组名
     *
     */

    private void showSelectedGroupMUCs(String groupName) throws XmppStringprepException {
        MUC_ITEMS.clear();
        if(groupName.equals("Friends")){
            return;
        }
        //设置的所有共享组和群聊服务的名称一致。
        String domainPattern = groupName+"."+ OpenfireConnectUtil.SERVER_NAME;
        DomainBareJid domainBareJid = JidCreate.domainBareFrom(domainPattern);
        try {
            if(MUCutil.domainCertificate(groupName)){
                if(!MUC_ITEM_CONCURRENT_HASH_MAP.containsKey(domainBareJid)){
                    ConcurrentHashMap<Jid, MucItem> allMUCbyDomainInMap = MUCutil.getAllMUCbyDomainInMap(domainBareJid);
                    if(!allMUCbyDomainInMap.isEmpty()) {
                        MUC_ITEMS.addAll(allMUCbyDomainInMap.values());
                        MUC_ITEM_CONCURRENT_HASH_MAP.put(domainBareJid,allMUCbyDomainInMap);
                    }
                }else{
                    ConcurrentHashMap<Jid, MucItem> allMUCbyDomainInMap = MUC_ITEM_CONCURRENT_HASH_MAP.get(domainBareJid);
                    MUC_ITEMS.addAll(allMUCbyDomainInMap.values());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void personalInfoAction(MouseEvent event) {
        Stage infoStage = new Stage();
        infoStage.setAlwaysOnTop(true);
        infoStage.initStyle(StageStyle.UNDECORATED);
        infoStage.setResizable(false);
        infoStage.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/UserInformation.fxml")));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        infoStage.setScene(scene);
        infoStage.show();

        UserInformationController controller = fxmlLoader.getController();
        try {
            controller.setChatItem(CHAT_ITEM_CONCURRENT_HASH_MAP.get(ConnectionContainer.getUserJid().asEntityBareJidIfPossible()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void searchAction(MouseEvent event) {
        String text = searchTxt.getText();
        if(text!=null&&!text.equals("")){
            FilteredList<ChatItem> filteredItems = new FilteredList<>(CHAT_ITEMS);
            filteredItems.setPredicate(item -> item.getUsername().contains(text)); // 根据条件筛选元素
            chatListView.setItems(filteredItems); // 将FilteredList设置为ListView的显示列表
        }else{
            chatListView.setItems(CHAT_ITEMS);
        }
    }

    @FXML
    void addFriendAction(MouseEvent event) {
        Stage sidemenuStage = new Stage();
        sidemenuStage.setAlwaysOnTop(true);
        sidemenuStage.initStyle(StageStyle.UNDECORATED);
        sidemenuStage.setResizable(false);
        sidemenuStage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(EZChatApplication.class.getResource("fxml/AddFriendWindow.fxml")));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sidemenuStage.setScene(scene);
        sidemenuStage.show();

        AddFriendController controller = fxmlLoader.getController();

    }

    public static ConcurrentHashMap<DomainBareJid,ConcurrentHashMap<Jid, MucItem>> getMucItemConcurrentHashMap(){
        return MUC_ITEM_CONCURRENT_HASH_MAP;
    }
}
