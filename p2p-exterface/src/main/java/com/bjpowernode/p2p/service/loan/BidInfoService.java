package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.List;
import java.util.Map;

/**
 * author :动力节点张开
 * 2019-5-27
 */
public interface BidInfoService {
    Double queryTotalBidMoney();

    /**
     * 获取最近投资记录
     * @param paramMap
     *
     */
    List<BidInfo> queryRecentBidByLoanId(Map<String, Object> paramMap);

    /**
     * 根据用户id查询最近投资记录
     * @param
     * @return
     */
    List<BidInfo> queryRecentBidInfoByUid(Map<String,Object> paramMap);

    /**
     * 根据用户id查询最近投资记录分页
     * @param paramMap
     * @return
     */
    PaginationVO<BidInfo> queryRecentBidInfoByPage(Map<String, Object> paramMap);

    /**
     * 投资
     * @param paramMap
     * @return
     */
    ResultObject invest(Map<String, Object> paramMap);

    /**
     * 用户投资排行榜
     * @return
     */
    List<BidUser> queryBidUserTop();
}
