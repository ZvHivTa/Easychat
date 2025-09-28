package com.ezchat.utils;

import com.ezchat.pojo.ConnectionContainer;
import javafx.scene.image.Image;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.Jid;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class EntryUtil {
    /***
     *
     * 获取用户头像信息
     *
     * @param bareJid 要获取的用户的jid
     * @return 用户头像
     */
    public static Image getUserImage(Jid bareJid) {
        VCardManager vCardManager = ConnectionContainer.getVCardManager();
        Image image = null;
        try {
            VCard vcard = vCardManager.loadVCard(bareJid.asEntityBareJidIfPossible());
            if(vcard == null || vcard.getAvatar() == null) {
                return null;
            }
            image = getUserImage(vcard.getAvatar());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /***
     * 以byte数组形式获取头像
     * @param avatarByteArray 头像byte数组
     * @return 头像
     */
    public static Image getUserImage(byte[] avatarByteArray){
        InputStream bais = new ByteArrayInputStream(avatarByteArray);
        return new Image(bais);
    }

    /***
     * 获取当前登录者头像
     * @return 当前登录者头像
     */

    public static Image getUserImage(){
       return getUserImage(ConnectionContainer.getUserJid());
    }
}
