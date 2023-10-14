package com.ezchat.pojo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.jxmpp.jid.Jid;

public class ChatItem implements Item {
    private Jid jid;
    private String username;
    private Image avatar;
    private StringProperty newMessageNumber;

    private boolean gotHistoryMessages;

    public boolean isGotHistoryMessages() {
        return gotHistoryMessages;
    }

    public void setGotHistoryMessages(boolean gotHistoryMessages) {
        this.gotHistoryMessages = gotHistoryMessages;
    }

    public StringProperty getNewMessageNumber() {
        return newMessageNumber;
    }

    public void setNewMessageNumber(StringProperty newMessageNumber) {
        this.newMessageNumber = newMessageNumber;
    }

    public ChatItem() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public Jid getJid() {
        return jid;
    }

    public void setJid(Jid jid) {
        this.jid = jid;
    }

    public ChatItem(Jid jid, String username, Image avatar) {
        this.jid = jid;
        this.username = username;
        this.avatar = avatar;
        this.newMessageNumber = new SimpleStringProperty("0");
    }
    public void increment(){
        int i = Integer.parseInt(newMessageNumber.getValue());
        i++;
        newMessageNumber.set(Integer.toString(i));
    }

    public void setZero(){
        newMessageNumber.set("0");
    }
}
