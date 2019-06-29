package com.bjpowernode.pay.web;

import com.bjpowernode.http.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Author :动力节点张开
 * 2019-6-6
 */
@Controller
public class WxpayController {


    @RequestMapping(value = "/api/wxpay")
    public @ResponseBody Object wxpay(HttpServletRequest request,
                                      @RequestParam(value = "body",required = true)String body,
                                      @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                                      @RequestParam(value = "total_fee",required = true)String total_fee

                                      ) throws Exception {
        //准备参数
        Map<String,String> requestparamMap = new HashMap<>();
        requestparamMap.put("appid", "wx8a3fcf509313fd74");
        requestparamMap.put("mch_id", "1361137902");
        requestparamMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述
        requestparamMap.put("body", body);
        //订单号
        requestparamMap.put("out_trade_no", out_trade_no);

        BigDecimal bigDecimal = new BigDecimal(total_fee);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal("100"));
        int i = multiply.intValue();
        //标价金额
        requestparamMap.put("total_fee", String.valueOf(i));
        //终端ip
        requestparamMap.put("spbill_create_ip", "127.0.0.1");
        //通知地址
        requestparamMap.put("notify_url", "http:localhost:8080/p2p/wxpayNotify");
        requestparamMap.put("trade_type", "NATIVE");
        requestparamMap.put("product_id", out_trade_no);


        //准备签名
        String sign = WXPayUtil.generateSignature(requestparamMap, "367151c5fd0d50f1e34a68a802d6bbca");
        requestparamMap.put("sign",sign);
        //参数转换为xml
        String requestDataXml = WXPayUtil.mapToXml(requestparamMap);

        //将xml传给api接口
        String responseXml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", requestDataXml);

        Map<String, String> xmlToMap = WXPayUtil.xmlToMap(responseXml);
        return xmlToMap;
    }
}
