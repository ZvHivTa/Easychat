package com.ezchat.utils;

import com.ezchat.pojo.ChatItem;
import com.ezchat.pojo.ChatWindow;
import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.pojo.MucItem;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.XmlElement;
import org.jivesoftware.smack.util.MultiMap;
import org.jivesoftware.smackx.address.packet.MultipleAddresses;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MUCutil {
    private static final MultiUserChatManager multiUserChatManager = ConnectionContainer.getMultiUserChatManager();

    public static MultiUserChat getMUCbyJid(String MUCname, DomainBareJid Domain) throws XmppStringprepException {
        String roomJid = MUCname+"@"+Domain.toString();
        EntityBareJid roomBareJid = JidCreate.entityBareFrom(roomJid);
        return multiUserChatManager.getMultiUserChat(roomBareJid);
    }

    public static boolean domainCertificate(String Domain) throws Exception{
        String domainPattern = Domain+"."+OpenfireConnectUtil.SERVER_NAME;
        List<DomainBareJid> mucServiceDomains = multiUserChatManager.getMucServiceDomains();
        return mucServiceDomains.contains(JidCreate.domainBareFrom(domainPattern));
    }

    public static ConcurrentHashMap<Jid,MucItem> getAllMUCbyDomainInMap(DomainBareJid domainBareJid) throws Exception{
        Map<EntityBareJid, HostedRoom> roomsHostedBy = multiUserChatManager.getRoomsHostedBy(domainBareJid);
        Set<EntityBareJid> entityBareJids = roomsHostedBy.keySet();
        ConcurrentHashMap<Jid,MucItem> mucMap = new ConcurrentHashMap<>();
        for (EntityBareJid jid:entityBareJids) {
            mucMap.put(jid,createMucItem(jid));
        }
        return mucMap;
    }

    public static ConcurrentHashMap<Jid,MucItem> getAllMUCbyDomainInMap(String groupName) throws Exception {
        String domainPattern = groupName+"."+ OpenfireConnectUtil.SERVER_NAME;
        DomainBareJid domainBareJid = JidCreate.domainBareFrom(domainPattern);
        return getAllMUCbyDomainInMap(domainBareJid);
    }



    public static void joinInMucs(List<MucItem> mucItems,
                                  ConcurrentHashMap<Jid, ChatWindow> CHAT_WINDOW_CONCURRENT_HASH_MAP,
                                  ConcurrentHashMap<Jid, ChatItem> CHAT_ITEM_CONCURRENT_HASH_MAP) throws Exception{
        for (MucItem mucItem: mucItems) {
            MultiUserChat muc = mucItem.getMUC();
            muc.join(ConnectionContainer.getUserJid().getResourceOrThrow());
            addMUCListener(mucItem,CHAT_WINDOW_CONCURRENT_HASH_MAP,CHAT_ITEM_CONCURRENT_HASH_MAP);
        }
    }

    public static MucItem createMucItem(EntityBareJid jid){
        MultiUserChat muc = multiUserChatManager.getMultiUserChat(jid);
        String jidString = jid.toString();
        String name = jidString.split("@")[0];
        return new MucItem(name, muc, jid);
    }

    public static void addMUCListener(MucItem item,
                                      ConcurrentHashMap<Jid, ChatWindow> CHAT_WINDOW_CONCURRENT_HASH_MAP,
                                      ConcurrentHashMap<Jid, ChatItem> CHAT_ITEM_CONCURRENT_HASH_MAP){
        MultiUserChat muc = item.getMUC();
        muc.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                Jid from = message.getFrom();

                // {namespaceURI} localpart

                //[{jabber:client}body, 消息体
                // {jabber:x:event}x,   消息类型
                // {urn:xmpp:sid:0}origin-id,
                // {urn:xmpp:sid:0}stanza-id,
                // {http://jabber.org/protocol/address}addresses, 具体的消息发送者ofrom
                // {urn:xmpp:delay}delay]

                MultiMap<QName, XmlElement> extensionsMap = message.getExtensionsMap();
                QName addressesQname = new QName("http://jabber.org/protocol/address","addresses");

                List<XmlElement> addressesList = extensionsMap.getAll(addressesQname);
                Jid jid = null;
                if(!addressesList.isEmpty()) {
                    MultipleAddresses addresses  = (MultipleAddresses) addressesList.get(0);
                    List<MultipleAddresses.Address> addressesOfType = addresses.getAddressesOfType(MultipleAddresses.Type.ofrom);
                    MultipleAddresses.Address address = addressesOfType.get(0);
                    jid = address.getJid();
                }

                if(jid!=null) {
                    String body = message.getBody();
                    ChatItem chatItem = null;
                    if (!CHAT_ITEM_CONCURRENT_HASH_MAP.containsKey(jid)) {
                        try {
                            Image userImage = EntryUtil.getUserImage(jid.asEntityBareJidIfPossible());
                            String username = jid.toString().split("@")[0];
                            chatItem = new ChatItem(jid, username, userImage);
                            CHAT_ITEM_CONCURRENT_HASH_MAP.put(jid, chatItem);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        chatItem = CHAT_ITEM_CONCURRENT_HASH_MAP.get(jid);
                    }

                    if (chatItem != null) {
                        Jid MUCjid = item.getMUCjid();
                        ChatWindow chatWindow = null;
                        do {
                            chatWindow = CHAT_WINDOW_CONCURRENT_HASH_MAP.get(MUCjid);
                        } while (chatWindow == null);

                        FXMLLoader fxmlLoader = chatWindow.getFxmlLoader();
                        ObservableMap<String, Object> namespace = fxmlLoader.getNamespace();
                        VBox vbox_messages = (VBox) namespace.get("vbox_messages");
                        try {
                            HBox bubble = null;
                            if(!jid.equals(ConnectionContainer.getUserJid().asEntityBareJidIfPossible())) {
                                bubble = ChatUtil.createMUCReceiveBubble(message, chatItem);
                            }else{
                                bubble = ChatUtil.createMUCSendBubble(message);
                            }
                            HBox finalBubble = bubble;
                            Platform.runLater(() -> vbox_messages.getChildren().add(finalBubble));

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

}
