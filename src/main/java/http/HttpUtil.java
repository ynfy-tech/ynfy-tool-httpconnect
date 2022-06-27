package http;


import com.alibaba.fastjson.JSON;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * @author Hsiong
 * @version 1.0.0
 * @desc 〈〉
 * @date 2020/7/9 5:42 下午
 */
public class HttpUtil {

    /**
     *
     * 向指定URL发送GET方法的请求
     * @param url   发送请求的URL
     * @param paramObj 请求参数，以对象的形式
     * @param header 请求头, "key1":"value1"的形式
     * @param <T>
     * @param <M>
     * @return String 所代表远程资源的响应结果
     */
    public static <T> String sendGet(String url, T paramObj, Map<String, String> header) {
        HttpURLConnection connection = null;
        try {
            connection = initGetConnection(url, paramObj);
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
     *
     * 向指定URL发送 delete 方法的请求
     * @param url   发送请求的URL
     * @param paramObj 请求参数，以对象的形式
     * @param header 请求头, "key1":"value1"的形式
     * @param <T>
     * @param <M>
     * @return String 所代表远程资源的响应结果
     */
    public static <T> String sendDelete(String url, T paramObj, Map<String, String> header) {
        HttpURLConnection connection = null;
        try {
            connection = initGetConnection(url, paramObj);
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
     *
     * 向指定 URL 发送POST方法的请求
     *
     * @apiNote Content type 'application/json;charset=UTF-8' not supported
     * // 请考虑接口入参的情况
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param responseClass
     * @param <M>
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, Map<String, String> header) {

        HttpURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (param != null) {
            byte[] content = param.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = connection.getOutputStream();) {
                os.write(content);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        return getResponseString(connection);
    }

    public static String sendObjPost(String url, Object param, Map<String, String> header) {
        return sendPost(url, JSON.toJSONString(param), header);
    }

    private final static String BOUNDARY = UUID.randomUUID().toString()
                                               .toLowerCase().replaceAll("-", "");// 边界标识
    private final static String PREFIX = "--";// 必须存在
    private final static String LINE_END = "\r\n";

    /**
     * 发送 post 文件请求
     * @param url
     * @param paramObj
     * @param dir
     * @param responseClass
     * @param <T>
     * @param <M>
     * @return
     */
    public static <T> String sendPostFile(String url, T paramObj, String dir, Map<String, String> header) {
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
            e.printStackTrace();
        }

        File file = new File(dir);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在");
        }

        try (
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            ) {
            try{

                // 请求参数部分
                StringBuilder requestParams = new StringBuilder();
                requestParams.append(PREFIX).append(BOUNDARY).append(LINE_END);
                requestParams.append("Content-Disposition: form-data; name=\"")
                             .append("file").append("\"").append(LINE_END);
                requestParams.append("Content-Type: text/plain; charset=utf-8")
                             .append(LINE_END);
                requestParams.append("Content-Transfer-Encoding: 8bit").append(
                    LINE_END);
                requestParams.append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                requestParams.append("ori2.jpeg");
                requestParams.append(LINE_END);
                out.write(requestParams.toString().getBytes());
                out.flush();


                String msg = "请求上传文件部分:\n";
                requestParams = new StringBuilder();
                requestParams.append(PREFIX).append(BOUNDARY).append(LINE_END);
                requestParams.append("Content-Disposition: form-data; name=\"")
                             .append("file").append("\"; filename=\"")
                             .append("ori2.jpeg").append("\"")
                             .append(LINE_END);
                requestParams.append("Content-Type:")
                             .append("multipart/form-data")
                             .append(LINE_END);
                requestParams.append("Content-Transfer-Encoding: 8bit").append(
                    LINE_END);
                requestParams.append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容

                out.write(requestParams.toString().getBytes());

                int bytes=0;
                byte[] buffer = new byte[1024];
                while((bytes=in.read(buffer))!=-1){
                    out.write(buffer,0,bytes);
                }

                out.write(LINE_END.getBytes());
                out.flush();

                // 请求结束标志
                String endTarget = PREFIX + BOUNDARY + PREFIX + LINE_END;
                out.write(endTarget.getBytes());
                out.flush();
            }catch(Exception e){
                throw new Exception(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getResponseString(connection);
    }

    /******************************** 私有方法 ****************************************/

    /**
     * 设置通用的请求属性
     * @param url
     * @return
     * @throws IOException
     */
    private static HttpURLConnection getConnection(String url) throws IOException {
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setConnectTimeout(0);
        conn.setReadTimeout(0);
        return conn;
    }

    /**
     * 拼接 get 请求参数
     * @param url
     * @param paramObj
     * @param <T>
     * @return
     */
    private static <T> String appendGetUrlParam(String url, T paramObj) {
        StringBuilder urlNameString = new StringBuilder(url);
        if (paramObj != null) {
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
     * 获取URLConnection
     * @param url
     * @param paramObj
     * @param <T>
     * @return
     * @throws IOException
     */
    private static <T> HttpURLConnection initGetConnection(String url, T paramObj) throws IOException {
        String urlParams = appendGetUrlParam(url, paramObj);
        HttpURLConnection connection = getConnection(urlParams);

        return connection;
    }

    /**
     * 获取返回值
     * @param connection
     * @return
     */
    private static String getResponseString(HttpURLConnection connection) {
        String result = null;

        Long time1 = System.currentTimeMillis();

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
            e.printStackTrace();
        }

        Long time2 = System.currentTimeMillis();
        System.out.println("getInput ms " + (time2 - time1));


        // 定义 ByteArrayOutputStream 输入流来读取URL的响应
        try (
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[2 << 12];
            int length;
            while ((length = retStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            result = outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
