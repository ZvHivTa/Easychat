package com.ezchat.utils;

import javafx.scene.control.Alert;

public class AlertWindowUtil {

    public static void setAlertWindowAndShow(Alert.AlertType alertType,String contentText,String headerText){
        Alert a = new Alert(alertType);
        a.setContentText(contentText);
        a.setHeaderText(headerText);
        a.show();
    }
}
