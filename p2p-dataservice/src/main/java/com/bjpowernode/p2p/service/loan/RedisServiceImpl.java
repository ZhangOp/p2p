package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Author :动力节点张开
 * 2019-6-1
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void put(String key, String value) {
        redisTemplate.opsForValue().set(key, value,60, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        String  result = (String) redisTemplate.opsForValue().get(key);
        return result;
    }

    @Override
    public Long getOnlyNumber() {

        Long increment = redisTemplate.opsForValue().increment(Constants.ONLY_KEY, 1);

        return increment;
    }
}
