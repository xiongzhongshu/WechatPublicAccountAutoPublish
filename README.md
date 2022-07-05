# WechatPublicAccountAutoPublish
微信公众号自动发布工具，PDF文件生成微信公众号的草稿并自动发布。

## 功能

1. 搜索指定目录下的PDF文件
2. PDF文件转图片，生成100DPI的PNG图片
3. PDF文件自动生成微信公众号的草稿，可选发布
4. 支持自定义模板，可在草稿的PDF图片前后自定义添加内容

## 环境依赖

1. java 11
2. maven 3
3. 运行jar包，需要启动时引入JavaFX SDK 11.0.12，下载地址：[JavaFX下载 | JavaFX中文官方网站 (openjfx.cn)](https://openjfx.cn/dl/)

## 编译和运行

依赖java 11和JAVAFX SDK 11.0.12

1. 拉取代码

   ```
   git clone https://github.com/xiongzhongshu/WechatPublicAccountAutoPublish.git
   ```

2. 编译

   ```
   mvn clean package
   ```

3. 运行

   ```
   java --module-path="./javafx-sdk-11.0.2/lib" --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web -jar WechatPublicAccountAutoPublish-1.2.jar
   ```

## 运行jar包的目录结构

- images：pdf转换成图片的目录
- html：pdf生成微信公众号草稿的目录
- javafx-sdk-11.0.2：javafx sdk目录
- template.html：生成微信公众号草稿的模板文件，可在PDF内容前后添加自定义内容
- WechatPublicAccountAutoPublish-1.2.jar：编译后的jar包
- run.bat：启动程序脚本

# 程序运行说明

1. 取得微信公众号的开发者ID和开发者密码，详见：[1开启公众号开发者模式 | 微信开放文档 (qq.com)](https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Getting_Started_Guide.html)
2. 输入开发者ID和开发者密码，测试公众号连接，如果连接失败，在微信公众号的IP白名单添加本机公网ip
3. 输入PDF文件或文件夹路径，搜索pdf
4. 选择PDF文件，使用功能：PDF转图片/PDF生成草稿/PDF发布
