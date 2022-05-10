package ci.junit.model;

import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 * description: BaseRes
 * author: sunxz
 * date: 2020/5/8 15:42
 */
@Data
public class BaseRes<T> implements Serializable {

    /**
     * 处理结果代码
     */
    private String code;

    /**
     * 处理结果状态
     */
    private String status;

    /**
     * 处理结果描述信息
     */
    private String message;

    /**
     * 请求结果生成时间戳
     */
    private String time;


    /**
     * 处理结果数据信息
     */
    private T body;

    public BaseRes() {
    }
    public BaseRes(T body) {
        this.body = body;
    }
    public BaseRes(String code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        this.time = formatter.format(ZonedDateTime.now().toInstant());
    }
    public BaseRes(String code, String status, String message, T body) {
        this.code = code;
        this.status = status;
        this.message = message;
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        this.time = formatter.format(ZonedDateTime.now().toInstant());
        this.body = body;
    }

    public void setTime() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        this.time = formatter.format(ZonedDateTime.now().toInstant());
    }
}
