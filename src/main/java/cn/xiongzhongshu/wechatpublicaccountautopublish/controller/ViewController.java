package cn.xiongzhongshu.wechatpublicaccountautopublish.controller;

import cn.xiongzhongshu.wechatpublicaccountautopublish.bean.PdfFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.util.ArrayList;

import static cn.xiongzhongshu.wechatpublicaccountautopublish.tools.FileTools.findPdfFiles;
import static cn.xiongzhongshu.wechatpublicaccountautopublish.tools.IpTools.getAdderss;
import static cn.xiongzhongshu.wechatpublicaccountautopublish.tools.PdfTools.pdfToImages;
import static cn.xiongzhongshu.wechatpublicaccountautopublish.tools.WechatTools.connectTest;
import static cn.xiongzhongshu.wechatpublicaccountautopublish.tools.WechatTools.createDraft;


public class ViewController {
    @FXML
    private TextField ipaddress;

    @FXML
    private TableView fileTable;
    @FXML
    private TextField dirPath;

    @FXML
    private PasswordField appSecret;

    @FXML
    private TextField appID;

    @FXML
    private TextArea logsTextArea;

    @FXML
    private TableColumn<PdfFile, String> fileName;

    @FXML
    private TableColumn<PdfFile, String> filePath;

    @FXML
    private TableColumn<PdfFile, String> status;


    /**
     * 获取本机公网IP地址
     */
    @FXML
    public void onIpAdderssButtonClick() {
        String adderss = getAdderss();
        if (adderss.isBlank() || adderss.isEmpty()) {
            logsTextArea.appendText("您的网络好像出问题了，试一试访问：https://ip.me\n");
        } else {
            ipaddress.setText(getAdderss());
            logsTextArea.appendText("你的公网ip地址：" + adderss + "，看一下IP白名单有没得这个IP，没得的话要加进去\n");
        }
    }


    /**
     * 测试公众号连接
     */
    @FXML
    public void onWechatConnectTestButtonClick() {
        String appsecrettext = appSecret.getText();
        if (appsecrettext.isBlank() || appsecrettext.isBlank()) {
            logsTextArea.appendText("请输入开发者密码(AppSecret)，了解详情：https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Getting_Started_Guide.html\n");
            return;
        }
        String appidtext = appID.getText();
        if (appidtext.isBlank() || appidtext.isBlank()) {
            logsTextArea.appendText("请输入开发者ID(AppID)，了解详情：https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Getting_Started_Guide.html\n");
            return;
        }

        String result = connectTest(appidtext, appsecrettext);
        logsTextArea.appendText("公众号连接测试结果：" + result + "\n");

    }


    /**
     * 搜索pdf文件
     */
    @FXML
    public void onSearchPdfFileButtonClick() {
        String dirPathText = dirPath.getText();
        if (dirPathText.isEmpty() || dirPathText.isBlank()) {
            logsTextArea.appendText("请输入pdf文件路径或者目录路径\n");
            return;
        }
        ArrayList<File> pdfFiles = findPdfFiles(dirPathText);
        ObservableList<PdfFile> data = FXCollections.observableArrayList();
        for (File file : pdfFiles) {
            data.add(new PdfFile(file.getName(), file.getPath(), "ok"));
        }
        fileName.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        filePath.setCellValueFactory(cellData -> cellData.getValue().filePathProperty());
        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        fileTable.setItems(data);
        logsTextArea.appendText("搜索到" + pdfFiles.size() + "个pdf文件\n");
    }

    /**
     * PDF转图片
     */
    @FXML
    public void onPdfToImagesButtonClick() {
        PdfFile selectedItem = (PdfFile) fileTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            logsTextArea.appendText("请选择文件\n");
        } else {
            long startTime = System.currentTimeMillis();
            pdfToImages(selectedItem.getFilePath());
            long endTime = System.currentTimeMillis();
            long usedTime = (endTime - startTime);
            logsTextArea.appendText(usedTime + "毫秒，" + selectedItem.getFileName() + "，转换成功\n");
        }
    }


    /**
     * PDF转草稿
     */
    @FXML
    public void onPdfToImagesDraftClick() {
        String appsecrettext = appSecret.getText();
        if (appsecrettext.isBlank() || appsecrettext.isBlank()) {
            logsTextArea.appendText("请输入开发者密码(AppSecret)，了解详情：https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Getting_Started_Guide.html\n");
            return;
        }
        String appidtext = appID.getText();
        if (appidtext.isBlank() || appidtext.isBlank()) {
            logsTextArea.appendText("请输入开发者ID(AppID)，了解详情：https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Getting_Started_Guide.html\n");
            return;
        }
        PdfFile selectedItem = (PdfFile) fileTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            logsTextArea.appendText("请选择文件\n");
        } else {
            long startTime = System.currentTimeMillis();
            String draft = createDraft(appidtext, appsecrettext, selectedItem.getFilePath(), false);
            long endTime = System.currentTimeMillis();
            long usedTime = (endTime - startTime);
            logsTextArea.appendText(usedTime + "毫秒，" + selectedItem.getFileName() + "，生成草稿成功，id:" + draft + "\n");
        }

    }


    /**
     * PDF发布
     */
    @FXML
    public void onFreepublishClick() {
        String appsecrettext = appSecret.getText();
        if (appsecrettext.isBlank() || appsecrettext.isBlank()) {
            logsTextArea.appendText("请输入开发者密码(AppSecret)，了解详情：https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Getting_Started_Guide.html\n");
            return;
        }
        String appidtext = appID.getText();
        if (appidtext.isBlank() || appidtext.isBlank()) {
            logsTextArea.appendText("请输入开发者ID(AppID)，了解详情：https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Getting_Started_Guide.html\n");
            return;
        }
        PdfFile selectedItem = (PdfFile) fileTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            logsTextArea.appendText("请选择文件\n");
        } else {
            long startTime = System.currentTimeMillis();
            String draft = createDraft(appidtext, appsecrettext, selectedItem.getFilePath(), true);
            long endTime = System.currentTimeMillis();
            long usedTime = (endTime - startTime);
            logsTextArea.appendText(usedTime + "毫秒，" + selectedItem.getFileName() + "，发布成功，id:" + draft + "\n");
        }
    }

    /**
     * 表格点击事件
     */
//    @FXML
//    public void onFileTableClick() {
//        PdfFile selectedItem = (PdfFile) fileTable.getSelectionModel().getSelectedItem();
//        if (selectedItem != null) {
//            logsTextArea.appendText("选中：" + selectedItem.getFileName() + "\n");
//        }
//    }


}
