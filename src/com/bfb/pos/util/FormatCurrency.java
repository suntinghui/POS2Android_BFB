package com.bfb.pos.util;

/**
 * @author dxp
 *
 */
public class FormatCurrency {
	
	/**
	 * 支持任何格式的金额（如：,##,,#,#.##）转换成带千分位的金额格式
	 * 输入格式如果不是金额格式，那么原值返回
	 * @param amount	：待转换金额
	 * @return			：带千分位金额
	 */
	public static String formatAmount(String amount) {
		String ret = amount;
		if (null == amount || 0 == amount.trim().length()) {
			return "0.00";
		}
		ret = renderAmount(amount, ',');
		String prePart      = ""; // 符号部分
		if (-1 != ret.indexOf('-')) {
			prePart = "-";
			ret = renderAmount(amount, '-');
		}
		try {
			Double.valueOf(ret);
		} catch (Exception e) { // 错误格式，原值返回
			return amount;
		}
		String integerPart  = ""; // 整数部分
		String fractionPart = ""; // 小数部分
		
		if (-1 != ret.indexOf('.')) { // 带小数点
			integerPart  = Long.parseLong("0"+ret.substring(0, ret.indexOf('.')))+""; // format 00## to ##
			fractionPart = ret.substring(ret.indexOf('.')+1)+"0";
		} else {
			integerPart = ret;
		}		
		return prePart + parseAmount(integerPart, ",") + "." + parseNumber(fractionPart, "0", 2);
	}
	/**
	 * 将带千分位的金额类型转换成标准double类型字符串，同@formatAmount(String amount)配合使用
	 * 本方法在@formatAmount(String amount)后使用
	 * @param amount	：带千分位的金额格式
	 * @return			：标准double类型的字符串
	 */
	private static String renderAmount(String amount, char flag) {
		String ret = amount;
		while(-1 != ret.indexOf(flag)) { // 可使用replaceAll
			ret = ret.substring(0, ret.indexOf(flag)) + ret.substring(ret.indexOf(flag)+1); // 为支持J2ME，兼容,##,,#,#格式
		}
		return ret;
	}
	/**
	 * 解析金额型数值，只支持将正整数解析成带千分位金额格式
	 * @param inStr	：待转换值
	 * @param split	：分隔符，如：,
	 * @return		：已解析的待千分位的金额格式
	 */
	private static String parseAmount(String inStr, String split) {
		String ret = inStr;
		for(int i= inStr.length() - 3; i> 0; i-= 3) {
			ret = ret.substring(0, i) + split + ret.substring(i);
		}
		return ret;
	}
	/**
	 * 本方法支持将任何一字符串，自动按照给定的长度使用默认值右补齐
	 * 如果输入值长度大于给定的长度len，那么自动截取给定长度的字符串
	 * @param inStr					：待处理字符串
	 * @param appendDefaultValue	：默认补齐值
	 * @param len					：期望长度
	 * @return						：已处理结果
	 */
	private static String parseNumber(String inStr, String appendDefaultValue, int len) {
		StringBuffer ret = new StringBuffer();
		if (len < inStr.trim().length()) {
			return inStr.trim().substring(0, len);
		}
		ret.append(inStr);
		for (int i=0; i<len-inStr.length(); i++) {
			ret.append(appendDefaultValue);
		}
		return ret.toString();
	}
}
