package com.bfb.pos.fsk;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.util.XORUtil;
import com.dhcc.pos.packets.util.ConvertUtil;
import com.itron.android.ftf.Util;
import com.itron.protol.android.CommandController;
import com.itron.protol.android.CommandReturn;
import com.itron.protol.android.CommandStateChangedListener;

public class CommandControllerEx extends CommandController {

	public CommandControllerEx(Context cx, CommandStateChangedListener s) {
		super(cx, s);
	}

	public CommandReturn Set_InfoRenew(int Disp_mode, String info) {
		try {
			return super.Set_InfoRenew(Disp_mode, info.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CommandReturn Cmd_Transfer_WithACK(String command, int timer) {
		return super.Cmd_Transfer_WithACK(HexToBin(command), timer);
	}

	public CommandReturn Cmd_Transfer_WithoutACK(String command, int timer) {
		return super.Cmd_Transfer_WithoutACK(HexToBin(command), timer);
	}

	@Override
	public CommandReturn Get_CardNo(int timer) {
		return super.Get_CardNo(timer);
	}

	@Override
	public CommandReturn Get_CardTrack(int timer) {
		return super.Get_CardTrack(timer);
	}

	@Override
	public CommandReturn Get_CommBattery() {
		return super.Get_CommBattery();
	}

	@Override
	public CommandReturn Get_CommExit() {
		return super.Get_CommExit();
	}

	@Override
	public CommandReturn Get_CommReTrans(int mode) {
		return super.Get_CommReTrans(mode);
	}

	public CommandReturn Get_ConOperator(int mode, String Parameter_Random, String cash, String append_data, int timer) {
		return super.Get_ConOperator(mode, Parameter_Random.getBytes(), cash.getBytes(), append_data.getBytes(), timer);
	}

	public CommandReturn Get_EncTrack(int DES_mode, int keyindex, String Random, int timer) {
		return super.Get_EncTrack(DES_mode, keyindex, null, timer);
	}

	public CommandReturn Get_ExtConOperator(int mode, String Parameter_Random, String cash, String append_data, int timer) {
		return super.Get_ExtConOperator(mode, Parameter_Random.getBytes(), cash.getBytes(), append_data.getBytes(), timer);
	}

	public CommandReturn Get_ExtCtrlConOperator(int mode, int PINkeyindex, int DESkeyindex, int MACkeyindex, String Ctrl_mode, String Parameter_Random, String cash, String append_data, int timer) {
		return super.Get_ExtCtrlConOperator(mode, PINkeyindex, DESkeyindex, MACkeyindex, HexToBin(Ctrl_mode)[0], HexToBin(Parameter_Random), HexToBin(cash), HexToBin(append_data), timer);
	}

	@Override
	public CommandReturn Get_ExtPsamNo() {
		return super.Get_ExtPsamNo();
	}

	public CommandReturn Get_MAC(int MAC_mode, int keyindex, String Random, String data) {
		// 对要进行mac计算的数据，先进行异或算法，能减少手机与点付宝的通讯量。
		// Important 最关键的是事先不进行异或计算与8583标准算法结果不一致。
		Log.e("data----", data);
		byte[] xorBytes = XORUtil.xorAfterData(StringUtil.hexStringToBytes(data));
		return super.Get_MAC(MAC_mode, keyindex, null, xorBytes);
	}

	public CommandReturn Get_CheckMAC(int MAC_mode, int keyindex, String Random, String data, String mac) {
		// 同Get_MAC注释
		byte[] xorBytes = XORUtil.xorAfterData(StringUtil.hexStringToBytes(data));
		byte[] macBytes = mac.getBytes();
		
//		Log.e("校验MAC原数据：", ConvertUtil.trace(StringUtil.hexStringToBytes(data)));
//		Log.e("异或MAC后的数据：", ConvertUtil.trace(xorBytes));
//		Log.e("服务器返回MAC：", ConvertUtil.trace(macBytes));

		// 将原数据（做过异或）与mac值合并
		byte[] concatBytes = new byte[xorBytes.length + macBytes.length];
		System.arraycopy(xorBytes, 0, concatBytes, 0, xorBytes.length);
		System.arraycopy(macBytes, 0, concatBytes, xorBytes.length, macBytes.length);

		// 计算mac的数据＋mac（8字节）
		return super.Get_CheckMAC(MAC_mode, keyindex, null, concatBytes);
	}

	// 这里一定要注意。cash必须为整数，即356表示3.56。不能出现小数点。。。
	public CommandReturn Get_PIN(int PIN_mode, int keyindex, String cash, String Random, String PAN, int timer) {
		return super.Get_PIN(PIN_mode, keyindex, cash.getBytes(), null, HexToBin(PAN), timer);
	}

	@Override
	public CommandReturn Get_PsamNo() {
		return super.Get_PsamNo();
	}

	@Override
	public CommandReturn Get_PsamRandom(int length) {
		return super.Get_PsamRandom(length);
	}

	@Override
	public CommandReturn Get_PtrState() {
		return super.Get_PtrState();
	}

	public CommandReturn Cmd_Display(String displayInfo, int timer) {
		try {
			return super.Cmd_Display(displayInfo.getBytes("GBK"), timer);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return super.Cmd_Display(displayInfo.getBytes(), timer);
		}
	}

	public CommandReturn Get_RenewKey(String PINkey, String MACkey, String DESkey) {
		byte[] pinB = Util.HexToBin(PINkey);
		Log.e("PIN：", Util.BinToHex(pinB, 0, pinB.length));

		byte[] macB = Util.HexToBin(MACkey);
		Log.e("MAC:", Util.BinToHex(macB, 0, macB.length));

		byte[] desB = Util.HexToBin(DESkey);
		Log.e("DES：", Util.BinToHex(desB, 0, desB.length));

		return super.Get_RenewKey(Util.HexToBin(PINkey), Util.HexToBin(MACkey), Util.HexToBin(DESkey));
	}

	public CommandReturn Get_RenewVendorTerID(String venndorID, String TerID) {
		return super.Get_RenewVendorTerID(venndorID.getBytes(), TerID.getBytes());
	}

	@Override
	public CommandReturn Get_VendorTerID() {
		return super.Get_VendorTerID();
	}

	@Override
	public CommandReturn Set_CloseTimeReset(int closetime) {
		return super.Set_CloseTimeReset(closetime);
	}

	@Override
	public CommandReturn Set_FSKParameter(boolean turn, boolean level) {
		return super.Set_FSKParameter(turn, level);
	}

	/***
	 * public CommandReturn Set_PtrData(int current_package, int total_package,
	 * int prt_count, String prt_data, int timer) {
	 * 
	 * return super.Set_PtrData(current_package, total_package, prt_count,
	 * HexToBin(prt_data), timer); }
	 ***/

	/**
	 * public CommandReturn Set_PtrData(){ String[] test = new String[]{
	 * "11      商户名称：中国联通\n商户存根            请妥善保管\n", //第一份的标题
	 * "12    商户名称：中国联通\n持卡人存根             请妥善保管\n", //第二份的标题
	 * "00商户编号（MERCHAANT NO） 123456", "00终端编号（TERMINAL NO） 123456",
	 * "00发卡行：中国银行", "00卡号（CARD NO）622209******6620", "00交易类型（TRANS TYPE）",
	 * "00    消费（SALE）", "00交易时间（DATE/TIME）", "00    2012/12/02   19:05:26",
	 * "00参考号（REFERENCE NO）P000000000324", "00交易金额（RMB）￥ 200.00",
	 * "00交易金额：1000.00元", "00备注（REFERENCE）：",
	 * 
	 * "00交易时间（DATE/TIME）", "00    2012/12/02   19:05:26",
	 * "00参考号（REFERENCE NO）P000000000324", "00交易金额（RMB）￥ 200.00",
	 * "00交易金额：1000.00元", "00备注（REFERENCE）：",
	 * 
	 * "21持卡人签名\n\n\n\n\n\n\n本人确认以上交易，同意将其计入本卡账户\nI ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES"
	 * , //第一份的落款 "22",//第二份的落款 }; return super.Set_PtrData(2, test, 60); }
	 ***/

	// 也许将凭条做成可配置的文件更灵活
	public CommandReturn Set_PtrData(String str) {
		try {
			HashMap<String, String> fieldsMap = new HashMap<String, String>();

			String[] array = str.split(";");
			for (String temp : array) {
				String[] field = temp.split("=");
				if (field.length == 2) {
					fieldsMap.put(field[0], field[1]);
				} else {
					fieldsMap.put(field[0], "");
				}
			}

			String[] content = new String[] { "11    商户名称：" + AppDataCenter.getValue("__MERCHERNAME") + "\n商户存根            请妥善保管\n", // 第一份的标题
					"12    商户名称：" + AppDataCenter.getValue("__MERCHERNAME") + "\n持卡人存根          请妥善保管\n", // 第二份的标题
					"00商户编号（MERCHAANT NO） " + fieldsMap.get("field42"), "00终端编号（TERMINAL NO） " + fieldsMap.get("field41"), "00发卡行 (BANK NAME)：" + fieldsMap.get("issuerBank"), "00卡号（CARD NO）", "00    " + StringUtil.formatAccountNo(fieldsMap.get("field2")), "00卡有效期 (VALID DATE) " + fieldsMap.get("field15"), "00交易类型（TRANS TYPE）", "00          " + AppDataCenter.getTransferName(fieldsMap.get("fieldTrancode")), "00交易时间（DATE/TIME）", "00    " + DateUtil.formatDateTime(fieldsMap.get("field13") + fieldsMap.get("field12")), "00交易金额（RMB）", "00    " + StringUtil.String2SymbolAmount(fieldsMap.get("field4")), "00参考号（REFERENCE NO）" + fieldsMap.get("field37"), "00批次号 (BATCH NO) " + fieldsMap.get("field60").substring(2, 8),
					// "00授权号 (AUTH NO) " + fieldsMap.get("field38"),
					"00流水号 (SERIAL NO) " + fieldsMap.get("field11"), "00备注（REFERENCE）:",

					"21持卡人签名\n\n\n\n\n\n本人确认以上交易，同意将其计入本卡账户\nI ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", // 第一份的落款
					"22",// 第二份的落款
			};

			 return super.Set_PtrData(2, content, 60);


		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] HexToBin(String str) {
		if (str.trim().equals("null") || null == str) {
			return null;
		}

		return Util.HexToBin(str);
	}

}
