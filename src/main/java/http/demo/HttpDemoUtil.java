
package http.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import http.HttpUtil;

import java.util.List;
import java.util.Map;

/**
 * http 工具示例类
 *
 * @author Hsiong
 * @version  1.0.0
 * @since  2020/7/9 5:42 下午
 */
public class HttpDemoUtil {

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url      发送请求的URL
     * @param paramObj 请求参数，以对象的形式
     * @param header   请求头, "key1":"value1"的形式
     * @param responseClass 返回子类型
     * @param <T> 返回类型
     * @param <M> 返回子类型
     * @return String 所代表远程资源的响应结果
     */
    public static <T, M> M sendGet(String url, T paramObj, Map<String, String> header, Class<M> responseClass) {
        String result = HttpUtil.sendGet(url, paramObj, header);
        return getRet(result, responseClass);
    }

    /**
     * 向指定URL发送 delete 方法的请求
     *
     * @param url      发送请求的URL
     * @param paramObj 请求参数，以对象的形式
     * @param header   请求头, "key1":"value1"的形式
     * @param responseClass 返回子类型
     * @param <T> 返回类型
     * @param <M> 返回子类型
     * @return String 所代表远程资源的响应结果
     */
    public static <T, M> M sendDelete(String url, T paramObj, Map<String, String> header, Class<M> responseClass) {
        String result = HttpUtil.sendDelete(url, paramObj, header);
        return getRet(result, responseClass);
    }

    /**
     *
     * 发送分页请求
     *
     * @param url 接口地址
     * @param paramObj 返回类型
     * @param header 加密头
     * @param responseClass 返回子类型
     * @return T 所代表远程资源的响应结果
     * @param <T> 返回类型
     * @param <M> 返回子类型
     */
    public static <T, M> List<M> sendGetPage(String url,
                                             T paramObj,
                                             Map<String, String> header,
                                             Class<M> responseClass) {
        String result = HttpUtil.sendGet(url, paramObj, header);
        return getRetPage(result, responseClass);
    }


    /**
     *
     * 向指定 URL 发送POST方法的请求
     *
     * @param url           发送请求的 URL
     * @param param         请求参数，请求参数应该是 name1=value1 name2=value2 的形式。
     * @param header 请求头
     * @param <M> 返回子类型
     * @param responseClass 返回子类型
     * @return 所代表远程资源的响应结果
     *
     * Content type 'application/json;charset=UTF-8' not supported
     * // 请考虑接口入参的情况
     */
    public static <M> M sendPost(String url, String param, Map<String, String> header, Class<M> responseClass) {
        String result = HttpUtil.sendPost(url, param, header);
        return getRet(result, responseClass);
    }

    public static <M> M sendObjPost(String url, Object param, Map<String, String> header, Class<M> responseClass) {
        return sendPost(url, JSON.toJSONString(param), header, responseClass);
    }

    /**
     * 发送 post 文件请求
     *
     * @param url 远程接口地址
     * @param paramObj 返回类型
     * @param dir 文件路径
     * @param header 消息头
     * @param responseClass 子类型
     * @param <T> 返回类型
     * @param <M> 子类型
     * @return T 所代表远程资源的响应结果
     */
    public static <T, M> M sendPostFile(String url,
                                        T paramObj,
                                        String dir,
                                        Map<String, String> header,
                                        Class<M> responseClass) {
        String result = HttpUtil.sendPostFile(url, paramObj, dir, header);
        return getRet(result, responseClass);
    }

    /******************************** 私有方法 ****************************************/

    /**
     * 将返回值解析为泛型
     *
     * @param result 返回字符串
     * @param responseClass 返回子类型
     * // 实现 java 泛型声明 java 实例
     * BaseRes s = new BaseRes(responseClass.getDeclaredConstructor().newInstance());
     * @see com.alibaba.fastjson.parser.DefaultJSONParser#parse()
     * alibaba-fastjson 不支持泛型转换的原因:
     * fastJson 将所有{} 识别成 JSONObject; 而不是泛型, 故使用 JSONObject.parseObject 的成员变量为jsonObj
     * @see JSON#toJavaObject(JSON, Class)
     * alibaba-fastjson 实现逻辑:
     * JsonObject 是 java-map 的实现类, 通过 com.alibaba.fastjson.ci.junit.util.TypeUtils#castToJavaBean 实现转换
     * @see com.alibaba.fastjson.util.TypeUtils#castToJavaBean(Map, Class, com.alibaba.fastjson.parser.ParserConfig)
     * fastjson mapToJavaBean
     * @see com.alibaba.fastjson.TypeReference#TypeReference(java.lang.reflect.Type...)
     * 实现逻辑:
     * fastjson 用 ParameterizedType.class 通过递归构造了一个多层泛型的类型, 简直代码之精髓!
     *
     * @param <T> 返回类型
     * @return T 所代表远程资源的响应结果
     */
    private static <T> T getRet(String result, Class<T> responseClass) {
        DemoVO<T> baseRes = JSONObject.parseObject(result, new TypeReference<>(responseClass) {
        });
        if (baseRes == null) {
            return null;
        }
        if ("0000".equals(baseRes.getCode())) {
            try {
                if (responseClass == String.class) {
                    return (T) baseRes.getMessage();
                }

                T t = baseRes.getBody();
                if (t == null) {
                    throw new IllegalArgumentException("ret null!");
                }
                return t;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException(baseRes.getMessage());
        }
        return null;
    }

    /**
     * 将返回值解析为分页泛型
     *
     * @param result 结果字符串
     * @param responseClass 返回嵌套类型
     * @param <T> 返回类型 
     * @return 所代表远程资源的响应结果
     */
    private static <T> List<T> getRetPage(String result, Class<T> responseClass) {
        TypeReference<DemoVO<DemoPagingVO<T>>> typeReference = new TypeReference<>(responseClass) {
        };
        DemoVO<DemoPagingVO<T>> baseRes = JSONObject.parseObject(result, typeReference);
        if ("0000".equals(baseRes.getCode())) {
            try {
                if (responseClass == String.class) {
                    throw new Exception(baseRes.getMessage());
                }
                DemoPagingVO<T> pagingBody = baseRes.getBody();
                List<T> list = pagingBody.getList();
                if (list == null) {
                    throw new IllegalArgumentException("ret null!");
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException(baseRes.getMessage());
        }
        return null;
    }

    /**
     * Default constructor added by Java.
     */
    public HttpDemoUtil() {
    }
}
