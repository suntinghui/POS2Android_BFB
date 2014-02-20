package com.bfb.pos.fsk;

import android.content.Intent;
import android.os.Handler;

import com.bfb.pos.agent.client.ApplicationEnvironment;
import com.bfb.pos.agent.client.Constant;

public class FSKOperator {
	
	public static Handler fskHandler = null;
	
	public static void execute(String fskCommand, Handler handler){
		fskHandler = handler;
		
		Intent intent  = null;
		if (Constant.isAISHUA){
			intent = new Intent(ApplicationEnvironment.getInstance().getApplication(), AiShuaService.class);
		} else {
			intent = new Intent(ApplicationEnvironment.getInstance().getApplication(), FSKService.class);
		}
		
		intent.putExtra("FSKCOMMAND", fskCommand);
		ApplicationEnvironment.getInstance().getApplication().startService(intent);
	}

}
