package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author :动力节点张开
 * 2019-5-27
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    //    获取总人数
    @Override
    public Long queryTotalUser() {
//        判断缓存中是否存在，有直接使用，没有则访问数据库

        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(Constants.TOTAL_USER);

//        获取指定key的值
        Long totalUser = (Long) boundValueOperations.get();

        if ( null == totalUser) {
            totalUser = userMapper.selectTotalUser();

            boundValueOperations.set(totalUser,15, TimeUnit.SECONDS);
        }
        return totalUser;
    }

    @Override
    public User queryUserByPhone(String phone) {
        User user = userMapper.selectUserByPhone(phone);
        return user;
    }

    @Override
    public ResultObject register(String phone, String loginPassword) {
        ResultObject resultObject = new ResultObject();
        //添加用户
        User user = new User();
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        user.setLoginPassword(loginPassword);
        user.setPhone(phone);
        int userCount = userMapper.insert(user);
        if (userCount>0){
            //添加个人账户
            user = userMapper.selectUserByPhone(phone);
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setAvailableMoney(888.0);
            financeAccount.setUid(user.getId());
            int financeCount = financeAccountMapper.insert(financeAccount);
            if (financeCount<=0){
                resultObject.setErrorCode(Constants.FAIL);
            }
        }else {
            resultObject.setErrorCode(Constants.FAIL);
        }
        resultObject.setErrorCode(Constants.SUCCESS);
        return resultObject;
    }

    @Override
    public int modifyUserInfoById(User user) {
        int count  =  userMapper.updateByPrimaryKeySelective(user);
        return count;
    }

    //登录功能
    @Override
    public User login(Map<String, String> paramMap) {
        User user =  userMapper.selectUserByPhoneAndPwd(paramMap);
        if (user!=null){
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setAddTime(new Date());
            userMapper.updateByPrimaryKeySelective(updateUser);

        }
        return user;
    }
}
