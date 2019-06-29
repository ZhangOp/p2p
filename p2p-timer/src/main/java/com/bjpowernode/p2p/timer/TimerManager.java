package com.bjpowernode.p2p.timer;

import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Author :动力节点张开
 * 2019-6-3
 */
@Component
public class TimerManager {
     private  Logger logger = LogManager.getLogger(TimerManager.class);

    @Autowired
    private IncomeRecordService incomeRecordService;


    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomePlan(){
        logger.info("------------------生成收益计划开始 ------------------");
        incomeRecordService.generateIncomePlan();

        logger.info("------------------生成收益计划结束------------------");
    }


    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBackPlan(){
        logger.info("------------------生成收益返回计划开始 ------------------");
        incomeRecordService.generateIncomeBackPlan();

        logger.info("------------------生成收益返回计划结束------------------");
    }




}
