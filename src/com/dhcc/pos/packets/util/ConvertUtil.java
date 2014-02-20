package com.dhcc.pos.packets.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConvertUtil {
	static final byte[] HEX = new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	public static final String nLine = "----------------------------------------------------------------------------";

	/**
	 * 文件内容读取到字节数组中
	 * 
	 * @param filePath
	 * @return 字节数组
	 */
	public static byte[] file2byte(String filePath) {
		FileInputStream is;
		byte[] tmpBuf = null;
		try {
			is = new FileInputStream(filePath);
			if (true) {
				int count = 0;
				while (count == 0) {
					count = is.available();
				}
				tmpBuf = new byte[count];
				is.read(tmpBuf);
			} else if (false) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b = 0;
				while ((b = is.read()) != -1)
					baos.write(b);
				return baos.toByteArray();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmpBuf;
	}

	/**
	 * 把一个字节数组的串格式化成十六进制形式 格式化后的样式如下 <blockquote>
	 * --------------------------------
	 * -------------------------------------------- 000: 00 38 60 00 12 00 00 60
	 * 22 10 00 00 00 08 00 00 .8`...` ".....:016 016: 20 00 00 00 C0 00 10
	 * 16 83 74 30 30 30 32 31 36 ...�. �t000216:032 032: 37 38 31 30 35 33 36
	 * 30 31 37 30 31 31 30 31 35 78105360 17011015:048 048: 39 00 14 00 00 00
	 * 02 00 10 00 9..... . :064
	 * ----------------------------------------------
	 * ------------------------------ </blockquote>
	 * 
	 * @param inBytes
	 *            需要格式化的字节数组
	 * @return 格式化后的串，其内容如上。可以直接输出
	 */
	public static String trace(byte[] inBytes) {
		int i, j = 0;
		/** 每行字节数 */
		byte[] temp = new byte[76];

		bytesSet(temp, ' ');
		StringBuffer strc = new StringBuffer("");
		strc.append(nLine + "\n");
		for (i = 0; i < inBytes.length; i++) {
			if (j == 0) {
				/** 打印出来的前四位 000: */
				System.arraycopy(String.format("%03d: ", i).getBytes(), 0, temp, 0, 5);

				/** 打印出来的后四位 :015 */
				System.arraycopy(String.format(":%03d", i + 16).getBytes(), 0, temp, 72, 4);
			}

			System.arraycopy(String.format("%02X ", inBytes[i]).getBytes(), 0, temp, j * 3 + 5 + (j > 7 ? 1 : 0), 3);
			if (inBytes[i] == 0x00) {
				temp[j + 55 + ((j > 7 ? 1 : 0))] = '.';
			} else {
				temp[j + 55 + ((j > 7 ? 1 : 0))] = inBytes[i];
			}
			j++;
			/** 当j为16时换行，j重置为0 每行显示为16进制的16个字节 */
			if (j == 16) {
				strc.append(new String(temp)).append("\n");
				bytesSet(temp, ' ');
				j = 0;
			}
		}
		if (j != 0) {
			strc.append(new String(temp)).append("\n");
			bytesSet(temp, ' ');
		}
		strc.append(nLine + "\n");
		// System.out.println(strc.toString());
		return strc.toString();
	}

	/**
	 * 
	 * 将十六进制A--F转换成对应数
	 * 
	 * @param ch
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */

	public static int getIntByChar(char ch) throws Exception {

		char t = Character.toUpperCase(ch);

		int i = 0;

		switch (t) {

		case '0':

		case '1':

		case '2':

		case '3':

		case '4':

		case '5':

		case '6':

		case '7':

		case '8':

		case '9':

			i = Integer.parseInt(Character.toString(t));

			break;

		case 'A':

			i = 10;

			break;

		case 'B':

			i = 11;

			break;

		case 'C':

			i = 12;

			break;

		case 'D':

			i = 13;

			break;

		case 'E':

			i = 14;

			break;

		case 'F':

			i = 15;

			break;

		default:

			throw new Exception("getIntByChar was wrong");

		}

		return i;

	}

	/**
	 * 
	 * 将字符串转换成二进制数组
	 * 
	 * @param source
	 *            : 16字节
	 * 
	 * @return
	 */

	public static int[] str2Binary(String source) {

		int len = source.length();

		int[] dest = new int[len * 4];

		char[] arr = source.toCharArray();

		for (int i = 0; i < len; i++) {

			int t = 0;

			try {

				t = getIntByChar(arr[i]);

				// System.out.println(arr[i]);

			} catch (Exception e) {

				e.printStackTrace();

			}

			String[] str = Integer.toBinaryString(t).split("");

			int k = i * 4 + 3;

			for (int j = str.length - 1; j > 0; j--) {

				dest[k] = Integer.parseInt(str[j]);

				k--;

			}

		}

		return dest;

	}

	/**
	 * 
	 * 返回x的y次方
	 * 
	 * @param x
	 * 
	 * @param y
	 * 
	 * @return
	 */

	public static int getXY(int x, int y) {

		int temp = x;

		if (y == 0)
			x = 1;

		for (int i = 2; i <= y; i++) {

			x *= temp;

		}

		return x;

	}

	/**
	 * 
	 * s�?位长度的二进制字符串
	 * 
	 * @param s
	 * 
	 * @return
	 */

	public static String binary2Hex(String s) {

		int len = s.length();

		int result = 0;

		int k = 0;

		if (len > 4)
			return null;

		for (int i = len; i > 0; i--) {

			result += Integer.parseInt(s.substring(i - 1, i)) * getXY(2, k);

			k++;

		}

		switch (result) {

		case 0:

		case 1:

		case 2:

		case 3:

		case 4:

		case 5:

		case 6:

		case 7:

		case 8:

		case 9:

			return "" + result;

		case 10:

			return "A";

		case 11:

			return "B";

		case 12:

			return "C";

		case 13:

			return "D";

		case 14:

			return "E";

		case 15:

			return "F";

		default:

			return null;

		}

	}

	/**
	 * 
	 * 将二进制字符串转换成十六进制字符
	 * 
	 * @param s
	 * 
	 * @return
	 */

	public static String binary2ASC(String s) {

		String str = "";

		int ii = 0;

		int len = s.length();

		// 不够4bit左补0

		if (len % 4 != 0) {

			while (ii < 4 - len % 4) {

				s = "0" + s;

			}

		}

		for (int i = 0; i < len / 4; i++) {

			str += binary2Hex(s.substring(i * 4, i * 4 + 4));

		}

		return str;

	}

	public static byte[] cancat(byte[] a, byte[] b) {
		int alen = a.length;
		int blen = b.length;
		byte[] result = new byte[alen + blen];
		System.arraycopy(a, 0, result, 0, alen);
		System.arraycopy(b, 0, result, alen, blen);
		return result;
	}

	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 0] & 0xff) << 24) | ((bb[index + 1] & 0xff) << 16) | ((bb[index + 2] & 0xff) << 8) | ((bb[index + 3] & 0xff) << 0)));
	}

	public static short getShort(byte[] b, int index) {
		return (short) (((b[index] << 8) | b[index + 1] & 0xff));
	}

	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = (b.length - 1); i >= 0; i--) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return "0x" + str;// 0x表示十六进制
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = null;
		String sTemp;

		if (bArray == null || bArray.length <= 0) {
			return null;
		}
		sb = new StringBuffer(bArray.length);
		for (int i = 0; i < bArray.length; i++) {
			int v = 0xFF & bArray[i];
			sTemp = Integer.toHexString(v);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	// java二进制,字节数组,字符,十六进制,BCD编码转换2007-06-07 00:17
	/**
	 * 把16进制字符串转换成字节数组 Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]s
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

	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/** */
	/**
	 * 把字节数组转换为对象
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return o;
	}

	/** */
	/**
	 * 把可序列化对象转换成字节数组
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static final byte[] objectToBytes(Serializable s) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream ot = new ObjectOutputStream(out);
		ot.writeObject(s);
		ot.flush();
		ot.close();
		return out.toByteArray();
	}

	public static final String objectToHexString(Serializable s) throws IOException {
		return bytesToHexString(objectToBytes(s));
	}

	public static final Object hexStringToObject(String hex) throws IOException, ClassNotFoundException {
		return bytesToObject(hexStringToBytes(hex));
	}

	/**
	 * 检查其数据是否能进行BCD
	 * 
	 * @param val
	 *            待检查的数据
	 * @return 都在 0x00 ~ 0x09, 0x30 ~ 0x3F的范围中，则返回true， 否则false
	 */
	private static boolean canbeBCD(byte[] val) {
		for (int i = 0; i < val.length; i++) {
			if (val[i] < 0x00 || val[i] > 0x3F || (val[i] > 0x09 && val[i] < 0x30))
				return false;
		}
		return true;
	}

	/**
	 * 对给定的数据进行BCD装换
	 * 
	 * @param val
	 *            带装换数据，需满足canbeBCD()。
	 * @return
	 */
	public static byte[] byte2BCD(byte[] val) {
		if (val == null) { // 检查参数是否为null
			System.out.println("不能进行BCD, 传入的参数为null");
			return null;
		}
		byte[] ret_val = val;
		if (!canbeBCD(val)) { // 检查参数的内容是否合法
			System.out.println("不能进行BCD, 传入的参数非法：含有 不在[0x00 ~ 0x09], [0x30 ~ 0x3F]的范围中的数据");
			return ret_val;
		}
		if (val.length == 0) // 当参数的内容的长度为0时，不必进行装换
			return null;
		if (val.length % 2 == 0) { // 长度为偶数时
			ret_val = new byte[val.length / 2];
			for (int i = 0; i < ret_val.length; i++) {
				byte temp1 = (byte) (val[i * 2] < 0x30 ? val[i * 2] : val[i * 2] - 0x30);
				byte temp2 = (byte) (val[i * 2 + 1] < 0x30 ? val[i * 2 + 1] : val[i * 2 + 1] - 0x30);
				ret_val[i] = (byte) (((temp1 << 4) & 0xFF) // 前4位
				| ((temp2 & 0x0F) & 0xFF)); // 后4位
			}
		} else { // 长度为奇数时
			ret_val = new byte[val.length / 2 + 1];
			ret_val[0] = (byte) (val[0] & 0x0F);
			for (int i = 1; i < ret_val.length; i++) {
				byte temp1 = (byte) (val[i * 2 - 1] < 0x30 ? val[i * 2 - 1] : val[i * 2 - 1] - 0x30);
				byte temp2 = (byte) (val[i * 2] < 0x30 ? val[i * 2] : val[i * 2] - 0x30);
				ret_val[i] = (byte) (((temp1 << 4) & 0xFF) // 前4位
				| ((temp2 & 0x0F) & 0xFF)); // 后4位
			}
		}
		return ret_val;
	}

	/** */
	/**
	 * 此函数做了特殊处理 默认会删掉左侧的零
	 * 
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @输入参数: BCD码
	 * @输出结果: 10进制串
	 */
	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
	}

	/**
	 * 此函数为原始函数 1个字节转换成2个字符串 2个字节转换为4个字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String Bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString();
	}

	/**
	 * 1个字节转换成2个字符串 2个字节转换为4个字符串 然后删掉右侧零
	 * 
	 * @param bytes
	 * @return
	 */
	public static String Bcd2Str_0(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}

		return temp.toString().substring(0, temp.toString().length() - 1);
	}

	/**
	 * 1个字节转换成2个字符串 2个字节转换为4个字符串 然后删掉左侧零
	 * 
	 * @param bytes
	 * @return
	 */
	public static String _0Bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}

		return temp.toString().substring(1, temp.toString().length());
	}

	/** */
	/**
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @输入参数: BCD码
	 * @输出结果: 10进制串
	 */
	public static String bcd2Str_(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(temp.toString().length() - 1, temp.toString().length()).equalsIgnoreCase("0") ? temp.toString().substring(0, temp.toString().length() - 1) : temp.toString();
	}

	/**
	 * 右靠齐 左补0
	 * */
	public static String _bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	/**
	 * 左靠齐 右补0
	 * 
	 * @函数功能: 10进制串转为BCD码
	 * @输入参数: 10进制串
	 * @输出结果: BCD码
	 */
	public static byte[] str2Bcd_(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = asc + "0";
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	/** */
	/**
	 * 当arc为单数时左补零右靠起，为复数时为原值
	 * 
	 * @函数功能: 10进制串转为BCD码
	 * @输入参数: 10进制串
	 * @输出结果: BCD码
	 */
	public static byte[] _str2Bcd(String arg) {
		int len = arg.length();
		int mod = len % 2;

		if (mod != 0) {
			arg = "0" + arg;
			len = arg.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = arg.getBytes();
		int j, k;

		for (int p = 0; p < arg.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	/** */
	/**
	 * @函数功能: BCD码转ASC码
	 * @输入参数: BCD串
	 * @输出结果: ASC码
	 */
	// public static String BCD2ASC(byte[] bytes) {
	// StringBuffer temp = new StringBuffer(bytes.length * 2);
	//
	// for (int i = 0; i < bytes.length; i++) {
	// int h = ((bytes[i] & 0xf0) >>> 4);
	// int l = (bytes[i] & 0x0f);
	// temp.append(BToA[h]).append( BToA[l]);
	// }
	// return temp.toString() ;
	// }

	/**
	 * MD5加密字符串，返回加密后的16进制字符串
	 * 
	 * @param origin
	 * @return
	 */
	public static String MD5EncodeToHex(String origin) {
		return bytesToHexString(MD5Encode(origin));
	}

	/**
	 * MD5加密字符串，返回加密后的字节数组
	 * 
	 * @param origin
	 * @return
	 */
	public static byte[] MD5Encode(String origin) {
		return MD5Encode(origin.getBytes());
	}

	/**
	 * MD5加密字节数组，返回加密后的字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] MD5Encode(byte[] bytes) {
		MessageDigest md = null;
		try {
			// 创建具有指定算法名称的信息摘要
			md = MessageDigest.getInstance("MD5");
			// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}


	/**
	 * 把fill的值替换整个inBytes 例如：
	 * 
	 * @param inBytes
	 * @param fill
	 */
	private static void bytesSet(byte[] inBytes, char fill) {
		if (inBytes.length == 0) {
			return;
		}
		for (int i = 0; i < inBytes.length; i++) {
			inBytes[i] = (byte) fill;
		}
	}


}
