/**
 * 
 */
package com.bfb.pos.dynamic.component.expression;

import com.bfb.pos.dynamic.component.Component;
import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.parse.ParseView;
import com.bfb.pos.dynamic.regex.Regex;

/**
 * 逻辑表达式标签，用于定义逻辑表达式的功能实现
 * @author:DongXiaoping
 *
 */
public abstract class Logic extends Expression {
	private Object condition1;
	private Object condition2;
	/**
	 * 0:=
	 * 1:>
	 * 2:<
	 * 3:#
	 * others:boolean
	 */
	private int compareType;
		
	public Logic(ViewPage viewPage) {
		super(viewPage);
	}
	public Logic(ViewPage viewPage, String conditionRegex) {
		super(viewPage);
		this.setConditionRegex(conditionRegex);
	}
	
	public Object getCondition1() {
		return condition1;
	}
	public void setCondition1(Object condition1) {
		this.condition1 = condition1;
	}
	public Object getCondition2() {
		return condition2;
	}
	public void setCondition2(Object condition2) {
		this.condition2 = condition2;
	}
	public int getCompareType() {
		return compareType;
	}
	public void setCompareType(int compareType) {
		this.compareType = compareType;
	}
	
	@Override
	protected void copyParams(Component src, Component des) {
		super.copyParams(src, des);
		((Logic) des).setCondition1(((Logic)des).condition1);
		((Logic) des).setCondition2(((Logic)des).condition2);
		((Logic) des).setCompareType(((Logic)des).compareType);
	}
	@Override
	protected void parseCondition() throws ViewException {
		String regex = this.getConditionRegex();
		if (null == regex) {
			throw new ViewException("Component ["+this.getId()+"],The Condition of expression must be entered!");
		} 
		if (!ParseView.isComponentTarget(regex)) {
			throw new ViewException("Component ["+this.getId()+"],The expression is invalidate!");
		}
		String[] conditions = null;
		if (-1 != regex.indexOf("=")) {
			this.compareType = 0;
			conditions = this.parseRegex("=", 2);
		} else if (-1 != regex.indexOf("#")) {
			this.compareType = 3;
			conditions = this.parseRegex("#", 2);
		} else if (-1 != regex.indexOf(">")) {
			this.compareType = 1;
			conditions = this.parseRegex(">", 2);
		} else if (-1 != regex.indexOf("<")) {
			this.compareType = 2;
			conditions = this.parseRegex("<", 2);
		} else {
			this.compareType = 4;
		}
		if (null != conditions) {
			this.condition1  = this.parseCondition(conditions[0]);
			this.condition2  = this.parseCondition(conditions[1]);
		}
	}
	private String[] parseRegex(String split, int lengthCondition) throws ViewException {
		String[] ret = null;
		ret = ParseView.parseComponentTarget(this.getConditionRegex()).split("\\"+split);
		if (ret.length != lengthCondition) {
			throw new ViewException("Component ["+this.getId()+"]The arguments of the logic expression must less than two!");
		}
		return ret;
	}
	private Object parseCondition(String conditionRegex) throws ViewException {
		String tmp = conditionRegex.trim();
		try {
			return Integer.valueOf(tmp);
		} catch(Exception e) {}
		try {
			return Double.valueOf(tmp);
		} catch(Exception e) {}
		if ("true".equalsIgnoreCase(tmp)) {
			return new Boolean(true);
		} else if("false".equalsIgnoreCase(tmp)) {
			return new Boolean(false);
		} else if("null".equalsIgnoreCase(tmp)) {
			return null;
		} else if(tmp.startsWith("\'") && tmp.endsWith("\'")) {
			return tmp.substring(1, tmp.length() - 1); // 字符串
		} else {
			return Regex.getRegexValue(this.getViewPage(), ParseView.toComponentTarget(tmp));
		}
	}
	public boolean compare() throws ViewException {
		this.parseCondition();
		
		if (null == this.getCondition1() && null == this.getCondition2()) {
			return true;
		}
		switch (this.getCompareType()){
			case 3: // #
				if ((null == this.getCondition1() && null != this.getCondition2()) ||
						(null != this.getCondition1() && null == this.getCondition2())) {
					return true;
				}
				return !this.getCondition1().equals(this.getCondition2());
			case 0: // ==
			case 1: // >
			case 2: // <
			default: // others
				if (null == this.getCondition1() || null == this.getCondition2()) {
					return false;
				}
				switch (this.getCompareType()){
					case 0: // ==
						return this.getCondition1().equals(this.getCondition2());
					case 1: // >
						try {
							return Integer.parseInt(this.getCondition1().toString()) > Integer.parseInt(this.getCondition2().toString()); 
						} catch (Exception e){}
						try {
							return Double.parseDouble(this.getCondition1().toString()) > Double.parseDouble(this.getCondition2().toString());
						} catch (Exception e){}
						throw new ViewException("Component["+this.getId()+"], condition type must be Integer or Double");
					case 2: // <
						try {
							return Integer.parseInt(this.getCondition1().toString()) < Integer.parseInt(this.getCondition2().toString()); 
						} catch (Exception e){}
						try {
							return Double.parseDouble(this.getCondition1().toString()) < Double.parseDouble(this.getCondition2().toString());
						} catch (Exception e){}
						throw new ViewException("Component["+this.getId()+"], condition type must be Integer or Double");
					default: // others
						try {
							return Boolean.valueOf(this.getCondition1().toString());
						} catch (Exception e) {
							throw new ViewException("Component["+this.getId()+"], the result of condition["+this.getConditionRegex()+"] must be boolean!");
						}
				}
		}
	}
}
