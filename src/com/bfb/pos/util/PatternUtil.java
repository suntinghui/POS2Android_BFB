package com.bfb.pos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {

	public static boolean isValidEmail(String email) {
		String regex = "([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})";
		Pattern pattern = Pattern.compile(regex);
		Matcher mat = pattern.matcher(email);
		return mat.matches();
	}
	
	public static boolean isLegalPassword(String pswd) {
		boolean ret = false;
		String ftmt = "[0-9a-zA-Z_]{5,10}";
		Pattern pattern = Pattern.compile(ftmt);
		Matcher mat = pattern.matcher(pswd);
		ret = mat.matches();
		return ret;
	}
	
	public static boolean isValidIDNum(String idNum){
		String regex = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
		Pattern pattern = Pattern.compile(regex);
		Matcher mat = pattern.matcher(idNum);
		return mat.matches();
	}


}
