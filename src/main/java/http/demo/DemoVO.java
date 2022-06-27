package http.demo;

import lombok.Data;

import java.io.Serializable;


/**
 * 返回统一类, 可自定义
 * description: BaseRes
 */
@Data
public class DemoVO<T> implements Serializable {

    /**
     * 处理结果代码
     */
    private String code;

    /**
     * 处理结果状态
     */
    private String status;

    /**
     * 请求结果生成时间戳
     */
    private String time;

    /**
     * 处理结果描述信息
     */
    private String message;


    /**
     * 处理结果数据信息
     */
    private T body;

}
