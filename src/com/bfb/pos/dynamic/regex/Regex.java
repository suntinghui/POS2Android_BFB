package com.bfb.pos.dynamic.regex;

import java.util.ArrayList;
import java.util.List;

import com.bfb.pos.dynamic.component.ViewException;
/**
 * 表达式支撑工具类，为XML增加标签语法
 * @author：DongXiaoping
 *
 */
public class Regex {
	/**
	 * 获得表达式的参数值，原则上支持的类型包括a.b(c.d(e.f)[0]).g，但实际应用中不建议将表达式书写的过于复杂
	 * 表达式必须包含在“{}”中，使用方式如：getRegexValue("a", "{getClass.getMethods[0]}")；
	 * @param receiver		：带取值的对象
	 * @param methodRegex	：表达式
	 * @return				：对应的值
	 * @throws Exception	：系统异常
	 */
	public static Object getRegexValue(Object receiver, String methodRegex) throws ViewException {
		String methodNs = methodRegex.trim();
		Object re     = receiver;
		if (isComponentTarget(methodNs)) {
			methodNs = parseComponentTarget(methodNs);
			String[] methodNames     = null;
			List<String> methodLists = new ArrayList<String>();
			char[] mns 			     = methodNs.toCharArray();
			int containArgsCount     = 0;
			StringBuffer buffer      = new StringBuffer();
			for (char mn:mns) {
				if ('(' == mn) {
					containArgsCount++;
				} 
				if (')' == mn) {
					containArgsCount--;
				}
				if ('.' == mn && 0 == containArgsCount) {
					methodLists.add(buffer.toString());
					buffer = new StringBuffer();
					continue;
				}
				buffer.append(String.valueOf(mn));
			}
			if (0 != buffer.length()) {
				methodLists.add(buffer.toString());
			}
			methodNames = new String[methodLists.size()];
			for (int i = 0; i <methodLists.size(); i++) {
				methodNames[i] = methodLists.get(i);
			}
			for (String methodNTmp:methodNames) {
				boolean isArray = false;
				int index = 0;
				if (-1 != methodNTmp.indexOf('[') && -1 != methodNTmp.indexOf(']')) {
					isArray = true;
				}
				if (isContainArguments(methodNTmp)) {
					if (isArray) {
						index = Integer.valueOf(methodNTmp.substring(methodNTmp.indexOf('[')+1, methodNTmp.length()-1));
						methodNTmp = methodNTmp.substring(0, methodNTmp.indexOf('['));
					}
					methodNTmp = methodNTmp.substring(0, methodNTmp.length() - 1);
					String[] argNames = methodNTmp.substring(methodNTmp.indexOf("(")+1).split("\\,");
					Object[] args     = new Object[argNames.length];
					methodNTmp = methodNTmp.substring(0, methodNTmp.indexOf("("));
					for (int i= 0;i <argNames.length; i++) {
						if (argNames[i].startsWith("\'") && argNames[i].endsWith("\'")) {
							args[i] = argNames[i].substring(1, argNames[i].length() - 1); // 字符串参数
							continue;
						}
						try {
							args[i] = Integer.valueOf(argNames[i].toString());
							continue;
						} catch (Exception e) {}
						try {
							args[i] = Double.valueOf(argNames[i].toString());
							continue;
						} catch (Exception e) {}
						if (0 != argNames[i].indexOf("\\.")) {
							args[i] = getRegexValue(re, argNames[i]);
						}
					} 
					re = Reflect.getValue(re, methodNTmp, args);
				} else {
					if (isArray) {
						index = Integer.valueOf(methodNTmp.substring(methodNTmp.indexOf('[')+1, methodNTmp.length()-1));
						methodNTmp = methodNTmp.substring(0, methodNTmp.indexOf('['));
					}
					re = Reflect.getBeanValue(re, methodNTmp);
				}
				if (isArray) {
					re = ((Object[])re)[index];
				}
			}
		}
		return re;
	}
	
	public static boolean isComponentTarget(String object) {
		return null != object && object.toString().startsWith("{") && object.toString().endsWith("}");
	}
	
	public static String parseComponentTarget(String object) {
		return object.toString().substring(1, object.toString().length()-1);
	}
	public static boolean isContainArguments(String object) {
		return null != object && 0 != object.toString().indexOf("(") && object.toString().endsWith(")");
	}
	public static String[] split(String str) {
		return str.split("\\.");
	}
}
