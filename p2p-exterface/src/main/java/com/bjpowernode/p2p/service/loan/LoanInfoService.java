package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

/**
 * author :动力节点张开
 * 2019-5-27
 */
public interface LoanInfoService {
    Double queryHistoryAverageRate();

    List<LoanInfo> queryProductListByType(Map<String, Object> mapParam);


    PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);

    /**
     * 根据id查询产品详情
     * @param id
     * @return
     */
    LoanInfo queryLoanInfoById(Integer id);
}
