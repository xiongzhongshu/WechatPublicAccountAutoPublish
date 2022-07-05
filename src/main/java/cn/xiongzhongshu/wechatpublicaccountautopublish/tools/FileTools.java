package cn.xiongzhongshu.wechatpublicaccountautopublish.tools;

import java.io.File;
import java.util.ArrayList;


public class FileTools {


    /**
     * 递归检索PDF文件
     * @param dirPath
     * @return
     */
    public static ArrayList<File> findPdfFiles(String dirPath) {
        File directoryPath = new File(dirPath);
        ArrayList<File> pdfPathList = new ArrayList<>();
        if (directoryPath.isDirectory()) {
            File[] files = directoryPath.listFiles();
            for (File file : files) {
                ArrayList<File> pdfFiles = findPdfFiles(file.getPath());
                for (File path : pdfFiles) {
                    pdfPathList.add(path);
                }
            }
        } else {
            String name = directoryPath.getName();
            String lowerCaseName = name.toLowerCase();
            int i = lowerCaseName.lastIndexOf(".pdf");
            if (i > -1) {
                pdfPathList.add(directoryPath);
            }
        }
        return pdfPathList;
    }

    /**
     * 获取无后缀的文件名
     *
     * @param FilePath
     * @return
     */
    public static String getFileNameNoEx(String FilePath) {
        File file = new File(FilePath);

        String fileNameNoEx = file.getName();
        int pos = file.getName().lastIndexOf(".");
        if (pos > -1) {
            fileNameNoEx = file.getName().substring(0, pos);
        }
        return fileNameNoEx;
    }
}
