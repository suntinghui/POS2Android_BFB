package com.bfb.pos.util;

public class ByteOrHexString {
	// 将指定byte数组以16进制的形式打印到控制台
		/**
		 * @param data
		 */
		public static void printHexString(byte[] data) {
			for (int i = 0; i < data.length; i++) {
				String hex = Integer.toHexString(data[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				System.out.print(hex.toUpperCase());
			}
		}
		
	/**
	 * Convert byte[] to hex string. 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param data 需要进行hex的字节数组数据
	 * @return hex String (大写)
	 */
	public static String bytes2HexString(byte[] data) {
		String ret = "";
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * Convert byte[] to hex string. 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param data 需要进行hex的字节数组数据
	 * @return hex string (小写)
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}


}
