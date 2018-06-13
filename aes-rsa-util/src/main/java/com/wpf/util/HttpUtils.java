package com.wpf.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

/**
 * 和HTTP相关的操作
 * 
 * @author
 * 
 */
public class HttpUtils {

	private static final Logger logger = Logger.getLogger(HttpUtils.class);
	private final static MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
	// 支持重复连接
	private static HttpClient client = new HttpClient(manager);

	/**
	 * 
	 * @Title: doPost
	 * @Description: post请求
	 * @param reqUrl
	 * @param parameters
	 * @return String
	 */
	public static String doPost(String reqUrl, Map<String, String> parameters, String token) {
		HttpURLConnection urlConn = null;
		try {
			urlConn = _sendPost(reqUrl, parameters, token);
			String responseContent = _getContent(urlConn);
			return responseContent.trim();
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	/**
	 * 
	 * @Title: doUploadFile
	 * @Description: 从网络上下载文件
	 * @param reqUrl
	 * @param parameters
	 * @param fileParamName
	 * @param filename
	 * @param contentType
	 * @param data
	 * @return String
	 */
	public static String doUploadFile(String reqUrl, Map<String, String> parameters, String fileParamName,
			String filename, String contentType, byte[] data) {
		HttpURLConnection urlConn = null;
		try {
			urlConn = _sendFormdata(reqUrl, parameters, fileParamName, filename, contentType, data);
			String responseContent = new String(_getBytes(urlConn));
			return responseContent.trim();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	private static HttpURLConnection _sendFormdata(String reqUrl, Map<String, String> parameters, String fileParamName,
			String filename, String contentType, byte[] data) {
		HttpURLConnection urlConn = null;
		try {
			URL url = new URL(reqUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(5000);// （单位：毫秒）jdk
			urlConn.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			urlConn.setDoOutput(true);

			urlConn.setRequestProperty("Connection", "keep-alive");

			String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			boundary = "--" + boundary;
			StringBuffer params = new StringBuffer();
			if (parameters != null) {
				for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
					String name = iter.next();
					String value = parameters.get(name);
					params.append(boundary + "\r\n");
					params.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
					// params.append(URLEncoder.encode(value, "UTF-8"));
					params.append(value);
					params.append("\r\n");
				}
			}

			StringBuilder sb = new StringBuilder();
			// sb.append("\r\n");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + fileParamName + "\"; filename=\"" + filename
					+ "\"\r\n");
			sb.append("Content-Type: " + contentType + "\r\n\r\n");
			byte[] fileDiv = sb.toString().getBytes();
			byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes();
			byte[] ps = params.toString().getBytes();

			OutputStream os = urlConn.getOutputStream();
			os.write(ps);
			os.write(fileDiv);
			os.write(data);
			os.write(endData);

			os.flush();
			os.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return urlConn;
	}

	private static String _getContent(HttpURLConnection urlConn) {
		try {
			String responseContent = null;
			InputStream in = urlConn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			responseContent = tempStr.toString();
			rd.close();
			in.close();
			return responseContent;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static byte[] _getBytes(HttpURLConnection urlConn) {
		try {
			InputStream in = urlConn.getInputStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for (int i = 0; (i = in.read(buf)) > 0;)
				os.write(buf, 0, i);
			in.close();
			return os.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static HttpURLConnection _sendPost(String reqUrl, Map<String, String> parameters, String token) {
		HttpURLConnection urlConn = null;
		try {
			StringBuffer params = new StringBuffer();
			if (parameters != null) {
				for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
					String name = iter.next();
					String value = parameters.get(name);
					params.append(name + "=");
					params.append(URLEncoder.encode(value, "UTF-8"));
					if (iter.hasNext())
						params.append("&");
				}
			}

			URL url = new URL(reqUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			
			if(StringUtils.isNotBlank(token)) {
				urlConn.addRequestProperty("token", token);
			}
			
			urlConn.setRequestMethod("POST");
			//urlConn.setConnectTimeout(5000);// （单位：毫秒）jdk
			//urlConn.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			urlConn.setDoOutput(true);
			byte[] b = params.toString().getBytes();
			urlConn.getOutputStream().write(b, 0, b.length);
			urlConn.getOutputStream().flush();
			urlConn.getOutputStream().close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return urlConn;
	}

	/**
	 * 发送GET请求
	 * 
	 * @param link
	 * @param charset
	 * @return
	 */
	public static String doGet(String link, String charset, String token) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(link);
			conn = (HttpURLConnection) url.openConnection();
			
			
			conn.setRequestMethod("GET");
			
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			if(StringUtils.isNotEmpty(token)) {
				conn.addRequestProperty("token", token);
			}
			
			BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for (int i = 0; (i = in.read(buf)) > 0;) {
				out.write(buf, 0, i);
			}
			out.flush();
			out.close();
			String s = new String(out.toByteArray(), charset);
			return s;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * UTF-8编码
	 * 
	 * @param link
	 * @return
	 */
	public static String doGet(String link, String token) {
		return doGet(link, "UTF-8", token);
	}

	/**
	 * 
	 * @Title: getIntResponse
	 * @Description: 发送GET请求
	 * @param link
	 * @return int
	 */
	public static int getIntResponse(String link, String token) {
		String str = doGet(link, token);
		return Integer.parseInt(str.trim());
	}

	public static long ip2Long(String strIP) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIP.indexOf(".");
		int position2 = strIP.indexOf(".", position1 + 1);
		int position3 = strIP.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIP.substring(0, position1));
		ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIP.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	// 将10进制整数形式转换成127.0.0.1形式的IP地址
	public static String long2IP(long longIP) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf(longIP >>> 24));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}

	/**
	 * 
	 * urlParse:url解析. <br/>
	 * 
	 * @author majun
	 * @param arrList
	 * @param url
	 * @return
	 * @since JDK 1.6
	 */
	public static Map<String, String> urlParse(List<String> arrList, String url) {

		Map<String, String> vaulesMap = new HashMap<String, String>();
		for (String s : arrList) {
			Pattern pattern = Pattern.compile(s + "=([^&]*)(&|$)");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				String[] arr = matcher.group(1).split("'");
				vaulesMap.put(s, arr[1]);
			}
		}
		return vaulesMap;

	}

	/***
	 * 
	 * http_doPost: httpClient发送post 请求. <br/>
	 *
	 * @author majun
	 * @version 创建时间：2016年6月22日 下午6:15:59
	 * @since JDK 1.6
	 */
	public static String http_doPost(String reqUrl, Map<String, String> parameters, String userToken) {

		try {

			// MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
			// 支持重复连接
			// HttpClient client = new HttpClient(manager);

			PostMethod post = new PostMethod(reqUrl);
			post.setRequestHeader("Connection", "keep-alive");
			post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

			NameValuePair[] params = new NameValuePair[parameters.size()];
			Set<String> keys = parameters.keySet();
			int index = 0;
			for (String key : keys) {

				params[index] = new NameValuePair(key, parameters.get(key));
				index++;
			}

			post.setQueryString(params);

			if (StringUtils.isNotBlank(userToken)) {
				post.setRequestHeader("userToken", userToken);
			}

			Integer status = client.executeMethod(post);
			logger.info("loginStatus:" + status);

			String body = post.getResponseBodyAsString();

			return body;

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 
	 * http_doGet: httpClient发送get请求. <br/>
	 *
	 * @author majun
	 * @version 创建时间：2016年6月22日 下午6:25:10
	 * @since JDK 1.6
	 */
	public static String http_doGet(String reqUrl, Map<String, String> parameters, String userToken) {

		try {

			// MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
			// 支持重复连接
			// HttpClient client = new HttpClient(manager);
			GetMethod get = new GetMethod(reqUrl);

			NameValuePair[] params = new NameValuePair[parameters.size()];
			Set<String> keys = parameters.keySet();
			int index = 0;
			for (String key : keys) {

				params[index] = new NameValuePair(key, parameters.get(key));
				index++;
			}

			get.setQueryString(params);

			if (StringUtils.isNotBlank(userToken)) {
				get.setRequestHeader("userToken", userToken);
			}

			Integer status = client.executeMethod(get);
			logger.info("http_doGet==>Status:" + status);
			String body = get.getResponseBodyAsString();
			return body;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
