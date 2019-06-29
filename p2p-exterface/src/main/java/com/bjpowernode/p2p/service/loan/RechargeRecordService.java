package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author :动力节点张开
 * 2019-6-1
 */
public interface RechargeRecordService {
    List<RechargeRecord> queryRecentRechargeRecordByUid(Map<String, Object> paramMap);
    /**
     * 生成充值记录
     */
    int addRecharge(RechargeRecord rechargeRecord);

    /**
     * 根据订单号修改充值记录
     * @param rechargeRecord
     * @return
     */
    int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 充值
     * @param paramMap
     * @return
     */
    int recharge(HashMap<String, Object> paramMap);
}
