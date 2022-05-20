package ci.junit.model;

import lombok.Data;

import java.io.Serializable;


/**
 * description: BaseRes
 */
@Data
public class BaseVO<T> implements Serializable {

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

}
