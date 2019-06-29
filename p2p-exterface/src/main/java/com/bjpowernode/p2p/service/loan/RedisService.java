package com.bjpowernode.p2p.service.loan;

/**
 * author :动力节点张开
 * 2019-6-1
 */
public interface RedisService {
    void put(String key, String value);

    String get(String key);

    /**
     * 获取唯一数字
     * @return
     */
    Long getOnlyNumber();
}
