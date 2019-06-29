package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author :动力节点张开
 * 2019-5-28
 */
@Data
public class PaginationVO<T> implements Serializable {
    //总记录数
    private Long total;
    //查询的数据List
    private List<T> dataList;
}
