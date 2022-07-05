package cn.xiongzhongshu.wechatpublicaccountautopublish.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IpTools {


    /**
     * 获取本机公网IP
     *
     * @return
     */
    public static String getAdderss() {
        InputStream inputStream = null;
        try {
            URL url = new URL("https://ip.me/");
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "curl");
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                    StringBuffer resultBuffer = new StringBuffer();
                    String line;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    while ((line = bufferedReader.readLine()) != null) {
                        resultBuffer.append(line);
                    }
                    return resultBuffer.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
