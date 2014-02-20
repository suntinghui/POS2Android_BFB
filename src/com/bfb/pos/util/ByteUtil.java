package com.bfb.pos.util;


public class ByteUtil {

	public static byte[] cancat(byte[] a, byte[] b) {
		int alen = a.length;
		int blen = b.length;
		byte[] result = new byte[alen + blen];
		System.arraycopy(a, 0, result, 0, alen);
		System.arraycopy(b, 0, result, alen, blen);
		return result;
	}

	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 0] & 0xff) << 24)
				| ((bb[index + 1] & 0xff) << 16)
				| ((bb[index + 2] & 0xff) << 8) | ((bb[index + 3] & 0xff) << 0)));
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
		for (int i = (b.length - 1); i >=0; i--) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}
	
	
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
	
	 //转换成十六进制字符串
    public static String byte2hex(byte[] b) {
        String hs="";
        String stmp="";

        for (int n=0;n<b.length;n++) {
            stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
//            if (n<b.length-1)  hs=hs;
        }
        return hs.toUpperCase();
    }
    
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍   
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
          int intTmp = arrB[i];
          // 把负数转换为正数   
          while (intTmp < 0) {
            intTmp = intTmp + 256;
          }
          // 小于0F的数需要在前面补0   
          if (intTmp < 16) {
            sb.append("0");
          }
          sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
      }
}
