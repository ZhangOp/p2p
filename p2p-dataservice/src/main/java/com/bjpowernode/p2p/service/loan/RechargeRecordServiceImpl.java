package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author :动力节点张开
 * 2019-6-1
 */
@Service
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public List<RechargeRecord> queryRecentRechargeRecordByUid(Map<String, Object> paramMap) {
        List<RechargeRecord> rechargeRecordList = rechargeRecordMapper.selectRecentRechargeRecordByPage(paramMap);
        return rechargeRecordList;
    }

    @Override
    public int addRecharge(RechargeRecord rechargeRecord) {
        int i = rechargeRecordMapper.insertSelective(rechargeRecord);
        return i;
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {

        return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Override
    public int recharge(HashMap<String, Object> paramMap) {
        //更新余额
        int updateFinanceCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);

        if (updateFinanceCount>0){
            //更新充值记录状态为1
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
            rechargeRecord.setRechargeStatus("1");
            int i = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
            if (i<=0){
                return 0;
            }

        }else {
            return 0;
        }
        return 1;
    }


}
