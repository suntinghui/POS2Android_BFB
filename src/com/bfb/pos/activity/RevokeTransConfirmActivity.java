package com.bfb.pos.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bfb.pos.activity.view.PasswordWithLabelView;
import com.bfb.pos.agent.client.AppDataCenter;
import com.bfb.pos.agent.client.Constant;
import com.bfb.pos.agent.client.StaticNetClient;
import com.bfb.pos.agent.client.TransferLogic;
import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.util.ByteUtil;
import com.bfb.pos.util.DateUtil;
import com.bfb.pos.util.StringUtil;
import com.bfb.pos.util.UnionDes;
import com.bfb.pos.util.XORUtil;
import com.bfb.pos.R;
import com.dhcc.pos.packets.util.ConvertUtil;

public class RevokeTransConfirmActivity extends BaseActivity implements OnClickListener {
	
	private Button backButton = null;
	private Button okButton  = null;
	private PasswordWithLabelView passwordText = null;
	
	private HashMap<String, String> map = null;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.revoke_trans_confirm);
		
		this.findViewById(R.id.topInfoView);
		
		passwordText = (PasswordWithLabelView) this.findViewById(R.id.passwordET);
		passwordText.setHintWithLabel(this.getResources().getString(R.string.pwd2), this.getResources().getString(R.string.pInputNewPwd));
		
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		
 		map = (HashMap<String, String>) this.getIntent().getSerializableExtra("map");
		if (null != map && map.size() != 0){
			((TextView)this.findViewById(R.id.rSerialNo)).setText(map.get("field11"));
			((TextView)this.findViewById(R.id.rReferIndex)).setText(map.get("field37"));
			((TextView)this.findViewById(R.id.rAccountNo)).setText(StringUtil.formatAccountNo(map.get("field2")));
			((TextView)this.findViewById(R.id.rAmount)).setText(StringUtil.String2SymbolAmount(map.get("field4")));
			((TextView)this.findViewById(R.id.rBatchNum)).setText(map.get("field60").substring(2, 8));
			((TextView)this.findViewById(R.id.rTransTime)).setText(DateUtil.formatDateTime(map.get("field13")+map.get("field12")));
		}
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
			
		case R.id.okButton:
			if (passwordText.getText().trim().length() != 6){
				Toast.makeText(this, this.getResources().getString(R.string.pInputNewPwd), Toast.LENGTH_SHORT).show();
				break;
			}
			if(Constant.isAISHUA){
				try {

					Event event = new Event(null, "xiaofeichexiao", null);
					event.setTransfer("020023");

					// 磁道密文加pinkey
					String str0 = AppDataCenter.getENCTRACK() + Constant.pinkey;
					// 16位异或 值作des key
					byte[] tmpByte = XORUtil.xorDataFor16(ByteUtil.hexStringToBytes(str0));
					
					String tmpStr = passwordText.getText() + "00";

					String pin52= ConvertUtil.bytesToHexString(tmpStr.getBytes());
					byte[] desByte = UnionDes.TripleDES(tmpByte, ByteUtil.hexStringToBytes(pin52));//3131313131313030
//					map.put("AISHUAPIN", "B2A85787299895F6");
					map.put("AISHUAPIN", ByteUtil.bytesToHexString(desByte));
					
					String flagStr = "01";
					String randomStr = AppDataCenter.getRANDOM();
					String enctrackStr = AppDataCenter.getENCTRACK();
					String enctracks = flagStr + randomStr + enctrackStr;
					map.put("ENCTRACKS", enctracks);
					event.setStaticActivityDataMap(map);
					event.trigger();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				// 因为在这里有可能交易后验证服务器响应数据时点付宝发生异常，这时应该检查冲正。
				if (TransferLogic.getInstance().reversalAction()){
					return;
				}
				
				if (Constant.isStatic){
					StaticNetClient.demo_amount = map.get("field4");
				}
				
				if (null != map && map.size() != 0){
					try{
				        Event event = new Event(null,"RevokeTransConfirm", null);
				        String fskStr = "Get_PsamNo|null#Get_VendorTerID|null#Get_EncTrack|int:0,int:0,string:null,int:60#Get_PIN|int:0,int:0,string:"+ map.get("field4") +",string:null,string:__PAN,int:60";
				        event.setFsk(fskStr);
				        event.setTransfer("020023");
				        map.put("fieldMerchPWD", passwordText.getEncryptPWD());
				        event.setStaticActivityDataMap(map);
				        
				        event.trigger();
				        
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			break;
		}
		
	}
	
}
