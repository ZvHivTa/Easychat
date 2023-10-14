package com.ezchat.utils;

import com.ezchat.EZChatApplication;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class FileTransferUtil {

    /***
     * 展示文件选择器
     * @return 返回选择的文件集合
     */
    public static List<File> showFileChooser() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Files");
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(Objects.requireNonNull(EZChatApplication.class.getResourceAsStream("img/EZchat.png"))));
        return fileChooser.showOpenMultipleDialog(stage);
    }

    /***
     *
     * @param transfer 文件传输器
     * @param files 文件列表
     * @return 文件传输结果信息
     *
     */
    public static String sendFiles(OutgoingFileTransfer transfer, List<File> files) throws SmackException {
        transfer.sendFile(files.get(0),"");

        while(true) {//判断传输是否完成，传输取消、传输完成、传输发生错误都会返回true
            double progress = transfer.getProgress();
            System.out.println(progress);
            if(transfer.isDone()){
                break;
            }
        }
        String resultInfo = null;
        if(FileTransfer.Status.complete.equals(transfer.getStatus())) {
            //传输完成
            resultInfo = "Has sent "+files.get(0).getName();
        } else if(FileTransfer.Status.cancelled.equals(transfer.getStatus())) {
            //传输取消
            resultInfo = "Transformation cancel..";
        } else if(FileTransfer.Status.error.equals(transfer.getStatus())) {
            //传输错误
            resultInfo = "Transformation error..";
        } else if(FileTransfer.Status.refused.equals(transfer.getStatus())) {
            //传输拒绝
            resultInfo = "Transformation has been rejected!";
        }
        return resultInfo;
    }

    public static String receiveFiles(FileTransferRequest request) throws SmackException, IOException {
        String home = System.getProperty("user.home");
        File file = new File(home+"\\Download\\"+request.getFileName());
        IncomingFileTransfer accept = request.accept();
        accept.receiveFile(file);
        while(true) {
            if (accept.getStatus() == FileTransfer.Status.complete) {
                return file.toPath().toString();
            }
        }
    }

    /***
     * 计算对应文件的大小，以合理的单位显示在前端。
     * @param file 文件对象
     * @return 返回表示文件大小的字符串
     */
    public static String fileSizeCalculator(File file) throws IOException {
        double size = Files.size(file.toPath());
        return fileSizeCalculator(size);
    }

    public static String fileSizeCalculator(double size){
        String byteType = "b";
        if(size>=1024){
            size/=1024;
            byteType = "kb";
        }
        if(size>=1024){
            size/=1024;
            byteType = "mb";
        }
        if(size>=1024){
            size/=1024;
            byteType = "gb";
        }
        BigDecimal temp = new BigDecimal(size);
        size = temp.setScale(3, RoundingMode.HALF_UP).doubleValue();
        return size+" "+byteType;
    }

    private FileTransferUtil(){

    }
}
