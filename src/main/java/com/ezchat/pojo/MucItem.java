package com.ezchat.pojo;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.Jid;

import java.util.Objects;

public class MucItem implements Item {
    private String MUCName;
    private MultiUserChat MUC;
    private Jid MUCjid;

    public String getMUCName() {
        return MUCName;
    }

    public void setMUCName(String MUCName) {
        this.MUCName = MUCName;
    }

    public MultiUserChat getMUC() {
        return MUC;
    }

    public void setMUC(MultiUserChat MUC) {
        this.MUC = MUC;
    }

    public Jid getMUCjid() {
        return MUCjid;
    }

    public void setMUCjid(Jid MUCjid) {
        this.MUCjid = MUCjid;
    }

    public MucItem() {
    }

    public MucItem(String MUCName, MultiUserChat MUC, Jid MUCjid) {
        this.MUCName = MUCName;
        this.MUC = MUC;
        this.MUCjid = MUCjid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MucItem mucItem = (MucItem) o;
        return MUCjid.equals(mucItem.MUCjid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(MUCjid);
    }
}
