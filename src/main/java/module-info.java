module cn.xiongzhongshu.wechatpublicaccountautopublish {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires org.apache.pdfbox.tools;
    requires weixin.java.mp;
    requires weixin.java.common;
    requires thymeleaf;

    opens cn.xiongzhongshu.wechatpublicaccountautopublish to javafx.fxml;
    exports cn.xiongzhongshu.wechatpublicaccountautopublish;
    opens cn.xiongzhongshu.wechatpublicaccountautopublish.controller to javafx.fxml;
    exports cn.xiongzhongshu.wechatpublicaccountautopublish.controller;

}