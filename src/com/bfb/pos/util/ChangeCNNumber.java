package com.bfb.pos.util;

/**
 * 本类实现将普通阿拉伯数字转换成标准中国普通话发音的数字
 * @author dxp
 *
 */
public class ChangeCNNumber {
	private static String digit_CN = "零壹贰叁肆伍陆柒捌玖";
	private static String digit_EN = "0123456789";
	private static String base_CN  = "个拾佰仟";
	private static String base_EN  = "abcd";
	private static String times_CN = "个万亿兆";
	private static String times_EN = "aefg";
	private String unit_CN  = "元角分";
	private String unit_EN  = "xyz";
	/**
	 * 将数字格式的文本转成中文发音的数字，格式支持：#,###.##或###.##
	 * @param number	：整数部分最大支持16位
	 * @return			：中文数字
	 */
	public static String changeNumber(String number) {
		return changeNumber(number, false);
	}
	/**
	 * 将数字格式的文本转成中文发音的数字，格式支持：#,###.##或###.##
	 * 通过isRetPictureData的设置，支持直接翻译成图片名称或中文
	 * 图片名称命名：零壹贰叁肆伍陆柒捌玖 个拾佰仟万亿兆 元角分
	 * 对应于：0123456789 abcdefg xyz
	 * @param number			：整数部分最大支持16位
	 * @param isRetPictureData	：true，返回图片名称；false，返回中文
	 * @return					：中文数字
	 */
	public static String changeNumber(String number, boolean isRetPictureData) {
		String ret = null;
//		String errorCode = "NA";
		String errorCode = "无效输入";
		if (null == number || 0 == number.trim().length()) {
			return errorCode;
		}
		while(-1 != number.indexOf(",")) { // 可使用replaceAll
			number = number.substring(0, number.indexOf(",")) + number.substring(number.indexOf(",")+1); // 为支持J2ME，兼容,##,,#,#格式
		}
		try {
			Double.valueOf(number);
		} catch (Exception e) { // 非法字符
			return errorCode;
		}
		String fractionPart = ""; // 小数部分
		String strAmt  = ""; // 元
		String strAmt1 = ""; // 角
		String strAmt2 = ""; // 分
		if (-1 != number.indexOf('.')) { // 带小数点
			fractionPart = number.substring(number.indexOf('.')+1);
			number = number.substring(0, number.indexOf('.'));
			
			fractionPart += 2 > fractionPart.length() ? "00" : ""; // 容错补齐小数点后面一位的情况
			
			String jiao = fractionPart.substring(0,1);
			String cent = fractionPart.substring(1,2);
			if(jiao.equals("0") && number.equals("0")) {
	            strAmt1 = "";
	        } else if(jiao.equals("0") && !cent.equals("0")) {
	            strAmt1 = isRetPictureData? "0y": "零角";
	        } else {
	        	strAmt1 = changeDigitToHanzi(jiao, isRetPictureData? times_EN: times_CN, errorCode, isRetPictureData)
	        				+(isRetPictureData? "y": "角");
	        }
			
			if(cent.equals("0")) {
	            if (jiao.equals("0")) {
    	            strAmt1 = "";
            	}
            } else {
                strAmt2 = changeDigitToHanzi(cent, isRetPictureData? times_EN: times_CN, errorCode, isRetPictureData)
                			+(isRetPictureData? "z": "分");
            }
		}
		if(number.equals("0")) {
			strAmt = isRetPictureData? "0x": "零元";
		} else {
			strAmt = changeDigitToHanzi(number, isRetPictureData? times_EN: times_CN, errorCode, isRetPictureData) 
						+ (isRetPictureData? "x": "元");
		}
		ret = strAmt + strAmt1 + strAmt2;
		return ret;
	}
	/**
	 * 主方法，转换各种正确的"正整数"为标准中国普通话发音的数字
	 * @param inString	:正整数
	 * @param times	  	：阶数(如: "个万亿兆...")
	 * @param errorCode ：错误提示
	 */
	static String changeDigitToHanzi(String inString, String times, String errorCode, boolean isRetPictureData) {
	    /** 变量定义 **/
	    String base,digit;                           // times: 阶数("个万亿兆...")

	    base=isRetPictureData? base_EN: base_CN;                      // base: 位数
	    digit=isRetPictureData? digit_EN: digit_CN;         // digit: 汉字数码串(大写)

	    int sLen,b,t,bLen;
	 
	    sLen=inString.length();                     // 输入字符串的长度
	 
	    bLen=base.length();                         // 位数的长度
	 
	    if(sLen>bLen*times.length() || sLen<1)      // 溢出归零
	        return errorCode;
	 
	    b=(sLen-1)%bLen;                          // 当前数码在base中的位置
	 
	    t=(sLen-1)/bLen;              // 当前数码在times中的位置
	 
	    int i,at;
	    String zero;
	    
	    i=0;                                      // at某个位上的数码, i循环计数
	    zero="";                                  // 保存数字中的0值
	 
	    String OUT = "";                                   // 输出汉字数码
	    
	    /** 开始 **/
	    at=inString.charAt(i)-48;             // 处理"一十二"为"十二", 此时i=0
	 
	    if(at==1 && b==1) {
	        OUT+=base.charAt(b--);
	        i+=1;                                 // 此时, i=1
	    }
	 
	    while(i<sLen) {
	        at=inString.charAt(i++)-48;
	        if(b!=0) {
	            if(at!=0) {
	                OUT+=zero;
	                zero="";
	                OUT+=digit.charAt(at);
	                OUT+=base.charAt(b);
	            } else { 
	                zero=digit.charAt(0)+"";         // 此时, zero="零"或"〇"
	            }
	            b--;
	        } else {
	            if(at!=0) {
	                OUT+=zero;
	                OUT+=digit.charAt(at);
	            }
	            zero="";
	            if(t!=0) OUT+=times.charAt(t--);
	            b=bLen-1;
	        }
	    }
	    return OUT;
	}
}
