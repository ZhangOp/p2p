package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author :动力节点张开
 * 2019-6-1
 */
@Service
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public List<IncomeRecord> queryRecentIncomeRecordByUid(Map<String, Object> paramMap) {
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectRecentIncomeRecordByPage(paramMap);
        return incomeRecordList;
    }

    @Override
    public void generateIncomePlan() {
        //找到满标记录，返回LoanInfo
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByStatus(1);
        //循环list集合，获取LoanInfo对象
        loanInfoList.forEach(loanInfo -> {
            //获取LoanInfo投资记录，返回投资记录集合
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidByLoanId(loanInfo.getId());
            //遍历集合，获取投资记录对象
            bidInfoList.forEach(bidInfo -> {
                //给当前投资记录生成对应收益计划
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setIncomeStatus(0);
                //收益时间 满标时间+产品的周期
                Date incomeDate = null;
                //收益金额  投资金额*日利率*投资天数
                Double incomeMoney = null;
                if (Constants.NOOB_LOAN == loanInfo.getProductType()) {
                    //新手宝，周期为天
                    incomeDate = DateUtils.getDateByAddDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();

                } else {
                    //其他产品，周期为月

                    incomeDate = DateUtils.getDateByAddMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle() * 30;
                }
                incomeMoney = Math.round(incomeMoney * Math.pow(10, 2)) / Math.pow(10, 2);
                //添加收益和收益时间
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                incomeRecordMapper.insertSelective(incomeRecord);

            });
            //更新产品状态为2 满标且生成收益计划
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);

        });


    }
    //收益返回
    @Override
    public void generateIncomeBackPlan() {
        //查询收益记录状态为0的且收益时间与当前时间一致的收益计划
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByStatusAndDate(0);

        incomeRecordList.forEach(incomeRecord -> {
            //修改账户余额
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("uid", incomeRecord.getUid());
            paramMap.put("bidMoney", incomeRecord.getBidMoney());
            paramMap.put("incomeMoney", incomeRecord.getIncomeMoney());
            int updateFinanceAccount = financeAccountMapper.updateFinanceAccountByIncomeRecord(paramMap);
            if (updateFinanceAccount > 0){
                //更新当前收益计划的状态为1
                IncomeRecord updateIncome = new IncomeRecord();
                updateIncome.setId(incomeRecord.getId());
                updateIncome.setIncomeStatus(1);
                incomeRecordMapper.updateByPrimaryKeySelective(updateIncome);
            }

        });

    }
}
