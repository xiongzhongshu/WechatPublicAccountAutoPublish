package cn.xiongzhongshu.wechatpublicaccountautopublish.tools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class PdfTools {
    //逻辑处理器数量
    private static int processors = Runtime.getRuntime().availableProcessors();

    /**
     * PDF转图片
     *
     * @param pdfFilePath
     * @return
     */
    public static ArrayList<String> pdfToImages(String pdfFilePath) {
        ExecutorService executorService = Executors.newFixedThreadPool(processors);
        CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executorService);

        ArrayList<String> imagesList = new ArrayList<String>();
        PDDocument document = null;
        File file = new File(pdfFilePath);
//        去掉后缀 ，用文件名创建目录
        String fileNameNoEx = FileTools.getFileNameNoEx(pdfFilePath);
        String imagesPath = "images" + File.separator + fileNameNoEx;
        File file1 = new File(imagesPath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            document = PDDocument.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int numberOfPages = document.getNumberOfPages();
//        多线程执行PDF转图片
        for (int pageCounter = 0; pageCounter < numberOfPages; pageCounter++) {
            String path = imagesPath + File.separator + (pageCounter) + ".png";
            PdfConvert pdfConvert = new PdfConvert(file, pageCounter, path);
            imagesList.add(path);
            completionService.submit(pdfConvert);
        }
        try {
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        等待线程执行结束
        for (int i = 0; i < numberOfPages; i++) {
            try {
                completionService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        return imagesList;
    }
}

/**
 * pdf转图片执行线程
 */
class PdfConvert implements Callable {
    private final System.Logger LOGGER = System.getLogger("PdfConvert");
    private int pageCounter = 0;
    private PDFRenderer pdfRenderer = null;
    private String fileName;
    private PDDocument document;


    public PdfConvert(File file, int pageCounter, String fileName) {
        this.pageCounter = pageCounter;
        this.fileName = fileName;
        try {
            this.document = PDDocument.load(file);
            this.pdfRenderer = new PDFRenderer(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object call() throws Exception {
        boolean result = false;
        File file = new File(fileName);
        if (!file.exists()) {
            BufferedImage bim = null;
            try {
                bim = pdfRenderer.renderImageWithDPI(pageCounter, 100, ImageType.RGB);
                result = ImageIOUtil.writeImage(bim, fileName, 100);
                LOGGER.log(System.Logger.Level.INFO, fileName + "，生成：" + result);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.document.close();
            }
        } else {
            LOGGER.log(System.Logger.Level.INFO, fileName + "，存在");
            this.document.close();
            return true;
        }
        return false;
    }
}