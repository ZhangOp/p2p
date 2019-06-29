package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.Map;

/**
 * author :动力节点张开
 * 2019-5-27
 */
public interface UserService {
    /**
     * 获取平台注册人数，
     * @return 返回Long 人数
     */
    Long queryTotalUser();

    /**
     * 通过手机号查询用户
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 注册
     * @param phone
     * @param loginPassword
     * @return
     */
    ResultObject register(String phone, String loginPassword);

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    int modifyUserInfoById(User user);

    /**
     * 登录操作
     * @param paramMap
     * @return
     */
    User login(Map<String, String> paramMap);
}
