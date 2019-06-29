package com.bjpowernode.pay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016092900626611";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCeLtYT2K1aoPiVUtjtf5jex9XVZYmEzd3edk5pXgmDVWyFqm1MrAZ8116UPn4KZqPDM/X+dyPe4qlVecG252NSwUz6RHy6kuqLZ8vwrbG+GnNM/A8sUKVMP+uL74U2QlbeD5/biHToUh5OZmEuR3P6z72VXkQ0+RIRIoI6fs04nMoyH4EjsqBlef8LtoQAdDKrafk0cTixlHBn+0+bEMUpLmb/0/9tSdClAxiQW6T4Q1g/nbyC1x5KUNvUnwuubxatxeHRMZva1RClLbcID8vlvTG5MHI4sY0OytsnFYuOh1FUdzcAOshrr0jab/NNefs/Az9L9rNJ6yphJZmUexLbAgMBAAECggEAPksxkVVGR6eW3a9nsHgMaseF5Wj8NCmik2ZB0OrwkiEFAMqyCnzAfU+Pdo/5sPzkxw3idVjT7oWSrA+sAuyyx7dKKlDoUc1jwoyY2up8UeI2v/2PuCL2RcXybARikJClhwPeL2VYrNkSCEv3P3NyMDa5cWZTNA1WWRse8AxZo04xBRAYkLwa8MZ7PElVFp5X1LOYoMZkDlWGJQXWXUTFQTg0rofQpuoXDPYuLDnfDAoF0Y3rIrhMFMdzQqWAKb/0MxThzoIpfHFVMPYkc3Sc6KhxV2cDjBPI9mnHEN1Woe6WaTMOzxUEA2SvMp3zUk9Kwk/hLmKVwWtTZUMznvSWQQKBgQD89FJ6lgcxzT9FosobPWFdziVc9AELq9Sn8bcZocPI5XtGwZConO22RTlFTcHQ56fH3YdLISpfsoIIjmQ0qhZFDUfdehrsaMmZSbLVS7UFWX8AKqdpXfQ9YyFI/ZoRyAoNJJ7hDyWMJA/vRAUME4OxZsqZtW6KCi0U4rvSr32A+wKBgQCgFmbBF5jOq+UGGJqBEGhrE9kf5K1ZIAnpvqpiANkG4L4024i7E/r03CQOpraPomqlEYm0ejXMLmm9ezWvFJzsYM+zgZeIkRzq3IXtbyyPKtCZLCKU3tUvlymPrQjG8VFFqDKjbX+8K+vwUu1Wxexb3IolgtCwB1KDls7c4ibPoQKBgAifY3heSNx/vJppFC6dhwYlksx6XNbDArq+2Jj9FKlyeSNOUdoNj2TZMzyTi/nNC8EaYhRDiyg784bLI8tPyPaNn1eDZAuO/uBEzN3MeN030cZ1rcM/uqWlDWtpiaj1grIz/154n7UJPanQTcO9no2bjWhz8NKvyh2eJPznkoWlAoGADfIFn3p7wD0vJTnNc8LHJdTDzEhyZVoqIueOFh12ymeXiik5nvpdmSj4JyKUsqx2V8zQTx16REAiy/PIuGi2Rs0kKsH2PlpNx5PQQt+1ZP9Yzo8OsCM9NPkZEMAFDLeG2eeOCbiHMRAubG/85ts0/MeYbS4ZGcx5fqcLfChjMyECgYB5BFCdDsO0E0hpda+rAuPUDCPVl0Bb/EDCZIuxW6sMfw/lTv5FecO9A0Dy3grXr+ZY2vm98KYtXiVX1loANMfG/s5hsVKC+AvwWet2FVD8tF/qe+N5wLnE6FRudujvcj6Dc7OYlOJhkWcDvttQnbybPDDWCFllMpru0Dyb7irt2Q==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYfyTRJj4sozWHGFJK0LKjiQT681SiiYFktmucFoVS7GcyR9KxWiuP3gHfQL/2qsRyw3mCqC76ZiFqy7GOXiV0IoAXIhkxgCcqDP6xLYw7oxx0OxJfxnNwwyMlLhgfILM59IyjyX0hc6qI76jI371V0Y3BobvCmr/HnqPlVmuOWM3wux3ITH10nIqgfGVZO5w9XK2Xi+8tAm1Ncqafj07YR/urOy4VxbIQjb0OlbI8F02/HYFcG9GNFg+I5v6Pa7Rh/lOf/JDlUMSXCLR6hv2QLb23iZn3zJAPs04YMQsDuqfvRiTx3U5VxVQQqIzhakZYno7EC9dyq6e/beiw6RCwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/p2p/loan/alipayNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/p2p/loan/alipayBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

