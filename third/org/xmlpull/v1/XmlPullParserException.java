package org.xmlpull.v1;

import org.xmlpull.v1.XmlPullParser;

public class XmlPullParserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Throwable detail;
	protected int row = -1;
	protected int column = -1;

	public XmlPullParserException(String s) {
		super(s);
	}

	public XmlPullParserException(String msg, XmlPullParser parser,
			Throwable chain) {
		super(((msg == null) ? "" : new StringBuffer().append(msg).append(" ")
				.toString())
				+ ((parser == null) ? "" : new StringBuffer().append(
						"(position:").append(parser.getPositionDescription())
						.append(") ").toString())
				+ ((chain == null) ? "" : new StringBuffer().append(
						"caused by: ").append(chain).toString()));

		if (parser != null) {
			this.row = parser.getLineNumber();
			this.column = parser.getColumnNumber();
		}
		this.detail = chain;
	}

	public Throwable getDetail() {
		return this.detail;
	}

	public int getLineNumber() {
		return this.row;
	}

	public int getColumnNumber() {
		return this.column;
	}

	public void printStackTrace() {
		if (this.detail == null)
			super.printStackTrace();
		else
			synchronized (System.err) {
				System.err.println(super.getMessage()
						+ "; nested exception is:");
				this.detail.printStackTrace();
			}
	}
}