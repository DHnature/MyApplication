package com.example.administrator.rainmusic.httpservice;




import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterServiceUtils {



    public static String executeHttpGet(String username, String password, int type){

        HttpURLConnection conn=null;
        InputStream is = null;


        try {
            String path2= "http://" + LoginServiceUtils.IP + "/web/RegLet";
            path2 += "?username=" + username + "&password=" + password;
            conn = (HttpURLConnection) new URL(path2).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Charset", "UTF-8");
            if(conn.getResponseCode()==200){
                is = conn.getInputStream();
                return parseInfo(is);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "服务器连接超时...";
    }


    // 将输入流转化为 String 型
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }

    // 将输入流转化为byte型
    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}

