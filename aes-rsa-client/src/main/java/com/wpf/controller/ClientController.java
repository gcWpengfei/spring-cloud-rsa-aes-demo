package com.wpf.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.wpf.feign.TestFeign;
import com.wpf.interceptor.ParamInterceptor;
import com.wpf.util.AES;
import com.wpf.util.EncryUtil;
import com.wpf.util.RSA;
import com.wpf.util.Req;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class ClientController {

	@Autowired
	private TestFeign testFeign;

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
	
	
	@RequestMapping(value = "/client")
	public Map<String, String> client() throws Exception {
		
		 
		 LOG.info("hello world");

		Req req = testFeign.serverRequest();

		// 验签
		boolean passSign = EncryUtil.checkDecryptAndSign(req.getData(), req.getEncryptkey(), clientPublicKey,
				serverPrivateKey);
		Map<String, String> map = new HashMap<String, String>();
		if (passSign) {
			// 验签通过
			String aeskey = RSA.decrypt(req.getEncryptkey(), serverPrivateKey);
			String data = AES.decryptFromBase64(req.getData(), aeskey);

			JSONObject jsonObj = JSONObject.parseObject(data);
			String userid = jsonObj.getString("userid");
			String phone = jsonObj.getString("phone");

			
			map.put("userId", userid);
			map.put("phone", phone);
			System.out.println("解密后的明文:userid:" + userid + " phone:" + phone);
		} else {
			System.out.println("验签失败");
			
		}
		return map;
	}
	
	@PostMapping("/hello")
	public Map<String, String> hello(HttpServletRequest httpServletRequest,
									 @RequestParam("data") String data, @RequestParam("encryptkey") String encryptkey ) {


		Map<String, String> map = new HashMap<String, String>();
		map.put("data", data);
		map.put("encryptkey", encryptkey);
		return map;
	}

}
