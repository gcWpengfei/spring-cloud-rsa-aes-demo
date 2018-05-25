package com.wpf.filter;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.wpf.conf.AMHttpServletRequestWrapper;
import com.wpf.interceptor.ParamInterceptor;
import com.wpf.util.AES;
import com.wpf.util.EncryUtil;
import com.wpf.util.RSA;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/***
 * @Author:majun
 * @Desciption: 页面过滤器
 * @Date: 13:49 2018/1/15
 * @return
 */
public class RequestFilter implements Filter {
    // 客户端 将原始私钥的转换成PKCS8格式的私钥  123 1111
    public static   final String clientPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM9mfM/BTr2IPPtB" +
            "GyDYo1BkDbdBgfgDhvY+dFB22hF5t5aG1g1KW6CdQog7INM95YI3+vHMdlPvmkd8" +
            "vEjcBlLkd0EWvRIV7wv5upGO8TJ+vYor4EaoR7sx47996pm/TZD5jSWGesRrhTI4" +
            "CkbVGe4l06rfBZ8PRO2XKNpqGR8/AgMBAAECgYAkD5rV+LN7KuwHd7uCy4gh2zOz" +
            "UFWLzPkzaYqJzxB9h19PceRX7TzfQVinHTjI4fM84ATm8/kDAR8fHOYT+QW0JddU" +
            "Fjp+ilFHaXQV9Rjnou/U176sjCrXdNpiPude8MJ68VQezCyE5e/4TD66kvQDRpXb" +
            "AGl5W909rZwjFZXaEQJBAOpalv3r4VulPCl3Oxriz0b/emj1F6njJBCqTc2pQL2g" +
            "lOGhY3L3CyiRjiIBhFlIu6WTf8sp6PRmB8B7VIidHMcCQQDijpGS86LKsUvU8n7u" +
            "1gD//mi8e1deYaIfXQFrYhWJyah084GMGtnN86h8DTwIJiiUfVR59Jvbacr4/M/1" +
            "GUHJAkBF5e0hISifKAJwr7I+S6XdHDgLdAax0iCgo9r+21uG841UWsmJsatvVzKY" +
            "a/Fom+vz77FvDDoCIyhuvZoyAQJjAkEAnv6g6Tl0cL1WU57PN/wV/ZHknQoOeZ0Z" +
            "MtuJiHvwU5+jSlgt/U5GtoOeJVkAXVOyPOtr4p6o1qX7HRwHMaJFCQJBAONv+hwy" +
            "VM7uRIdEqnnx7oMT4/+1XpwXqqZL6Qu69ULw6r8OtCiCRUzvLBr1nquWxhTastQ0" +
            "X5uHQww065oHGwM=";

    // 客户端 公钥
    public static final String clientPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPZnzPwU69iDz7QRsg2KNQZA23" +
            "QYH4A4b2PnRQdtoRebeWhtYNSlugnUKIOyDTPeWCN/rxzHZT75pHfLxI3AZS5HdB" +
            "Fr0SFe8L+bqRjvEyfr2KK+BGqEe7MeO/feqZv02Q+Y0lhnrEa4UyOApG1RnuJdOq" +
            "3wWfD0TtlyjaahkfPwIDAQAB";

    // 服务端 将原始私钥的转换成PKCS8格式的私钥
    public static final String  serverPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIclo1lSxru9S6eg" +
            "YImIZCzSsd5xkMAtKXput7Quw6yhZRbO4j1OFPhNHzBmZmVaqAUvr1C/dWgnmFui" +
            "8NPa4uGz+5zWGWdcfyR0EQvyMamlPBA+6oo2UVLDgtPNKkz2eJy0UwWKN212Qz+H" +
            "Z/vi85X+HjSjgUcJmwYz2h/2pjR3AgMBAAECgYAr0yS5XoJbdvMFlJg5gSUa8+gB" +
            "/km2R+7faO/hWhjR7jRdxRDQWHWsXzXx+ALUcyVxKRls0ek8sTpS3O/Dg4N2t7bh" +
            "bxO+bG7Ife5RSgXFBpN7K6jyt2O3kZDRY1BmajF2lXly3TGLN3ZtVvAXaTRoVt79" +
            "b02Wx3iDT0tonfaHwQJBAPhLOqDNwmxO+IEMwaIiuBaCq4jzE+QQ3e80ubYxW7ly" +
            "vqkNaHeXNxFVjeFKG3NS0d4YQD7U/QmBmqBsuXohH38CQQCLV2w08arQhFZj+UP/" +
            "HwAOgFKm+ucdgm6xzQfHQUseL9N0147TpRL3lHK0XKG7wqs5CebsIQ+vcsR6JXaQ" +
            "MGcJAkAVJOroOL1+1bbJ3pk6wnQkzpnm/rRJ7rnHnhjWkBt8jm34HYEw9fqlikCb" +
            "1+DAkGP44t3Nu/uUbKoLUVb2NI3nAkAse7pFpKj9bGIQBHGarpDcEEdSm2LQ3uTr" +
            "yiKjj8qlVmtRL8ee9WH6u99qiO/w+xKiYPDhjSRuxFrJC9Cv82PRAkEAmHyruGrN" +
            "kDBgscTgtkXgQeAvXtBbo7aAEZQlO4MlYaJlpsYhsgriAjdugUdYjoLo7PMVVRcT" +
            "YxkFhvp0UEtAZQ==";

    // 服务端 公钥
    public static final String  serverPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHJaNZUsa7vUunoGCJiGQs0rHe" +
            "cZDALSl6bre0LsOsoWUWzuI9ThT4TR8wZmZlWqgFL69Qv3VoJ5hbovDT2uLhs/uc" +
            "1hlnXH8kdBEL8jGppTwQPuqKNlFSw4LTzSpM9nictFMFijdtdkM/h2f74vOV/h40" +
            "o4FHCZsGM9of9qY0dwIDAQAB";
    private Logger LOG = Logger.getLogger(ParamInterceptor.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * 过滤器， 将加密的请求参数解密
     *
     * @Author: wpf
     * @Date: 18:15 2018/5/25
     * @Description: 
     * @param  * @param null  
     * @return   
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest http_req = (HttpServletRequest)request;
        AMHttpServletRequestWrapper amHttpServletRequestWrapper = new AMHttpServletRequestWrapper(http_req, http_req.getParameterMap());

        Map<String,String> params = new HashMap<String,String>();
        Map<String, String[]> requestParams = amHttpServletRequestWrapper.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
            params.put(name, valueStr);
        }

        /*
        * 只修改data, encryptkey
         */
        final String data = params.get("data");
        final String encryptkey = params.get("encryptkey");

        // 验签
        boolean passSign = false;
        try {
            passSign = EncryUtil.checkDecryptAndSign(data, encryptkey, clientPublicKey,
                    serverPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        if (passSign) {
            // 验签通过
            String aeskey = null;
            try {
                aeskey = RSA.decrypt(encryptkey, serverPrivateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dataAes = AES.decryptFromBase64(data, aeskey);

            JSONObject jsonObj = JSONObject.parseObject(dataAes);

             /*
             * 直接遍历  无序遍历
             */
            for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                amHttpServletRequestWrapper.setParameter( entry.getKey(), (String)entry.getValue());
            }

        } else {
            System.out.println("验签失败");

        }
        chain.doFilter(amHttpServletRequestWrapper, response);
    }

    @Override
    public void destroy() {

    }

    public static void main(String[] args) {
        final String data = "RQ0J0IPm0pnNaz9IfC0afjQQmYfQD3DVuOuL8ef46eKm9uKx4JHO2z1t2iV4YDl0WDSHaKMaMfPKHRWMNXkiFTARBiTX3dLEaS6qhs5shHbXyBcgPQynPZMypde+dVPU8XoMdgvQ0orDX4WdMNdcOHHD89+x+SGi2x9+vGYxQ+Qs02TiqFaB22K9HPrKRUEDbmy0gePpDTWLGfcGpBvk+RGA+63TmT/NF3U009VTSPWVmrDqhpRhtUvnkIx2HEoJ/EjfoabVxifM0PadN9NHts5BI4xvhW3OCDsJVxhMqY3TxiIMSZKzufQMRfbAhn1s";
        final String encryptkey = "O9s8NljCumMW01lfaeDCEWdCUJ7t6KmvBB6TGAWE9iXCAh4mYhyq9/8y0syDnLIIwwhQEvD0CcJpvryOtjFzMgRyP7/6d3Bj9aLGxezluyyddipBemFYk9AOyjDtjYQ++wMTqJEuzJXoEumNQlSnmLqIGJzS8WHcVbqKPXkqkJI=";

        // 验签
        boolean passSign = false;
        try {
            passSign = EncryUtil.checkDecryptAndSign(data, encryptkey, clientPublicKey,
                    serverPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        if (passSign) {
            // 验签通过
            String aeskey = null;
            try {
                aeskey = RSA.decrypt(encryptkey, serverPrivateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dataAes = AES.decryptFromBase64(data, aeskey);
            JSONObject jsonObj = JSONObject.parseObject(dataAes);

            /*
             * 直接遍历  无序遍历
             */
            for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                map.put( entry.getKey(), (String)entry.getValue());
            }

            /*
             * 转为map后再遍历
             */
            /*Map<String, String> params = JSONObject.parseObject(jsonObj.toJSONString(), new TypeReference<Map<String, String>>(){});
            System.out.println(params);

            for (Map.Entry<String, String> entry : params.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                map.put( entry.getKey(), entry.getValue());
            }*/



             /*
             * 单个取值
             */
            /*String userid = jsonObj.getString("userid");
            String phone = jsonObj.getString("phone");


            map.put("userId", userid);
            map.put("phone", phone);
            System.out.println("解密后的明文:userid:" + userid + " phone:" + phone);*/
        }
    }


}


