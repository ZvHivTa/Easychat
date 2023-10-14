package com.ezchat.utils;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/***
 * 组操作类
 */
public class RosterUtil {
    /***
     * 返回所有组信息
     * @param roster
     * @return
     */
    public static List<RosterGroup> getGroups(Roster roster) {
        Collection<RosterGroup> rosterGroup = roster.getGroups();

        return new ArrayList<>(rosterGroup);
    }

    /**
     * 返回相应(groupName)组里的所有用户<RosterEntry>
     *
     * @return List(RosterEntry)
     */
    public static List<RosterEntry> getEntriesByGroup(Roster roster,
                                                      String groupName) {
        RosterGroup rosterGroup = roster.getGroup(groupName);
        List<RosterEntry> rosterEntry = new ArrayList<>();
        if(rosterGroup!=null) {
           rosterEntry = rosterGroup.getEntries();
        }
        return rosterEntry;
    }

    /**
     * 返回所有用户信息 <RosterEntry>
     *
     * @return List(RosterEntry)
     */
    public static List<RosterEntry> getAllEntries(Roster roster) {
        Collection<RosterEntry> rosterEntry = roster.getEntries();
        return new ArrayList<>(rosterEntry);
    }

    /**
     * 添加一个分组
     *
     * @param groupName
     * @return
     */
    public static boolean addGroup(Roster roster,String groupName) {
        boolean isSuccess = false;
        try {
            roster.createGroup(groupName);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 添加好友 无分组
     *
     * @param name
     * @return
     */
    public boolean addUser(Roster roster, Jid jid, String name) {
        boolean isSuccess = false;
        try {
            roster.createItem((BareJid) jid,name,null);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
