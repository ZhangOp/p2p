package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.awt.SunHints;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author :动力节点张开
 * 2019-5-28
 */
@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private BidInfoService bidInfoService;
    @Autowired
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request,
                       @RequestParam(value = "ptype", required = false) Integer ptype,
                       @RequestParam(value = "currentPage", required = false) Integer currentPage,
                       Model model
                       ) {
        if (null == currentPage) {
            currentPage = 1;
        }
        Map<String, Object> paramMap = new HashMap<>();

        if (null != ptype) {
            paramMap.put("productType", ptype);
        }
//        每页显示9条数据
        int pageSize = 9;

        paramMap.put("currentPage", (currentPage - 1) * pageSize);
        paramMap.put("pageSize", pageSize);
        //分页查询产品信息()
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);
//        放入产品列表
        model.addAttribute("loanInfoList", paginationVO.getDataList());
        Long totalPage = (paginationVO.getTotal()+pageSize-1)/pageSize;
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("currentPage",currentPage);
        if (null != ptype){
            model.addAttribute("productType",ptype);
        }



//        获取用户排行榜
        List<BidUser> bidUserList = bidInfoService.queryBidUserTop();
        model.addAttribute("bidUserList", bidUserList);

        return "loan";

    }


    @RequestMapping("/loan/loanInfo")
    public String  loanInfo(HttpServletRequest request,Model model,
                            @RequestParam(value = "id",required = true) Integer id
                            ){
        //根据产品标识查询详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo", loanInfo);
//        设置参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("currentPage", 0);
        paramMap.put("pageSize", 10);
        paramMap.put("loanId", id);
        //根据产品标识查询最近投资记录

        List<BidInfo> bidInfos = bidInfoService.queryRecentBidByLoanId(paramMap);

        model.addAttribute("bidInfos", bidInfos);

        //用户可投资金额
        User sessionUser = (User) request.getSession().getAttribute(Constants.USER);
        if (sessionUser != null) {
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount", financeAccount);
        }


        return "loanInfo";
    }
}
