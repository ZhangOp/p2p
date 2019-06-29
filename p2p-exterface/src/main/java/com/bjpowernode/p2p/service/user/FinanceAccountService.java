package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * author :动力节点张开
 * 2019-5-30
 */
public interface FinanceAccountService {

    FinanceAccount queryFinanceAccountByUid(Integer id);
}
