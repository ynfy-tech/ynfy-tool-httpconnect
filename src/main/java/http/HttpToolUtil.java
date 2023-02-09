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
    public static <T, M> M sendGet(String url, T paramObj, Map<String, String> header, Class<M> responseClass) {
        String result = HttpGetUtil.getInstance().send(url, header, paramObj);
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
    public static <T, M> M sendDelete(String url, T paramObj, Map<String, String> header, Class<M> responseClass) {
        String result = HttpDeleteUtil.getInstance().send(url, header, paramObj);
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
    public static <M> M sendPost(String url, String param, Map<String, String> header, Class<M> responseClass) {
        String result = HttpPostUtil.getInstance().send(url, header, param);
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
    public static <M> M send(String url, Object param, Map<String, String> header, Class<M> responseClass) {
        return sendPost(url, JSON.toJSONString(param), header, responseClass);
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
    public static <T, M> M sendPostFile(String url,
                                        Map<String, String> header,
                                        T paramObj,
                                        String dir,
                                        Class<M> responseClass) {
        String result = HttpPostUtil.getInstance().sendFile(url, header, paramObj, dir);
        return JSONObject.parseObject(result, responseClass);
    }

    /**
     * Default constructor added by Java.
     */
    public HttpToolUtil() {
    }
}
