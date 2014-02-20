package com.dhcc.pos.packets;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import com.dhcc.pos.packets.util.ConvertUtil;

/**
 * *字段域所存储的数据
 * 
 * @author maple
 */
public class cnValue<T> {
	/** 数据类型 */
	private cnType datatype;

	/** 数值 */
	private T value;

	/** 长度 */
	private int length;

	public cnValue(cnType t, T value) {
		if (t.needsLength()) {
			throw new IllegalArgumentException("Fixed-value types must use constructor that specifies length");
		}
		this.datatype = t;
		this.value = value;
		if ((this.datatype == cnType.LLVAR) || (this.datatype == cnType.LLLVAR)) {

			// 获取可变长域可变长度
			// this.length = value.toString().getBytes().length;
			this.length = value.toString().length();

			if ((t == cnType.LLVAR) && (this.length > 99))
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");

			if ((t == cnType.LLLVAR) && (this.length > 999))
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
		} else if ((this.datatype == cnType.LLNVAR) || (this.datatype == cnType.LLLNVAR)) {

			// 获取可变长域可变长度
			this.length = value.toString().getBytes().length;

			if ((t == cnType.LLNVAR) && (this.length > 99))
				throw new IllegalArgumentException("LLNVAR can only hold values up to 99 chars");

			if ((t == cnType.LLLNVAR) && (this.length > 999))
				throw new IllegalArgumentException("LLLNVAR can only hold values up to 999 chars");
		} else {
			this.length = this.datatype.getLength();
		}
	}

	public cnValue(cnType t, T val, int len) {
		this.datatype = t;
		this.value = val;
		this.length = len;
		if ((this.length == 0) && (t.needsLength()))
			throw new IllegalArgumentException("Length must be greater than zero");
		if ((t == cnType.LLVAR) || (t == cnType.LLLVAR)) {
			// 设置变长域的长度
//			this.length = val.toString().getBytes().length;

			if ((t == cnType.LLVAR) && (this.length > 99))
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
			if ((t == cnType.LLLVAR) && (this.length > 999))
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
		} else if ((t == cnType.LLNVAR) || (t == cnType.LLLNVAR)) {
			// 设置变长域的长度
//			this.length = val.toString().getBytes().length;

			if ((t == cnType.LLNVAR) && (this.length > 99))
				throw new IllegalArgumentException("LLNVAR can only hold values up to 99 chars");
			if ((t == cnType.LLLNVAR) && (this.length > 999))
				throw new IllegalArgumentException("LLLNVAR can only hold values up to 999 chars");
		}
	}

	public cnType getType() {
		return this.datatype;
	}

	public int getLength() {
		return this.length;
	}

	public T getValue() {
		return this.value;
	}

	/**
	 * Returns the formatted value as a String. The formatting depends on the
	 * type of the receiver.
	 */
	public String toString() {
		if (value == null) {
			return "ISOValue<null>";
		}
		if (datatype == cnType.NUMERIC || datatype == cnType.AMOUNT) {
			if (datatype == cnType.AMOUNT) {
				// return datatype.format((Double)value ,12);
				return datatype.format((BigDecimal) value, 12);
			} else if (value instanceof Number) {
				return datatype.format(((Number) value).longValue(), length);
			} else {
				return datatype.format(value.toString(), length);
			}
		} else if (datatype == cnType.ALPHA) {
			return datatype.format(value.toString(), length);
		} else if (datatype == cnType.LLLVAR || datatype == cnType.LLLVAR) {
			return value.toString();
		} else if (datatype == cnType.LLNVAR || datatype == cnType.LLLNVAR) {
			return value.toString();
		} else if (value instanceof Date) {
			return datatype.format((Date) value);
		}
		return value.toString();
	}

	/*
	 * public String toString() { throw new Error(
	 * "Unresolved compilation problems: \n\tThe method format(long, int) in the type cnType is not applicable for the arguments (String, int)\n\tThe method format(long, int) in the type cnType is not applicable for the arguments (String, int)\n"
	 * ); }
	 */

	public cnValue<T> clone() {
		return new cnValue(this.datatype, this.value, this.length);
	}

	public boolean equals(Object other) {
		if ((other == null) || !(other instanceof cnValue)) {
			return false;
		}
		cnValue comp = (cnValue) other;
		return (comp.getType() == getType()) && (comp.getValue().equals(getValue())) && (comp.getLength() == getLength());
	}

	public void write(OutputStream outs, boolean binary, Object fieldId) throws IOException {

		if (binary) {
			byte[] buf = (byte[]) null;

			if (this.datatype == cnType.NUMERIC || this.datatype == cnType.LLNVAR || this.datatype == cnType.LLLNVAR) {
				buf = new byte[this.length / 2 + this.length % 2];
			} else if (this.datatype == cnType.AMOUNT) {
				buf = new byte[6];
			} else if ((this.datatype == cnType.DATE10) || (this.datatype == cnType.DATE4) || (this.datatype == cnType.DATE_EXP) || (this.datatype == cnType.TIME)) {
				buf = new byte[this.length / 2];
			}

			if (buf != null && Integer.parseInt(fieldId.toString()) != 62) {
				
				/* 进行BCD码压缩 */
				buf = toBcd(toString(), buf.length, fieldId);

				outs.write(buf);
				/**
				 * 如果buf不为null证明value被bcd码压缩后并写入到outs中，故此return跳出该函数，
				 * 否则下面的write会重复性再写入一遍该值（不经过bcd码压缩的值）
				 */
				return;
			} else {
				outs.write(toString().getBytes());
			}
		}
	}

	/**
	 * @param value
	 * @param buf
	 * @param fieldId
	 */
	private byte[] toBcd(String value, int bufLen, Object fieldId) {
		byte[] buf = new byte[bufLen];
		int field = Integer.valueOf(fieldId.toString());

		/** 当域值为奇数时 需要根据不同域名来指定左靠齐或右靠齐 */
		if (value.length() % 2 == 1) {
			/* 左靠齐右补零 */
			if (field == 2 | field == 22 | field == 35 | field==48 | field == 60) {
				buf = ConvertUtil.str2Bcd_(value);

			} else {
				buf = ConvertUtil._str2Bcd(value);
			}
		} else {
			buf = ConvertUtil._str2Bcd(value);
		}
		return buf;
	}
}