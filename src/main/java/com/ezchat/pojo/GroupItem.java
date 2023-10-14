package com.ezchat.pojo;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.jid.Jid;

import java.util.Map;

public class GroupItem implements Item {
    private String groupName;
    private Map<Jid,RosterEntry> groupEntries;
    private boolean isSharedGroup;

    public GroupItem(String groupName, Map<Jid, RosterEntry> groupEntries, boolean isSharedGroup) {
        this.groupName = groupName;
        this.groupEntries = groupEntries;
        this.isSharedGroup = isSharedGroup;
    }

    private GroupItem() {

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Map<Jid,RosterEntry> getGroupEntries() {
        return groupEntries;
    }

    public void setGroupEntries(Map<Jid,RosterEntry> groupEntries) {
        this.groupEntries = groupEntries;
    }

    public boolean isSharedGroup() {
        return isSharedGroup;
    }

    public void setSharedGroup(boolean sharedGroup) {
        isSharedGroup = sharedGroup;
    }
}
