package http;


import http.constant.ProtocolConstant;
import http.constant.TimeConstant;
import http.constant.HttpEnum;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * http 工具类
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2020/7/9 5:42 下午
 */
public class InitUtil {

    /**
     *
     * 向指定 URL 发送POST方法的请求
     * <p>
     * Content type 'application/json;charset=UTF-8' not supported
     * // 请考虑接口入参的情况
     *
     * @param url    发送请求的 URL
     * @param httpEnum 链接类型枚举
     * @return T 所代表远程资源的响应结果
     */
    protected String sendPostOrPutConnection(String url, HttpEnum httpEnum) {

        HttpURLConnection connection = getConnection(url, httpEnum);

        return getResponseString(connection);
    }

    /**
     *
     * 向指定 URL 发送POST方法的请求
     * <p>
     * Content type 'application/json;charset=UTF-8' not supported
     * // 请考虑接口入参的情况
     *
     * @param url    发送请求的 URL
     * @param header 请求头
     * @param httpEnum 链接类型枚举
     * @return T 所代表远程资源的响应结果
     */
    protected String sendPostOrPutConnection(String url, HttpEnum httpEnum, Map<String, String> header) {

        HttpURLConnection connection = getConnection(url, httpEnum);
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return getResponseString(connection);
    }

    /**
     *
     * 向指定 URL 发送POST方法的请求
     * <p>
     * Content type 'application/json;charset=UTF-8' not supported
     * // 请考虑接口入参的情况
     *
     * @param url    发送请求的 URL
     * @param param  请求参数，请求参数应该是 name1=value1 name2=value2 的形式。
     * @param header 请求头
     * @param httpEnum 链接类型枚举
     * @return T 所代表远程资源的响应结果
     */
    protected String sendPostOrPutConnection(String url, HttpEnum httpEnum, Map<String, String> header, String param) {

        HttpURLConnection connection = getConnection(url, httpEnum);

        // support the default JSON content type in the HttpPostUtil
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        
        if (param != null) {
            byte[] content = param.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = connection.getOutputStream();) {
                os.write(content);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        return getResponseString(connection);
    }

    /**
     * 设置通用的请求属性
     *
     * @param url 远程接口
     * @return HttpURLConnection
     */
    protected HttpURLConnection getConnection(String url) {
        if (!url.contains(ProtocolConstant.PROTOCOL_HTTP)) {
            // 如果 url 不包含 http 协议头, 则自动拼接 http; 兼容 https; 尽可能实现开箱可用
            url = ProtocolConstant.PROTOCOL_HTTP_PREFIX + url;
        }
        // 打开和URL之间的连接
        HttpURLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setConnectTimeout(TimeConstant.CONNECT_TIME_OUT);
            conn.setReadTimeout(TimeConstant.READ_TIME_OUT);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return conn;
    }

    /**
     * 设置通用的请求属性
     *
     * @param url 远程接口
     * @param httpEnum 调用类型 
     * @return HttpURLConnection
     * @return HttpURLConnection
     */
    protected HttpURLConnection getConnection(String url, HttpEnum httpEnum) {
        HttpURLConnection connection = getConnection(url);
        try {
            connection.setRequestMethod(httpEnum.name());
            connection.setConnectTimeout(TimeConstant.CONNECT_TIME_OUT);
            connection.setReadTimeout(TimeConstant.READ_TIME_OUT);
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (ProtocolException e) {
            throw new IllegalArgumentException(e);
        }
        return connection;

    }

    /**
     * 拼接 get 请求参数
     *
     * @param url      接口地址
     * @param paramObj 返回泛型
     * @param <T>      返回类型
     * @return T 返回类型
     */
    protected <T> String appendGetUrlParam(String url, T paramObj) {
        StringBuilder urlNameString = new StringBuilder(url);
        if (paramObj != null) {
            if (String.class.equals(paramObj.getClass())) {
                throw new IllegalArgumentException("Param can't be string! ");
            }
            urlNameString.append("?");
            Class c = paramObj.getClass();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                field.setAccessible(true);
                Object filedValue = null;
                try {
                    filedValue = field.get(paramObj);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                if (filedValue == null) {
                    continue;
                }
                fieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
                String filedValueStr = URLEncoder.encode(filedValue.toString(), StandardCharsets.UTF_8);
                urlNameString.append(fieldName).append("=").append(filedValueStr).append("&");
            }
        }
        return urlNameString.toString();
    }

    /**
     * 获取返回值
     *
     * @param connection connection
     * @return String 所代表远程资源的响应结果
     */
    protected String getResponseString(HttpURLConnection connection) {
        String result = null;


        // 处理不同的retcode
        InputStream retStream = null;
        try {
            int retCode = connection.getResponseCode();
            if (retCode == HttpURLConnection.HTTP_OK) {
                retStream = connection.getInputStream();
            } else {
                retStream = connection.getErrorStream();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        
        // 定义 ByteArrayOutputStream 输入流来读取URL的响应
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[2 << 12];
            int length;
            while ((length = retStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            result = outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Default constructor added by Java.
     */
    public InitUtil() {
    }
}
