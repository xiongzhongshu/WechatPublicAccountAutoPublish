package cn.xiongzhongshu.wechatpublicaccountautopublish.tools;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpDraftServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpFreePublishServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpMaterialServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.material.WxMediaImgUploadResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

import static cn.xiongzhongshu.wechatpublicaccountautopublish.tools.PdfTools.pdfToImages;

public class WechatTools {

    //逻辑处理器数量
    private static int processors = Runtime.getRuntime().availableProcessors();

    /**
     * 实例化微信服务
     *
     * @param appId
     * @param secret
     * @return
     */
    public static WxMpService getWxService(String appId, String secret) {
        //公众号授权参数
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(appId); // 设置微信公众号的appid
        config.setSecret(secret); // 设置微信公众号的app corpSecret
        // 实例化微信服务
        WxMpService wxService = new WxMpServiceImpl();
        wxService.setWxMpConfigStorage(config);
        return wxService;
    }

    /**
     * 微信连接测试
     *
     * @param appId
     * @param secret
     * @return
     */
    public static String connectTest(String appId, String secret) {
        //获取微信服务
        WxMpService wxService = getWxService(appId, secret);
        try {
            wxService.getAccessToken();
            return "连接成功";
        } catch (WxErrorException e) {
            return e.getError().getErrorMsg();
        }
    }

    /**
     * 创建微信公众号草稿，可选发布
     *
     * @param appId
     * @param secret
     * @param pdfPath
     * @param freepublish
     * @return
     */
    public static String createDraft(String appId, String secret, String pdfPath, boolean freepublish) {
        ExecutorService executorService = Executors.newFixedThreadPool(processors);
        CompletionService<ImageIndex> completionService = new ExecutorCompletionService<>(executorService);
        String resutl = "";
        //获取无后缀的文件名
        String fileNameNoEx = FileTools.getFileNameNoEx(pdfPath);

        //PDF转成图片
        ArrayList<String> imagesWithList = pdfToImages(pdfPath);

        //公众号授权参数
        WxMpService wxService = getWxService(appId, secret);

        //公众号上传文件
        WxMpMaterialServiceImpl wxMpMaterialService = new WxMpMaterialServiceImpl(wxService);
        ArrayList<String> imagesUrlList = new ArrayList<>();
        try {
            //第一张作为封面 上传到素材库 作为封面
            File file = new File(imagesWithList.get(0));
            //新增其他类型永久素材
            WxMpMaterial material = new WxMpMaterial(fileNameNoEx, file, "", "");
            WxMpMaterialUploadResult image = wxMpMaterialService.materialFileUpload("image", material);
            //首页图片地址
            String firstImagesUrl = image.getUrl();
            imagesUrlList.add(firstImagesUrl);
            //首页媒体ID
            String thumbMediaId = image.getMediaId();
            //上传后续图片
            if (imagesWithList.size() > 1) {
                for (int i = 1; i < imagesWithList.size(); i++) {
                    File imageFile = new File(imagesWithList.get(i));
                    UploadImgae uploadImgae = new UploadImgae(i, imageFile, imageFile.getPath() + File.separator + imageFile.getName(), wxMpMaterialService);
                    completionService.submit(uploadImgae);

                }
                HashMap<Integer, String> imageMap = new HashMap<>();
                //等待线程执行结果
                for (int i = 1; i < imagesWithList.size(); i++) {
                    try {
                        Future<ImageIndex> take = completionService.take();
                        ImageIndex imageIndex = take.get();
                        imageMap.put(imageIndex.getIndex(), imageIndex.getUrl());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 1; i < imagesWithList.size(); i++) {
                    String url = imageMap.get(i);
                    imagesUrlList.add(url);
                }
            }

            String html = HtmlTemplateTools.convertHtml(imagesUrlList, "html" + File.separator + fileNameNoEx + ".html");
            //添加草稿
            WxMpDraftServiceImpl wxMpDraftService = new WxMpDraftServiceImpl(wxService);
            resutl = wxMpDraftService.addDraft(fileNameNoEx, html, image.getMediaId());
            //发布
            if (freepublish) {
                WxMpFreePublishServiceImpl wxMpFreePublishService = new WxMpFreePublishServiceImpl(wxService);
                String submit = wxMpFreePublishService.submit(resutl);
                resutl = submit;
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return resutl;
    }

}

/**
 * 上传图片执行线程
 */
class UploadImgae implements Callable {
    private final System.Logger LOGGER = System.getLogger("UploadImgae");

    private int index = 0;
    private File imageFile;
    private String fileName;
    private WxMpMaterialServiceImpl wxMpMaterialService;

    public UploadImgae(int index, File imageFile, String fileName, WxMpMaterialServiceImpl wxMpMaterialService) {
        this.index = index;
        this.imageFile = imageFile;
        this.fileName = fileName;
        this.wxMpMaterialService = wxMpMaterialService;
    }

    @Override
    public Object call() throws Exception {
        String imageUrl = "";
        int i = 0;
        //没有上传成功，最多重复3次
        while (i < 3 && imageUrl.isEmpty()) {
            //图片小于1M上传图文消息内的图片，图片小于10M上传素材
            if (imageFile.length() < 1024 * 1024) {
                WxMediaImgUploadResult wxMediaImgUploadResult = wxMpMaterialService.mediaImgUpload(imageFile);
                imageUrl = wxMediaImgUploadResult.getUrl();
                LOGGER.log(System.Logger.Level.INFO, imageFile.getName() + "，上传图文消息成功：" + imageUrl);
            } else if (imageFile.length() < 10 * 1024 * 1024) {
                //新增其他类型永久素材
                WxMpMaterial material1 = new WxMpMaterial(imageFile.getPath() + File.separator + imageFile.getName(), imageFile, "", "");
                WxMpMaterialUploadResult image1 = wxMpMaterialService.materialFileUpload("image", material1);
                //首页图片地址
                imageUrl = image1.getUrl();
                LOGGER.log(System.Logger.Level.INFO, imageFile.getName() + "，上传素材库成功：" + imageUrl);
            } else {
                imageUrl = "图片大于10M";
                LOGGER.log(System.Logger.Level.ERROR, imageFile.getPath() + "，图片大于10M");
            }
            i++;
        }
        ImageIndex imageIndex = new ImageIndex(index, imageUrl);
        return imageIndex;
    }
}

/**
 * 图片上传到微信后返回图片的页码和图片地址
 */
class ImageIndex {
    //    图片所在PDF的页码
    private int index;
    //    图片地址
    private String url;

    public ImageIndex(int index, String url) {
        this.index = index;
        this.url = url;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}