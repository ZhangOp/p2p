package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;
import java.util.Map;

/**
 * author :动力节点张开
 * 2019-6-1
 */
public interface IncomeRecordService {
    /**
     * 根据uid查询最近收益记录
     * @param paramMap
     * @return
     */
    List<IncomeRecord> queryRecentIncomeRecordByUid(Map<String, Object> paramMap);

    /**
     * 生成收益计划
     */
    void generateIncomePlan();

    /**
     * 收益返回
     */
    void generateIncomeBackPlan();
}
