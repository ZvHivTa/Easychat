package com.ezchat.controller;

import com.ezchat.EZChatApplication;
import com.ezchat.utils.FileTransferUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileListviewCell extends ListCell<File> {
    @FXML
    private Circle FileIconCircle;

    @FXML
    private Label Filename;

    @FXML
    private Text Filesize;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView deleteFileItem;

    @FXML
    private TextFlow newMessageNumberTextFlow;

    private File file;

    @FXML
    void deleteFileAction(MouseEvent event) {
        if(file!=null){
            SendFilesController.removeFile(file);
        }
    }


    @FXML
    public void initialize(){
        Image fileImage;
        try (InputStream resourceAsStream = EZChatApplication.class.getResourceAsStream("img/fileIcon.png")) {
            fileImage = new Image(Objects.requireNonNull(resourceAsStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileIconCircle.setFill(new ImagePattern(fileImage));
    }
    public FileListviewCell() {
        try {
            FXMLLoader loader = new FXMLLoader(FileListviewCell.class.getResource("/com/ezchat/fxml/FileListviewCell.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        //由于注释中表明的特性，我们只在非空的时候调用该操作去初始化列表。
        if (item != null && !empty) {
            this.file = item;
            Filename.setText(item.getName());
            String size = null;
            try {
                size = FileTransferUtil.fileSizeCalculator(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Filesize.setText(size);
            //最终要更改默认的item类型，Listview默认设置列表中的item是纯文本TEXT类型。
            //这里设置为Graphic类型，相当于把组件外部包裹的Pane（root）作为客制化的items植入进Listview中
            setText(null);
            setGraphic(anchorPane);
            setStyle("-fx-background-color:transparent");
        } else {
            //什么都不显示，把表格的显示类型置为空
            setText(null);
            setGraphic(null);
            setStyle("-fx-background-color:transparent");
        }
    }

}
