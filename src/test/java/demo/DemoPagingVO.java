package demo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回信息
 * @param <T> 返回类型
 */
@Getter
@Setter
public class DemoPagingVO<T> implements Serializable {


    /**
     * 总记录数
     */
    private long    total;
    /**
     * 结果
     */
    private List<T> list;

    /**
     * Default constructor added by Java.
     * @param total 总记录数
     * @param list 结果
     */
    public DemoPagingVO(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}
