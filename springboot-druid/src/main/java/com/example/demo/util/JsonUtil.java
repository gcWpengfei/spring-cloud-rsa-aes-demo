package com.example.demo.util;

public class JsonUtil {
	/***
	 * json字符串转换为对象
	 *
	 * @param param
	 * @param obj
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object paramConvertObj(String param, Class obj) {
		return JsonBinder.buildNonNullBinder().fromJson(param, obj);
	}


	/***
	 * 将json信息发送给前台展现
	 *
	 * @param fangleComments
	 */
	// json/jsonp格式
	public static String returnObjectToJson(Object obj) {

		return JsonBinder.buildNormalBinder().toJson(obj);
	}

	/***
	 * 将json信息发送给前台展现
	 *
	 * @param fangleComments
	 */
	// json/jsonp格式
	public static String returnNonNullObjectToJson(Object obj) {

		return JsonBinder.buildNonNullBinder().toJson(obj);
	}

}
