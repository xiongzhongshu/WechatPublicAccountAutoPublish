package cn.xiongzhongshu.wechatpublicaccountautopublish.tools;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HtmlTemplateTools {
    private final static TemplateEngine engine = new TemplateEngine();

    /**
     * 使用 Thymeleaf 渲染 HTML
     *
     * @param template HTML模板
     * @param params   参数
     * @return 渲染后的HTML
     */
    public static String render(String template, Map<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        return engine.process(template, context);
    }

    /**
     * 用模板生成微信公众号页面
     *
     * @param arrayList
     * @param outPath
     * @return
     */

    public static String convertHtml(ArrayList<String> arrayList, String outPath) {
        //创建父级目录
        File file = new File(outPath);
        String parent = file.getParent();
        File file1 = new File(parent);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        String render = "";
        try {

            FileReader fileReader;
            String template = "";
            if (!new File("template.html").exists()) {
                template = "<img th:each=\"image:${imagesList}\" th:src=\"${image}\">";
            } else {
                //读模板文件
                fileReader = new FileReader("template.html");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while (bufferedReader.ready()) {
                    template = template + bufferedReader.readLine();
                }
                bufferedReader.close();
                fileReader.close();
            }
            //渲染html
            HashMap<String, Object> map = new HashMap<>();
            map.put("imagesList", arrayList);
            render = HtmlTemplateTools.render(template, map);
            //输出选然后的html文件
            FileWriter fileWriter = new FileWriter(outPath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(render);
            bufferedWriter.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回渲染出来的html
        return render;
    }
}
