/**
 * 
 */
package com.bfb.pos.dynamic.component;

/**
 * @author DongXiaoping
 *
 */
public class ViewException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String errorMessage;

	/**
	 * @param detailMessage
	 */
	public ViewException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
	/**
	 * @param throwable
	 */
	public ViewException(Throwable throwable) {
		super(throwable);
		this.errorMessage = throwable.getMessage();
	}
	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public ViewException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		this.errorMessage = detailMessage;
	}
	public String getErrorMessage() {
		return this.errorMessage;
	}
}
