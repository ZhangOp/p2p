package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.config.AlipayConfig;
import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.bjpowernode.p2p.utils.DateUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.System.out;

/**
 * Author :动力节点张开
 * 2019-6-5
 */
@Controller
public class RechargeRecodeController {

    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private RedisService redisService;
    @RequestMapping(value = "/loan/toAlipayRecharge")
    public String  toAlipayRecharge(HttpServletRequest request, Model model,
                                  @RequestParam(value = "rechargeMoney",required = true) Double rechargeMoney
                                  ) throws Exception {
//        out.println("------------alipay--------------");

//        获取用户
        User sessionUser  = (User) request.getSession().getAttribute(Constants.USER);
        //创建订单号
        String rechargeNo   = DateUtils.getDimeTamp() +redisService.getOnlyNumber();
        //创建充值记录对象
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeDesc("支付宝充值");
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeTime(new Date());
        //生成充值订单
        int addRechargeCount = rechargeRecordService.addRecharge(rechargeRecord);
        //调用
        if (addRechargeCount >0){
            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeDesc", "支付宝充值");

        }else {
            model.addAttribute("trade_msg", "充值人数过多，请稍后重新尝试");
            return "toRechargeBack";
        }
//        Map<String,Object> paramMap = new HashMap<>();
//        paramMap.put("rechargeNo", rechargeNo);
//        paramMap.put("rechargeMoney", rechargeMoney);
//        paramMap.put("rechargeDesc", "支付宝充值");
//        HttpClientUtils.doPost("http://localhost:9090/pay/api/alipay", paramMap);
        return "toAlipay";
    }

    @RequestMapping(value = "/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam(value = "out_trade_no",required = true) String out_trade_no,
                             @RequestParam(value = "total_amount",required = true) String total_amount
                             ) throws Exception {

        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("out_trade_no", out_trade_no);
            //通过订单号查询充值结果
            String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/query", paramMap);
            //转换为json对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");
            //获取通信标识
            String code = tradeQueryResponse.getString("code");
            if (StringUtils.equals(code, "10000")){
                //通信成功
                String tradeStatus = tradeQueryResponse.getString("trade_status");
                if (StringUtils.equals(tradeStatus, "TRADE_CLOSED")){
                    //充值失败，改充值状态为2
                    RechargeRecord rechargeRecord = new RechargeRecord();
                    rechargeRecord.setRechargeNo(out_trade_no);
                    rechargeRecord.setRechargeStatus("2");

                    int  modifyRechargeRecordCount  =rechargeRecordService.modifyRechargeRecordByRechargeNo(rechargeRecord);
                    model.addAttribute("trade_msg", "充值失败，请稍后重新尝试");
                    return  "toRechargeBack";
                }
                if (StringUtils.equals(tradeStatus, "TRADE_SUCCESS")){
                    User sessionUSer = (User) request.getSession().getAttribute(Constants.USER);


                    paramMap.put("uid",sessionUSer.getId());
                    paramMap.put("rechargeNo", out_trade_no);
                    paramMap.put("rechargeMoney",total_amount);
                    //充值成功给用户充值，改充值状态为1(uid,充值金额，订单号)
                    int rechargeCount = rechargeRecordService.recharge(paramMap);
                    //修改失败
                    if (rechargeCount<=0){
                        model.addAttribute("trade_msg", "充值失败，请稍后重新尝试");
                        return  "toRechargeBack";
                    }

                }


            }else {
                //通信失败
                model.addAttribute("trade_msg", "通信失败");
                return  "toRechargeBack";
            }



        }else {
            model.addAttribute("trade_msg", "签名验证失败");
            return  "toRechargeBack";
        }




        return "redirect:/loan/myCenter";
    }


    @RequestMapping(value = "/loan/toWxpayRecharge")
    public String  toWxpayRecharge(HttpServletRequest request, Model model,
                                  @RequestParam(value = "rechargeMoney",required = true) Double rechargeMoney
                                  ) throws Exception {
        out.println("------------wxpay--------------");

//        获取用户
        User sessionUser  = (User) request.getSession().getAttribute(Constants.USER);
        //创建订单号
        String rechargeNo   = DateUtils.getDimeTamp() +redisService.getOnlyNumber();
        //创建充值记录对象
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeDesc("微信充值");
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeTime(new Date());
        //生成充值订单
        int addRechargeCount = rechargeRecordService.addRecharge(rechargeRecord);
        //调用
        if (addRechargeCount >0){
            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        }else {
            model.addAttribute("trade_msg", "充值人数过多，请稍后重新尝试");
            return "toRechargeBack";
        }




        return "showQRCode";
    }

    @RequestMapping(value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request,HttpServletResponse response,
                               @RequestParam(value = "rechargeNo",required = true)String rechargeNo,
                               @RequestParam(value = "rechargeMoney",required = true)String rechargeMoney
                               ) throws Exception {
        //调用pay工程接口，准备参数
        Map<String,Object> paramMap =new HashMap<>();
        paramMap.put("body", "微信扫码支付");
        paramMap.put("out_trade_no", rechargeNo);
        paramMap.put("total_fee", rechargeMoney);
        //发送请求
        String StringJson = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);
        JSONObject jsonObject = JSONObject.parseObject(StringJson);
        //获取返回码
        String code = jsonObject.getString("return_code");
        //判断是否成功
        if (StringUtils.equals(code, Constants.SUCCESS)){
            String resultCode = jsonObject.getString("result_code");
            //判断结果码是否成功
            if (StringUtils.equals(resultCode, Constants.SUCCESS)){
                //获取code——url
                String codeUrl = jsonObject.getString("code_url");
                //设置编码
                Map<EncodeHintType,String>  hintTypeStringMap = new HashMap<>();
                hintTypeStringMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                //生成二维码
                BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE,200,200,hintTypeStringMap);

                OutputStream outputStream = response.getOutputStream();

                //将该矩阵对象响应给浏览器
                MatrixToImageWriter.writeToStream(bitMatrix,"jpg",outputStream);

                outputStream.flush();
                outputStream.close();

            }else {
                //失败
                response.sendRedirect(request.getContextPath() + "/toRechargeBack.jsp");
            }
        }else {
            //失败
            response.sendRedirect(request.getContextPath() + "/toRechargeBack.jsp");
        }

    }
}
