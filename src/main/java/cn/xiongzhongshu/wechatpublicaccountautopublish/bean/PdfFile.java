package cn.xiongzhongshu.wechatpublicaccountautopublish.bean;

import javafx.beans.property.SimpleStringProperty;

/**
 * 界面上的文件表格
 */
public class PdfFile {
    //文件名
    private SimpleStringProperty fileName;
    //文件路径
    private SimpleStringProperty filePath;
    //状态
    private SimpleStringProperty status;

    public String getFileName() {
        return fileName.get();
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public String getFilePath() {
        return filePath.get();
    }

    public SimpleStringProperty filePathProperty() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public PdfFile(String fileName, String filePath, String status) {
        this.fileName = new SimpleStringProperty(fileName);
        this.filePath = new SimpleStringProperty(filePath);
        this.status = new SimpleStringProperty(status);
    }
}
