package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Author :动力节点张开
 * 2019-6-3
 */
@Controller
public class BidInfoController {

    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/loan/invest")
    public @ResponseBody Object invest(HttpServletRequest request,
                  @RequestParam(value = "bidMoney",required = true) Double bidMoney,
                  @RequestParam(value = "loanId",required = true) Integer loanId
                         ){
        //取出当前用户
        User sessionUser = (User) request.getSession().getAttribute(Constants.USER);


        //准备参数 需要参数uid，bidMoney,loanId
        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("uid", sessionUser.getId());
        paramMap.put("bidMoney", bidMoney);
        paramMap.put("loanId", loanId);
        paramMap.put("phone", sessionUser.getPhone());

        //提交投资
        Map<String,Object> retMap = new HashMap<>();
        ResultObject resultObject = bidInfoService.invest(paramMap);
        if (StringUtils.equals(resultObject.getErrorCode(), Constants.SUCCESS)){

            retMap.put(Constants.ERROR_MESSAGE, Constants.OK);

        }else {
            retMap.put(Constants.ERROR_MESSAGE, resultObject.getErrorCode());
        }



        return retMap;
    }


}
