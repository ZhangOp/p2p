package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author :动力节点张开
 * 2019-5-30
 */
@Service
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer id) {
        FinanceAccount financeAccount = financeAccountMapper.selectFinanceAccountByUid(id);
        return financeAccount;
    }
}
