package ci.junit.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回信息
 * @param <T>
 */
@Data
public class PagingResVO<T> implements Serializable {


    /**
     * 总记录数
     */
    private long    total;
    /**
     * 结果
     */
    private List<T> list;

}
