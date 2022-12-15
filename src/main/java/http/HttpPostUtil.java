package http;


import com.alibaba.fastjson.JSON;
import http.constant.HttpEnum;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * http 工具类
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2020/7/9 5:42 下午
 */
public class HttpPostUtil extends InitUtil {

    /**
     * 内部静态类
     */
    private static class Inner {
        private static final HttpPostUtil instance = new HttpPostUtil();
    }

    /***
     * 单例模式之：静态内部类单例模式
     * 只有第一次调用getInstance方法时，虚拟机才加载 Inner 并初始化instance ，只有一个线程可以获得对象的初始化锁，其他线程无法进行初始化，
     * 保证对象的唯一性。目前此方式是所有单例模式中最推荐的模式，但具体还是根据项目选择。
     * @return 返回单例
     */
    public static HttpPostUtil getInstance() {
        return Inner.instance;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url) {
        return sendPostOrPutConnection(url, HttpEnum.POST);
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param header 请求头
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url, Map<String, String> header) {
        return sendPostOrPutConnection(url, HttpEnum.POST, header);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param param  请求参数，JSON string
     * @param header 请求头
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url, Map<String, String> header, String param) {
        return sendPostOrPutConnection(url, HttpEnum.POST, header, param);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param param  请求参数，Object
     * @param header 请求头
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url, Map<String, String> header, Object param) {
        return sendPostOrPutConnection(url, HttpEnum.POST, header, JSON.toJSONString(param));
    }

    /**
     * 边界标识
     */
    private final String BOUNDARY = UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");

    /**
     * 必须存在
     */
    private final String PREFIX = "--";

    /**
     * 字段结束
     */
    private final String LINE_END = "\r\n";

    /**
     * 发送 post 文件请求
     *
     * @param url      远程接口路径
     * @param paramObj 泛型出参
     * @param dir      文件本地路径
     * @param header   请求头
     * @param <T>      泛型入参
     * @return T 所代表远程资源的响应结果
     */
    public <T> String sendFile(String url, Map<String, String> header, T paramObj, String dir) {
        String urlParams = appendGetUrlParam(url, paramObj);
        HttpURLConnection connection = null;
        try {
            URL uri = new URL(urlParams);
            URLConnection urlConnection = uri.openConnection();
            connection = (HttpURLConnection) urlConnection;
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Charset", StandardCharsets.UTF_8.name());
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            connection.setRequestProperty("Charset", StandardCharsets.UTF_8.name());
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.connect();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        File file = new File(dir);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在");
        }

        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream());
             DataInputStream in = new DataInputStream(new FileInputStream(file));) {
            try {

                // 请求参数部分
                StringBuilder requestParams = new StringBuilder();
                requestParams.append(PREFIX).append(BOUNDARY).append(LINE_END);
                requestParams.append("Content-Disposition: form-data; name=\"")
                             .append("file")
                             .append("\"")
                             .append(LINE_END);
                requestParams.append("Content-Type: text/plain; charset=utf-8").append(LINE_END);
                requestParams.append("Content-Transfer-Encoding: 8bit").append(LINE_END);
                requestParams.append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                requestParams.append("ori2.jpeg");
                requestParams.append(LINE_END);
                out.write(requestParams.toString().getBytes());
                out.flush();


                String msg = "请求上传文件部分:\n";
                requestParams = new StringBuilder();
                requestParams.append(PREFIX).append(BOUNDARY).append(LINE_END);
                requestParams.append("Content-Disposition: form-data; name=\"")
                             .append("file")
                             .append("\"; filename=\"")
                             .append("ori2.jpeg")
                             .append("\"")
                             .append(LINE_END);
                requestParams.append("Content-Type:").append("multipart/form-data").append(LINE_END);
                requestParams.append("Content-Transfer-Encoding: 8bit").append(LINE_END);
                requestParams.append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容

                out.write(requestParams.toString().getBytes());

                int bytes = 0;
                byte[] buffer = new byte[1024];
                while ((bytes = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes);
                }

                out.write(LINE_END.getBytes());
                out.flush();

                // 请求结束标志
                String endTarget = PREFIX + BOUNDARY + PREFIX + LINE_END;
                out.write(endTarget.getBytes());
                out.flush();
            } catch (Exception e) {
                throw new Exception(e);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        return getResponseString(connection);
    }

    /**
     * 获取 sessionId
     * header.put("Cookie", sessionId);
     * 
     * https://blog.csdn.net/thomassamul/article/details/82346632
     * https://juejin.cn/post/7057714775736156168
     *
     * @param url   url
     * @param param 参数
     * @return sessionId
     */
    public String getSessionId(String url, Object param) {
        HttpURLConnection connection = getConnection(url, HttpEnum.POST);

        if (param != null) {
            byte[] content = param.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = connection.getOutputStream();) {
                os.write(content);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        System.out.println("the ret is: ");
        System.out.println(getResponseString(connection));

        String session_value = connection.getHeaderField("Set-Cookie");
        if (session_value == null || session_value.length() == 0) {
            throw new IllegalArgumentException("session Id is null!");
        }

        String[] sessionId = session_value.split(";");
        return sessionId[0];
    }


    /**
     * Default constructor added by Java.
     */
    public HttpPostUtil() {
    }
}
