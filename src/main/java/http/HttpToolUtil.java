package http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * http 工具示例类
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2020/7/9 5:42 下午
 */
public class HttpToolUtil {

    /**
     * 内部静态类
     */
    private static class Inner {
        private static final HttpToolUtil instance = new HttpToolUtil();
    }

    /***
     * 单例模式之：静态内部类单例模式
     * 只有第一次调用getInstance方法时，虚拟机才加载 Inner 并初始化instance ，只有一个线程可以获得对象的初始化锁，其他线程无法进行初始化，
     * 保证对象的唯一性。目前此方式是所有单例模式中最推荐的模式，但具体还是根据项目选择。
     * @return 返回单例
     */
    public static HttpToolUtil getInstance() {
        return HttpToolUtil.Inner.instance;
    }


    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    private static final class Static {
        private static HttpGetUtil getUtil = HttpGetUtil.getInstance();
        private static HttpPostUtil postUtil = HttpPostUtil.getInstance();
        private static HttpPutUtil putUtil = HttpPutUtil.getInstance();
        private static HttpDeleteUtil deleteUtil = HttpDeleteUtil.getInstance();
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url           发送请求的URL
     * @param paramObj      请求参数，以对象的形式
     * @param header        请求头, "key1":"value1"的形式
     * @param responseClass 返回子类型
     * @param <T>           返回类型
     * @param <M>           返回子类型
     * @return String 所代表远程资源的响应结果
     */
    public <T, M> M sendGet(String url, Map<String, String> header, T paramObj, Class<M> responseClass) {
        String result = Static.getUtil.send(url, header, paramObj);
        return JSONObject.parseObject(result, responseClass);
    }

    /**
     * 向指定URL发送 delete 方法的请求
     *
     * @param url           发送请求的URL
     * @param paramObj      请求参数，以对象的形式
     * @param header        请求头, "key1":"value1"的形式
     * @param responseClass 返回子类型
     * @param <T>           返回类型
     * @param <M>           返回子类型
     * @return String 所代表远程资源的响应结果
     */
    public <T, M> M sendDelete(String url, Map<String, String> header, T paramObj, Class<M> responseClass) {
        String result = Static.deleteUtil.send(url, header, paramObj);
        return JSONObject.parseObject(result, responseClass);
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url           发送请求的 URL
     * @param param         请求参数，请求参数应该是 name1=value1 name2=value2 的 json 形式。
     * @param header        请求头
     * @param <M>           返回子类型
     * @param responseClass 返回子类型
     * @return 所代表远程资源的响应结果
     * <p>
     * Content type 'application/json;charset=UTF-8' not supported
     * // 请考虑接口入参的情况
     */
    public <M> M sendPost(String url, Map<String, String> header, String param, Class<M> responseClass) {
        String result = Static.postUtil.send(url, header, param);
        return JSONObject.parseObject(result, responseClass);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url           发送请求的 URL
     * @param param         请求参数，object
     * @param header        请求头
     * @param <M>           返回子类型
     * @param responseClass 返回子类型
     * @return 所代表远程资源的响应结果
     * <p>
     * Content type 'application/json;charset=UTF-8' not supported
     * // 请考虑接口入参的情况
     */
    public <M> M sendPost(String url, Map<String, String> header, Object param, Class<M> responseClass) {
        String result = Static.postUtil.send(url, header, param);
        return JSONObject.parseObject(result, responseClass);
    }

    /**
     * 发送 post 文件请求
     *
     * @param url           远程接口地址
     * @param paramObj      返回类型
     * @param dir           文件路径
     * @param header        消息头
     * @param responseClass 子类型
     * @param <T>           返回类型
     * @param <M>           子类型
     * @return T 所代表远程资源的响应结果
     */
    public <T, M> M sendPost(String url, Map<String, String> header, T paramObj, String dir, Class<M> responseClass) {
        String result = Static.postUtil.sendFile(url, header, paramObj, dir);
        return JSONObject.parseObject(result, responseClass);
    }

    /**
     * Default constructor added by Java.
     */
    public HttpToolUtil() {
    }
}
