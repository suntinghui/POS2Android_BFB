package com.bfb.pos.util;

import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.dhcc.pos.packets.util.ConvertUtil;


 
/**
 * 本程序主要是des及3des的加解密
 * UnionDesEncrypt及UnionDesDecrypt为des加解密
 * Union3DesEncrypt及Union3DesDecrypt为3des加解密
 */
public class UnionDes {
 
  static String DES = "DES/ECB/NoPadding";  
    static String TriDes = "DESede/ECB/NoPadding";  
    
    /**
      * 十六进制字符串转二进制
      * @param str 十六进制串
      * @return
      */
     public static byte[] hex2byte(String str) { //字符串转二进制
         int len = str.length();
         String stmp = null;
         byte bt[] = new byte[len / 2];
         for (int n = 0; n < len / 2; n++) {
             stmp = str.substring(n * 2, n * 2 + 2);
             bt[n] = (byte) (java.lang.Integer.parseInt(stmp, 16));
         }
         return bt;
     }
     
    /**
      * 二进制转十六进制字符串
      * @param b
      * @return
      */
     public static String byte2hex(byte[] b) { //二行制转字符串
         String hs = "";
         String stmp = "";
         for (int n = 0; n < b.length; n++) {
             stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
             if (stmp.length() == 1) {
                 hs = hs + "0" + stmp;
             } else {
                 hs = hs + stmp;
             }
             if (n < b.length - 1) {
                 hs = hs + "";
             }
         }
         return hs.toUpperCase();
     }
     
    /**
      * des加密
      * @param key 密钥
      * @param data 明文数据 16进制且长度为16的整数倍
      * @return  密文数据
      */
     public static byte[] desEncrypt(byte key[], byte data[]) {  
  
        try {  
            KeySpec ks = new DESKeySpec(key);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(DES);  
            c.init(Cipher.ENCRYPT_MODE, ky);  
            return c.doFinal(data);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    /**
      * des解密
      * @param key 密钥
      * @param data 密文数据 16进制且长度为16的整数倍
      * @return 明文数据
      */
     public static byte[] desDecrypt(byte key[], byte data[]) {  
  
        try {  
            KeySpec ks = new DESKeySpec(key);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(DES);  
            c.init(Cipher.DECRYPT_MODE, ky);  
            return c.doFinal(data);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
     
     public static byte[] TripleDES(byte[] keyByte, byte[] textByte){
    	 try{
    		 SecretKey keySpec = new SecretKeySpec(keyByte, "DESede");
             Cipher e_cipher = Cipher.getInstance("DESede/ECB/NoPadding", "BC");
             e_cipher.init(Cipher.ENCRYPT_MODE, keySpec);
             byte [] cipherText = e_cipher.doFinal(textByte);
             
             return cipherText;
             
    	 } catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 
    	 return null;
     }
    
    /**
      * 3des加密
      * @param key 密钥
      * @param data 明文数据 16进制且长度为16的整数倍
      * @return  密文数据
      */
     public static byte[] thirdDesEncrypt(byte key[], byte data[]) {  
        try {  
            byte[] k = new byte[24];  
  
            int len = data.length;  
            if(data.length % 8 != 0){  
                len = data.length - data.length % 8 + 8;  
            }  
            byte [] needData = null;  
            if(len != 0)  
                needData = new byte[len];  
              
            for(int i = 0 ; i< len ; i++){  
                needData[i] = 0x00;  
            }  
              
            System.arraycopy(data, 0, needData, 0, data.length);  
              
            if (key.length == 16) {  
                System.arraycopy(key, 0, k, 0, key.length);  
                System.arraycopy(key, 0, k, 16, 8);  
            } else {  
                System.arraycopy(key, 0, k, 0, 24);  
            }  
  
            KeySpec ks = new DESedeKeySpec(k);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(TriDes);  
            c.init(Cipher.ENCRYPT_MODE, ky);  
            return c.doFinal(needData);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
  
    }  
    
    /**
      * 3des解密
      * @param key 密钥
      * @param data 密文数据 16进制且长度为16的整数倍
      * @return   明文数据
      */
     public static byte[] thirdDesDecrypt(byte key[], byte data[]) {  
        try {  
            byte[] k = new byte[24];  
  
            int len = data.length;  
            if(data.length % 8 != 0){  
                len = data.length - data.length % 8 + 8;  
            }  
            byte [] needData = null;  
            if(len != 0)  
                needData = new byte[len];  
              
            for(int i = 0 ; i< len ; i++){  
                needData[i] = 0x00;  
            }  
              
            System.arraycopy(data, 0, needData, 0, data.length);  
              
            if (key.length == 16) {  
                System.arraycopy(key, 0, k, 0, key.length);  
                System.arraycopy(key, 0, k, 16, 8);  
            } else {  
                System.arraycopy(key, 0, k, 0, 24);  
            }  
            KeySpec ks = new DESedeKeySpec(k);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(TriDes);  
            c.init(Cipher.DECRYPT_MODE, ky);  
            return c.doFinal(needData);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
  
    }  
     
     /**
 	 * 密钥
 	 * 
 	 * @param src
 	 * @param key
 	 * @return
 	 */
 	public static byte[] des(byte[] src, Key key) {
 		byte[] plainText = src;
 		byte[] cipherText = null;
 		Cipher cipher;
 		try {
 			// 很关键的一个地方API或算法本身不对数据进行处理，加密数据由加密双方约定填补算法。
 			cipher = Cipher.getInstance("DES/ECB/NoPadding");
 			cipher.init(Cipher.ENCRYPT_MODE, key);
 			cipherText = cipher.doFinal(plainText);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return cipherText;
 	}
 	
 	
 	
 	
 	/*======*/
 	
 	/**异或并且计算mac值
	 * @param data 需要进行mac计算的源数据
	 * @param macKey 
	 * @return macValue 8字节mac值
	 */
	public static byte[] xorDataAndMac(byte[] data, Key macKey){
		System.out.println("################### MAC计算 ###################");
		System.out.println("接收到的数据:" + ConvertUtil.trace(data));
		/*异或运算的结果集*/
		byte[] xorAfterData = new byte[8];
		
		byte[] macValue = null;
		
		xorAfterData = xorData(data);
		
		try {
			macValue = xorAfterDataToMac(xorAfterData, macKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return macValue;
		
	}


/**
	 * 根据异或后的值计算mac
	 * 
	 * @param xorAfterData
	 *            异或之后的8字节数组
	 * @param key
	 *            加密秘钥
	 * @return 返回MAC值（8字节）
	 * @throws Exception 
	 */
	public static byte[] xorAfterDataToMac(byte[] xorAfterData, Key maKey) throws Exception {
		byte[] b1 = new byte[8];
		byte[] b2 = new byte[8];

		System.out.println("【异或之后的8字节数组】:" + ConvertUtil.trace(xorAfterData));
//		System.out.println("【异或之后的8字节数组】:" + ByteOrHexString.bytesToHexString(xorAfterData));
		
		// 4：将异或运算后的最后8个字节结果转换成HEX
//		String strHex = byteToHex(xorAfterData);
		
		String strHex = ByteOrHexString.bytes2HexString(xorAfterData);
		System.out.println("异或后的最后8个字节转换为HEX ==》【" + strHex + "】");

		
		System.out.println("【异或后的数据HEX之后的值ASCII码显示为】:" + ConvertUtil.trace(strHex.getBytes()));
		
		
		// 5：取前8个字节用MAK加密(进行DES)
		System.arraycopy(strHex.getBytes(), 0, b1, 0, 8);
		System.out.println("【取前8字节进行MAK加密的数据】" + ConvertUtil.trace(b1));
//		System.out.println("【取前8字节进行MAK加密的数据】" + ByteOrHexString.bytes2HexString(b1));
		b1 = des(b1, maKey);
		System.out.println("【进行DES后的值】" + ByteOrHexString.bytes2HexString(b1));
		
		
		// 6：前8个字节进行DES的结果值与后8个字节（第4部分中得）异或运算
		System.arraycopy(strHex.getBytes(), 8, b2, 0, 8);
		System.out.println("【取后8字节的数据】" + ConvertUtil.trace(b2));
//		System.out.println("【取后8字节的数据】" + ByteOrHexString.bytes2HexString(b2));
		xorAfterData = xor(b1, b2);
		System.out.println("【前8个字节进行DES的结果值与后8个字节（第4部分中得）异或运算值】" + ConvertUtil.trace(xorAfterData));
//		System.out.println("【前8个字节进行DES的结果值与后8个字节（第4部分中得）异或运算值】" + ByteOrHexString.bytes2HexString(xorAfterData));
		
		
		// 7：用异或的结果,再一次用MAK加密（单倍长密钥算法运算）（进行DES运算）
		xorAfterData = des(xorAfterData, maKey);
		System.out.println("【异或的结果再次MAK加密后的数据】" + ConvertUtil.trace(xorAfterData));
//		System.out.println("【异或的结果再次MAK加密后的数据】" + ByteOrHexString.bytes2HexString(xorAfterData));
		
		// 8：将DES结果再一次转换成HEX
		strHex = ByteOrHexString.bytes2HexString(xorAfterData);
		System.out.println("DES结果再次转换HEX【" + strHex + "】");

		// 9：MAC (取前8个字节作为MAC值。)
		byte[] macValue =  strHex.substring(0, 8).getBytes();
		
		System.out.println("【MAC】(HEX)" + ByteOrHexString.bytes2HexString(macValue));
		System.out.println("【MAC】(String)" + new String(macValue));
		
		return macValue;
	}

/**
	 * 二个八字节进行XOR(异或)运算
	 * 
	 * @param b1
	 * @param b2
	 * @return 八字节数组
	 */
	public static byte[] xor(byte[] b1, byte[] b2) {
		byte[] result = new byte[8];
		for (int i = 0; i < 8; i++) {
			result[i] = (byte) (b1[i] ^ b2[i]);
		}
		return result;
	}

	/**
	 * 将字节转换成HEX形式
	 * 
	 * @param bArray
	 * @return HEX字符串
	 */
	public String byteToHex(byte[] bArray) {
		String result = "";
		byte b;
		for (int i = 0; i < bArray.length; i++) {
			b = bArray[i];
			result += Integer.toHexString((0x000000ff & b) | 0xffffff00)
					.substring(6);
		}
		return result;
	}
 	
	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 * 
	 * @param arrBTmp
	 *            构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws java.lang.Exception
	 */
	public static Key getKey(byte[] arrBTmp) throws Exception {
		// 创建一个空的8位字节数组（默认值为0）
		byte[] arrB = new byte[8];
		// 将原始字节数组转换为8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

		return key;
	}
	/**根据源数组 按每8个字节一组进行遍历异或 不满8字节补零 得到异或后的最后8个字节数组
	 * @param data 需要进行遍历分组异或的数据
	 * @return 异或之后的8字节数组
	 */
	public static byte[] xorData(byte[] data){
		System.out.println("################### 遍历异或处理 ###################");
		System.out.println("接收到的数据:" + ConvertUtil.trace(data));
		
		// 填充
		// 1：数据源
		// byte[] oSrc = src.getBytes();
		byte[] oSrc = data;

		int oLength = oSrc.length;

		// 2：目标数据源，用之后一系列计算，即用于填充：不需填充时直COPY否则需填充0X00
		byte[] nSrc;
		if (oLength % 8 == 0) {
			nSrc = new byte[oLength];
			System.arraycopy(oSrc, 0, nSrc, 0, oLength);
		} else {
			nSrc = new byte[(oLength / 8 + 1) * 8];
			System.arraycopy(oSrc, 0, nSrc, 0, oLength);
			for (int i = oLength; i < nSrc.length; i++) {
				nSrc[i] = 0X00;
			}
		}
		System.out.println("自动填充（0X00）后的数据:" + ConvertUtil.trace(nSrc));

		// 3：遍历分组进行xor（异或）运算。
		int reapt = nSrc.length / 8;
		byte[] b1 = new byte[8];
		byte[] b2 = new byte[8];
		// 异或运算的结果集
		byte[] temp = new byte[8];

		if (reapt > 1) {
			System.arraycopy(nSrc, 0, b1, 0, 8);
			System.out.println("第" + 1 + "个 B1【" + ByteOrHexString.bytes2HexString(b1) + "】" );
			System.arraycopy(nSrc, 8, b2, 0, 8);
			System.out.println("第" + 2 + "个 B2【" + ByteOrHexString.bytes2HexString(b2) + "】" );
			temp = xor(b1, b2);

			int j = 3;
			for (int i = 2; i < reapt; i++) {
				System.arraycopy(nSrc, i * 8, b1, 0, 8);
				System.out.println("第" + j + "个 B1【" + ByteOrHexString.bytes2HexString(b1) + "】" );
				j = j++;
				temp = xor(b1, temp);
			}

		}else if(reapt == 1){
			temp = nSrc;
		}else {
			System.out.println("数组长度太短，少于8个字节");
			return null;
		}

		System.out.println("【异或之后的8字节数组】:" + ConvertUtil.trace(temp));

		return temp;
		
	}
	
	/** string convert to hexstring*/
	public static String stringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }
        return hexString;
    }
}
