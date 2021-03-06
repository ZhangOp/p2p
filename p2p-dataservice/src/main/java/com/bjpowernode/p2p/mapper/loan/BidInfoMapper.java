package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 25 17:27:15 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 25 17:27:15 CST 2019
     */
    int insert(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 25 17:27:15 CST 2019
     */
    int insertSelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 25 17:27:15 CST 2019
     */
    BidInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 25 17:27:15 CST 2019
     */
    int updateByPrimaryKeySelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat May 25 17:27:15 CST 2019
     */
    int updateByPrimaryKey(BidInfo record);

    /**
     * 查询总投资金额
     * @return
     */
    Double selectTotalBidMonye();

    /**
     * 根据产品id查询最近投资信息
     * @param paramMap
     * @return
     */
    List<BidInfo> selectRecentBidByLoanId(Map<String, Object> paramMap);

    List<BidInfo> selectRecentBidByPage(Map<String,Object> paramMap);

    /**
     * 根据条件查询总记录数
     * @param paramMap
     * @return
     */
    Long selectRecentBidByUidCount(Map<String, Object> paramMap);

    /**
     * 根据产品loanId查询所有投资记录
     * @param id
     * @return
     */
    List<BidInfo> selectBidByLoanId(Integer id);
}