package com.ezchat.controller;

import com.ezchat.pojo.ConnectionContainer;
import com.ezchat.utils.OpenfireConnectUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.form.Form;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.util.HashMap;
import java.util.List;

public class AddFriendController {
    @FXML
    private Circle avatarCircle;

    @FXML
    private ImageView closeWindow;

    @FXML
    private Label jidLabel;

    @FXML
    private AnchorPane resultPane;

    @FXML
    private TextField searchTxt;

    @FXML
    private Label username;

    @FXML
    void addFriendAction(MouseEvent event) {

    }

    @FXML
    void cancelAction(MouseEvent event) {

    }

    @FXML
    void closeWindowAction(MouseEvent event) {
        resultPane.getScene().getWindow().hide();
    }

    @FXML
    void searchAction(MouseEvent event) {
        String text = searchTxt.getText();
        if(text!=null&&!text.equals("")){
            UserSearchManager userSearchManager = ConnectionContainer.getUserSearchManager();
            String searchServiceJid = "search." + OpenfireConnectUtil.SERVER_NAME;
            String jid = text+"@" + OpenfireConnectUtil.SERVER_NAME;
            try {
                DomainBareJid searchService = JidCreate.domainBareFrom(searchServiceJid);
                Form searchForm = userSearchManager.getSearchForm(searchService);
                FillableForm answerForm = searchForm.getFillableForm();
                answerForm.setAnswer("Username",true);
                answerForm.setAnswer("Name",true);
                answerForm.setAnswer("JID",jid);
                ReportedData data = userSearchManager.getSearchResults(answerForm,searchService);
                List<ReportedData.Row> rows = data.getRows();
                for (ReportedData.Row row : rows) {
                    List<CharSequence> values = row.getValues("jid");
                    if (values.size() > 0) {
                        System.out.println(values.get(0));
                        // 处理搜索结果
                    }
                }
                HashMap<Integer,Integer> m = new HashMap<>();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}
