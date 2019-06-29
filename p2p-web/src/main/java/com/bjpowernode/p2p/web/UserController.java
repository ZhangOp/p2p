package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.*;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author :动力节点张开
 * 2019-5-30
 */
//@RestController
@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private BidInfoService bidInfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private IncomeRecordService incomeRecordService;


    @RequestMapping(value = "/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(value = "phone", required = true) String phone) {
        Map<String, Object> retMap = new HashMap<>();
//        验证手机号是否重复
        User user = userService.queryUserByPhone(phone);
        if (null != user) {
            retMap.put(Constants.ERROR_MESSAGE, "该手机号已经存在");
            return retMap;
        }
        retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
        return retMap;
    }

    //注册功能
    @RequestMapping(value = "/loan/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(HttpServletRequest request,
                           @RequestParam(value = "phone", required = true) String phone,
                           @RequestParam(value = "loginPassword", required = true) String loginPassword
    ) {
        Map<String, Object> retMap = new HashMap<>();
        //注册
        ResultObject resultObject = userService.register(phone, loginPassword);
        //判断是否注册成功
        if (StringUtils.endsWithIgnoreCase(Constants.SUCCESS, resultObject.getErrorCode())) {
            retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
            request.getSession().setAttribute(Constants.USER, userService.queryUserByPhone(phone));

        } else {
            retMap.put(Constants.ERROR_MESSAGE, "注册失败，清稍后重写尝试");
        }

        return retMap;
    }

    @RequestMapping("/loan/checkCaptcha")
    @ResponseBody
    public Object checkCaptcha(HttpServletRequest request,
                               @RequestParam(value = "captcha", required = true) String captcha) {
        Map<String, Object> retMap = new HashMap<>();
        String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);
        if (!sessionCaptcha.equalsIgnoreCase(captcha)) {
            retMap.put(Constants.ERROR_MESSAGE, "验证码错误");
            return retMap;
        }
        retMap.put(Constants.ERROR_MESSAGE, Constants.OK);

        return retMap;
    }

    //查询账户余额
    @RequestMapping(value = "/loan/financeAccount")
    @ResponseBody
    public Object myFinanceAccount(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Constants.USER);

        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
        return financeAccount;
    }

    //实名认证
    @RequestMapping(value = "/loan/verifyRealName")
    @ResponseBody
    public Object verifyRealName(HttpServletRequest request,
                                 @RequestParam(value = "idCard", required = true) String idCard,
                                 @RequestParam(value = "realName", required = true) String realName
    ) throws Exception {
        Map<String, Object> retMap = new HashMap<>();
        //调用实名认证接口,获取身份二要素是否匹配
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appkey", "20b03fbc4a888e601fdc6167ddcfda36");
        paramMap.put("realName", realName);
        paramMap.put("cardNo", idCard);
        //调用接口
//        String jsonString = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);
        String jsonString = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"成功\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"乐天磊\",\n" +
                "            \"idcard\": \"350721197702134399\",\n" +
                "            \"isok\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";
        //解析json
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //判断通讯标识
        String code = jsonObject.getString("code");
        if (StringUtils.equals(code, "10000")) {
            //获取是否匹配
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
            if (isok) {
                //从session获取当前用户信息
                User sessionUser = (User) request.getSession().getAttribute(Constants.USER);
                //更新用户信息
                User updateUser = new User();
                updateUser.setId(sessionUser.getId());
                updateUser.setIdCard(idCard);
                updateUser.setName(realName);
                //调用service方法修改用户信息
                int updateCount = userService.modifyUserInfoById(updateUser);
                //更新成功
                if (updateCount > 0) {
                    sessionUser.setIdCard(idCard);
                    sessionUser.setName(realName);
                    retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
                } else {//更新失败
                    retMap.put(Constants.ERROR_MESSAGE, "实名认证失败");
                    return retMap;
                }
                //认证失败
            } else {
                retMap.put(Constants.ERROR_MESSAGE, "实名认证失败");
                return retMap;
            }
            //通讯失败 返回非10000
        } else {

            retMap.put(Constants.ERROR_MESSAGE, "通讯故障，请稍后重新尝试");
            return retMap;
        }

        //更新当前用户信息
        return retMap;
    }

    //注销session
    @RequestMapping("/loan/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(Constants.USER);
        //两种方式
//        request.getSession().invalidate();

        return "redirect:/index";

    }

    //登录页面查询平台宣传信息
    @RequestMapping("/loan/loadStat")
    public @ResponseBody
    Object loadStat() {
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        Long totalUser = userService.queryTotalUser();
        Double bidMoney = bidInfoService.queryTotalBidMoney();
        Map<String, Object> retMap = new HashMap<>();
        retMap.put(Constants.HISTORY_AVERAGE_RATE, historyAverageRate);
        retMap.put(Constants.TOTAL_BID_MONEY, bidMoney);
        retMap.put(Constants.TOTAL_USER, totalUser);

        return retMap;
    }


    //登录
    @RequestMapping("/loan/login")
    public @ResponseBody
    Object login(HttpServletRequest request,
                 @RequestParam(value = "phone", required = true) String phone,
                 @RequestParam(value = "loginPassword", required = true) String loginPassword,
                 @RequestParam(value = "messageCode",required = true) String messageCode
    ) {
        Map<String, Object> retMap = new HashMap<>();
        //验证验证码
        String result = redisService.get(phone);
        if (!StringUtils.equals(result, messageCode)){
            retMap.put(Constants.ERROR_MESSAGE, "验证码不正确");
            return retMap;
        }


//        查询用户是否存在，且修改登录时间
        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("phone", phone);
        paramMap.put("loginPassword", loginPassword);
        User user = userService.login(paramMap);
        if (null == user) {
            retMap.put(Constants.ERROR_MESSAGE, "用户名或密码错误,请重新输入");
            return retMap;
        }
        retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
        request.getSession().setAttribute(Constants.USER, user);
        return retMap;
    }

    //获取短信验证码
    @RequestMapping(value = "/loan/messageCode")
    public @ResponseBody
    Object messageCode(@RequestParam(value = "phone", required = true) String phone) throws Exception {
        Map<String,Object> retMap = new HashMap<>();
        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("mobile", phone);
        paramMap.put("appkey", "20b03fbc4a888e601fdc6167ddcfda36");
        //生成验证码
        String messageCode = this.getRandomCode(4);
        paramMap.put("content", "【凯信通】您的验证码是：" + messageCode);
        //解析返回json·
//        String josnString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);
        String josnString = "{\"code\":\"10000\",\"charge\":false,\"remain\":0,\"msg\":\"查询成功\",\"result\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-827746</remainpoint>\\n <taskID>91244940</taskID>\\n <successCounts>1</successCounts></returnsms>\"}";

        JSONObject jsonObject = JSONObject.parseObject(josnString);

        String code = jsonObject.getString("code");
        //判断是否通讯成功
        if (StringUtils.equals("10000", code)){
            String resultXMl = jsonObject.getString("result");
            Document document = DocumentHelper.parseText(resultXMl);

            Node node = document.selectSingleNode("//returnstatus");
            String text = node.getText();
            if (StringUtils.equals("Success", text)){

                retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
                //将验证码放入到redis中
                redisService.put(phone,messageCode);


                retMap.put("messageCode", messageCode);

            }else {
                retMap.put(Constants.ERROR_MESSAGE, "短信发送失败");
                return retMap;
            }
        }else {
            retMap.put(Constants.ERROR_MESSAGE, "通讯失败");
            return retMap;
        }
        return retMap;
    }

    //个人中心
    @RequestMapping("/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model){
        Map<String,Object> retMap = new HashMap<>();
        User sessionUser = (User) request.getSession().getAttribute(Constants.USER);
        //放入查询参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("currentPage", 0);
        paramMap.put("pageSize", 5);
//        根据用户获取可用金额
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
//        根据用户获取最近投资
        List<BidInfo> bidInfoList =  bidInfoService.queryRecentBidInfoByUid(paramMap);
//        根据用户获取最近充值
        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRecentRechargeRecordByUid(paramMap);
//        根据用户获取最近收益
        List<IncomeRecord> incomeRecordList = incomeRecordService.queryRecentIncomeRecordByUid(paramMap);
        model.addAttribute("financeAccount", financeAccount);
        model.addAttribute("bidInfoList", bidInfoList);
        model.addAttribute("rechargeRecordList", rechargeRecordList);
        model.addAttribute("incomeRecordList", incomeRecordList);

        return "myCenter";
    }

    //分页投资记录
    @RequestMapping(value = "/loan/myInvest")
    public String myInvest(HttpServletRequest request,Model model,
                           @RequestParam(value = "currentPage",required = false) Integer currentPage
                           ){

        Map<String,Object> paramMap = new HashMap<>();
        //获取当前用户                   ){
        User sessionUser = (User)request.getSession().getAttribute(Constants.USER);
        if (null == currentPage) {
            currentPage = 1;
        }
        int pageSize = 3;
        paramMap.put("uid", sessionUser.getId());
        paramMap.put("currentPage", currentPage);
        paramMap.put("pageSize", pageSize);
        PaginationVO<BidInfo> vo = bidInfoService.queryRecentBidInfoByPage(paramMap);
        Long totalPage = (vo.getTotal()+pageSize-1)/pageSize;

        model.addAttribute("totalPage", totalPage);
        model.addAttribute("bidInfoList", vo.getDataList());
        model.addAttribute("totalRows", vo.getTotal());
        model.addAttribute("currentPage",currentPage);

        return "myInvest";
    }


    //获取验证码
    private String getRandomCode(int count) {
        String[] arr = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int round = (int) Math.round(Math.random() * 9);
            stringBuilder.append(round);
        }

        return stringBuilder.toString();
    }


    //
    @RequestMapping(value = "/loan/verifyBankCard")
    public  @ResponseBody Object verifyBankCard(@RequestParam(value = "idCard",required = true)String idCard,
                                 @RequestParam(value = "realName",required = true)String realName,
                                 @RequestParam(value = "cardNo",required = true)String cardNo,
                                 @RequestParam(value = "phone",required = true)String phone,
                                 Model model
                                 ) throws Exception {

        //准备参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("appkey", "改及自己的appkey");
        paramMap.put("accName", realName);
        paramMap.put("cardPhone",phone);
        paramMap.put("certificateNo",idCard);
        paramMap.put("cardNo",cardNo);
        //调用第三方接口
        String StringJson = HttpClientUtils.doPost("https://way.jd.com/YOUYU365/keyelement", paramMap);
        /*String StringJson = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"serialNo\": \"5590601f953b512ff9695bc58ad49269\",\n" +
                "        \"respCode\": \"000000\",\n" +
                "        \"respMsg\": \"验证通过\",\n" +
                "        \"comfrom\": \"jd_query\",\n" +
                "        \"success\": \"true\"\n" +
                "    }\n" +
                "}";*/

        Map<String,Object> retMap = new HashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(StringJson);
        String code = jsonObject.getString("code");
        if (StringUtils.equals(code, "10000")){
            JSONObject result = jsonObject.getJSONObject("result");
            String success = result.getString("success");
            if (StringUtils.equalsIgnoreCase(success, "true")){
                retMap.put("errorMessage", "绑定成功");
            }else {

                retMap.put("errorMessage", "绑定失败");
            }

        }else {

            retMap.put("errorMessage", "绑定失败");
        }

        return retMap;
    }



}
