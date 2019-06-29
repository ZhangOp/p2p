package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author :动力节点张开
 * 2019-5-27
 */
@Controller
public class IndexController {

    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private BidInfoService bidInfoService;




//    访问主页
    @RequestMapping(value = "/index")
    public  String index(HttpServletRequest request, Model model){
//        历史平均年化收益
        Double historyAverageRate= loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE, historyAverageRate);
//        获取平台注册人数
        Long totalUser  = userService.queryTotalUser();
        model.addAttribute(Constants.TOTAL_USER,totalUser);
//        获取累计投资金额
        Double totalBidMoney = bidInfoService.queryTotalBidMoney();
        model.addAttribute(Constants.TOTAL_BID_MONEY, totalBidMoney);

//        参数：页码，每页显示个数,产品类型
        Map<String,Object> mapParam = new HashMap<>();
//        当前页码
        mapParam.put("currentPage", 0);
//        获取新手宝
        mapParam.put("pageSize", 1);
        mapParam.put("productType", Constants.NOOB_LOAN);
        List<LoanInfo>  noobLoanList = loanInfoService.queryProductListByType(mapParam);
        model.addAttribute("noobLoanList", noobLoanList);


//        获取优质产品，显示4个
        mapParam.put("pageSize", 4);
        mapParam.put("productType", Constants.EXCELLENT_LOAN);
        List<LoanInfo>  excellentLoanInfoList = loanInfoService.queryProductListByType(mapParam);
        model.addAttribute("excellentLoanInfoList", excellentLoanInfoList);

//        获取散标,显示8个
        mapParam.put("pageSize", 8);
        mapParam.put("productType", Constants.COMMON_LOAN);
        List<LoanInfo>  commonLoanInfoList = loanInfoService.queryProductListByType(mapParam);
        model.addAttribute("commonLoanInfoList", commonLoanInfoList);
        return "index";
    }

}
