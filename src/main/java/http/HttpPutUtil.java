package http;


import com.alibaba.fastjson.JSON;
import http.constant.HttpEnum;

import java.util.Map;

/**
 * http 工具类
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2020/7/9 5:42 下午
 */
public class HttpPutUtil extends InitUtil {

    /**
     * 内部静态类
     */
    private static class Inner {
        private static final HttpPutUtil instance = new HttpPutUtil();
    }

    /***
     * 单例模式之：静态内部类单例模式
     * 只有第一次调用getInstance方法时，虚拟机才加载 Inner 并初始化instance ，只有一个线程可以获得对象的初始化锁，其他线程无法进行初始化，
     * 保证对象的唯一性。目前此方式是所有单例模式中最推荐的模式，但具体还是根据项目选择。
     * @return 返回单例
     */
    public static HttpPutUtil getInstance() {
        return Inner.instance;
    }

    /**
     * 向指定 URL 发送 PUT 方法的请求
     *
     * @param url    发送请求的 URL
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url) {
        return sendPostOrPutConnection(url, HttpEnum.PUT);
    }

    /**
     * 向指定 URL 发送 PUT 方法的请求
     *
     * @param url    发送请求的 URL
     * @param header 请求头
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url, Map<String, String> header) {
        return sendPostOrPutConnection(url, HttpEnum.PUT, header);
    }

    /**
     * 向指定 URL 发送 PUT 方法的请求
     *
     * @param url    发送请求的 URL
     * @param param  请求参数，JSON string
     * @param header 请求头
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url, Map<String, String> header, String param) {
        return sendPostOrPutConnection(url, HttpEnum.PUT, header, param);
    }

    /**
     * 向指定 URL 发送 PUT 方法的请求
     *
     * @param url    发送请求的 URL
     * @param param  请求参数，Object
     * @param header 请求头
     * @return T 所代表远程资源的响应结果
     */
    public String send(String url, Map<String, String> header, Object param) {
        return sendPostOrPutConnection(url, HttpEnum.PUT, header, JSON.toJSONString(param));
    }

    /**
     * Default constructor added by Java.
     */
    public HttpPutUtil() {
    }
}
