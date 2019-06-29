package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Author :动力节点张开
 * 2019-6-4
 */
@Data
public class BidUser implements Serializable {
    private static final long serialVersionUID = -2805445908256549211L;
    private String phone;
    private Double bidMoney;
}
