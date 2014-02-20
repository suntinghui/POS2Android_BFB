package com.bfb.pos.dynamic.regex;

import com.bfb.pos.dynamic.component.ViewException;

/**
 * Java反射工具类，获取任何指定对象，指定方法的值
 * @author:DongXiaoping
 *
 */
public class Reflect {
	public static Object getValue(Object receiver, String methodName) throws ViewException {
		return getValue(receiver, methodName, null);
	}
	public static Object getBeanValue(Object receiver, String methodName) throws ViewException {
		String method = methodName;
		method = "get" + methodName.replaceFirst(methodName.substring(0, 1), methodName.substring(0, 1).toUpperCase()); 
		return getValue(receiver, method);
	}
	public static Object getValue(Object receiver, String methodName, Object... arguments) throws ViewException {
		Class[] args  = null;
		if (null != arguments) {
			args = new Class[arguments.length];
			int i = 0;
			for (Object arg:arguments) {
				args[i++] = arg.getClass();
			}
		}
		try {
			return receiver.getClass().getMethod(methodName, args).invoke(receiver, arguments);
		} catch (Exception e) {
			throw new ViewException("Method["+methodName+"] of object["+receiver+"] can't be invoked!",e);
		}
	}
}
