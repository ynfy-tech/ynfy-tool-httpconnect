package util;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author JF
 * @version 1.0.0
 * @desc 〈〉
 * @date 2020/7/9 5:42 下午
 */
public class HttpUtil<T> {

    /**
     * 向指定URL发送GET方法的请求
     * @param url   发送请求的URL
     * @param paramObj 请求参数，以对象的形式
     * @return String 所代表远程资源的响应结果
     */
    public static <T> String sendGet(String url, T paramObj) {
        URLConnection connection = null;
        try {
            connection = getGetConnection(url, paramObj);
            // 建立实际的连接
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getResponseString(connection);
    }

    /**
     * 向指定URL发送GET方法的请求
     * @param url   发送请求的URL
     * @param paramObj 请求参数，以对象的形式
     * @param header 请求头, "key1":"value1"的形式
     * @return String 所代表远程资源的响应结果
     */
    public static <T> String sendGet(String url, T paramObj, Map<String, String> header) {
        URLConnection connection = null;
        try {
            connection = getGetConnection(url, paramObj);
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 建立实际的连接
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getResponseString(connection);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        String result = "";

        URLConnection connection = null;
        try {
            connection = getConnection(url);
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
            // 获取URLConnection对象对应的输出流
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            ) {
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getResponseString(connection);
    }

    /**
     * 设置通用的请求属性
     * @param url
     * @return
     * @throws IOException
     */
    private static URLConnection getConnection(String url) throws IOException {
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        URLConnection conn = realUrl.openConnection();
        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        return conn;
    }

    /**
     * 获取URLConnection
     * @param url
     * @param paramObj
     * @param <T>
     * @return
     * @throws IOException
     */
    private static <T> URLConnection getGetConnection(String url, T paramObj) throws IOException {
        StringBuilder urlNameString = new StringBuilder(url);
        if (paramObj != null) {
            urlNameString.append("?");
            Class c = paramObj.getClass();
            Field[] fields = c.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object filedValue = ReflectUtil.getFieldValue(field, paramObj);
                fieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
                String filedValueStr = URLEncoder.encode(filedValue.toString(), StandardCharsets.UTF_8);
                urlNameString.append(fieldName).append("=").append(filedValueStr).append("&");
            }
        }

        URLConnection connection = getConnection(urlNameString.toString());

        return connection;
    }

    /**
     * 从链接中获取响应字符串
     * @param connection
     * @return
     */
    private static String getResponseString(URLConnection connection) {
        StringBuilder result = new StringBuilder();
        // 定义 BufferedReader输入流来读取URL的响应
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        ) {
            String line = null;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
