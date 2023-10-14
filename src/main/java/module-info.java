module com.ezchat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires rxcontrols;
    requires smack.tcp;
    requires smack.core;
    requires smack.im;
    requires jxmpp.jid;
    requires jxmpp.core;
    requires smack.extensions;
    //requires jakarta.oro;
    requires smack.experimental;
    requires java.desktop;
    requires jakarta.oro;

    opens com.ezchat to javafx.fxml;
    exports com.ezchat;
    exports com.ezchat.controller;
    opens com.ezchat.controller to javafx.fxml;

}