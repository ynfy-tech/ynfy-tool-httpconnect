package http;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * http 工具类
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2020/7/9 5:42 下午
 */
public class HttpDeleteUtil extends InitUtil {

    private static class Inner {
        private static final HttpDeleteUtil instance = new HttpDeleteUtil();
    }

    /***
     * 单例模式之：静态内部类单例模式
     * 只有第一次调用getInstance方法时，虚拟机才加载 Inner 并初始化instance ，只有一个线程可以获得对象的初始化锁，其他线程无法进行初始化，
     * 保证对象的唯一性。目前此方式是所有单例模式中最推荐的模式，但具体还是根据项目选择。
     * @return
     */
    public static HttpDeleteUtil getInstance() {
        return Inner.instance;
    }

    /**
     * 向指定URL发送 delete 方法的请求
     *
     * @param url      发送请求的URL
     * @param <T>      泛型入参
     * @return String 所代表远程资源的响应结果
     */
    public <T> String send(String url) {
        HttpURLConnection connection = null;
        try {
            connection = getConnection(url);
            connection.setRequestMethod("DELETE");
            // 建立实际的连接
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getResponseString(connection);
    }

    /**
     * 向指定URL发送 delete 方法的请求
     *
     * @param url      发送请求的URL
     * @param header   请求头, "key1":"value1"的形式
     * @param <T>      泛型入参
     * @return String 所代表远程资源的响应结果
     */
    public <T> String send(String url, Map<String, String> header) {
        HttpURLConnection connection = null;
        try {
            connection = getConnection(url);
            connection.setRequestMethod("DELETE");
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
     * 向指定URL发送 delete 方法的请求
     *
     * @param url      发送请求的URL
     * @param paramObj 请求参数，以对象的形式
     * @param header   请求头, "key1":"value1"的形式
     * @param <T>      泛型入参
     * @return String 所代表远程资源的响应结果
     */
    public <T> String send(String url, Map<String, String> header, T paramObj) {
        HttpURLConnection connection = null;
        try {
            String urlParams = appendGetUrlParam(url, paramObj);
            connection = getConnection(urlParams);
            connection.setRequestMethod("DELETE");
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
     * Default constructor added by Java.
     */
    public HttpDeleteUtil() {
    }
}
