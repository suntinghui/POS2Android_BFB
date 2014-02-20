package com.bfb.pos.dynamic.component;

public class ViewRuntimeException extends IllegalStateException {
	private static final long serialVersionUID = 1L;
	
	private String errorMessage;

	/**
	 * @param detailMessage
	 */
	public ViewRuntimeException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
}
