package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Author :动力节点张开
 * 2019-5-27
 */
@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    /**
     * 查询总成交金额
     *
     * @return
     */
    @Override
    public Double queryTotalBidMoney() {
//        查询redis，如果存在，直接返回，不存在查询数据库
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(Constants.TOTAL_BID_MONEY);
        Double totalBidMoney = (Double) boundValueOperations.get();
//        判断是否存在
        if (null == totalBidMoney) {
            totalBidMoney = bidInfoMapper.selectTotalBidMonye();
//            存到缓存中
            boundValueOperations.set(totalBidMoney, 60, TimeUnit.SECONDS);
        }


        return totalBidMoney;
    }

    //    根据产品查询最近投资记录
    @Override
    public List<BidInfo> queryRecentBidByLoanId(Map<String, Object> paramMap) {
        List<BidInfo> bidInfos = bidInfoMapper.selectRecentBidByLoanId(paramMap);
        return bidInfos;
    }

    //  根据用户id查询最近投资记录
    @Override
    public List<BidInfo> queryRecentBidInfoByUid(Map<String, Object> paramMap) {
        List<BidInfo> bidInfoList = bidInfoMapper.selectRecentBidByPage(paramMap);
        return bidInfoList;
    }

    //根据用户id查询最近投资记录分页
    @Override
    public PaginationVO<BidInfo> queryRecentBidInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<BidInfo> bidInfoPaginationVO = new PaginationVO<>();
        List<BidInfo> bidInfoList = bidInfoMapper.selectRecentBidByPage(paramMap);
        Long count = bidInfoMapper.selectRecentBidByUidCount(paramMap);
        bidInfoPaginationVO.setTotal(count);
        bidInfoPaginationVO.setDataList(bidInfoList);
        return bidInfoPaginationVO;
    }

    //投资
    @Override
    public ResultObject invest(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);
        Integer uid = (Integer) paramMap.get("uid");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        Integer loanId = (Integer) paramMap.get("loanId");
        String phone = (String) paramMap.get("phone");

        //获取版本号
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        Integer version = loanInfo.getVersion();
        paramMap.put("version", version);
        //更新产品可投资金额
        int updateLeftProduceMoneyCount = loanInfoMapper.updateLeftProduceMoneyByLoanId(paramMap);
        if (updateLeftProduceMoneyCount > 0) {
            //修改余额

            int updateFinanceAccountCount = financeAccountMapper.updateFinanceAccountByUid(paramMap);
            if (updateFinanceAccountCount > 0) {
                //更新成功，新增投资
                BidInfo bidInfo = new BidInfo();
                bidInfo.setBidMoney(bidMoney);
                bidInfo.setBidTime(new Date());
                bidInfo.setBidStatus(1);
                bidInfo.setLoanId(loanId);
                bidInfo.setUid(uid);
                int bidInfoCount = bidInfoMapper.insert(bidInfo);
                if (bidInfoCount > 0) {
                    //查看产品是否满标
                    LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey(loanId);
                    if (0 == loanInfoDetail.getLeftProductMoney()) {
                        LoanInfo updateLoanInfo = new LoanInfo();
                        updateLoanInfo.setId(loanId);
                        updateLoanInfo.setProductStatus(1);
                        updateLoanInfo.setProductFullTime(new Date());
                        int updateLoanInfoCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
                        if (updateLoanInfoCount <= 0) {
                            resultObject.setErrorCode("当前人数较多，业务繁忙，请刷新页面后重新尝试");
                        }

                    }
                    redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP, phone, bidMoney);
                } else {
                    //新增投资失败
                    resultObject.setErrorCode("当前人数较多，业务繁忙，请刷新页面后重新尝试");
                }

            } else {
                //更新失败
                resultObject.setErrorCode("余额不足");
            }
        } else {
            //更新可投资金额失败，可能版本号不一致
            resultObject.setErrorCode("当前人数较多，业务繁忙，请刷新页面后重新尝试");
        }
        //

        return resultObject;
    }

    //从缓存中查询排行榜
    @Override
    public List<BidUser> queryBidUserTop() {
        List<BidUser> bidUserList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> sets = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);

        sets.forEach(set -> {
            BidUser bidUser = new BidUser();
            bidUser.setPhone((String) set.getValue());
            bidUser.setBidMoney(set.getScore());
            bidUserList.add(bidUser);
        });

        return bidUserList;
    }
}
