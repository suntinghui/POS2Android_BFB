package com.bfb.pos.activity.view;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.inputmethodservice.Keyboard;

public class RandomPWDKeyboard extends Keyboard{
	
	public RandomPWDKeyboard(Context context, int xmlLayoutResId) {
		super(context, xmlLayoutResId);
		
		char[] c = getRandomString().toCharArray();
		List<Key> list = this.getKeys();
		
		for (Key key : list){
			if (key.codes[0] >= 48 && key.codes[0]<=57){
				key.label = String.valueOf(c[key.codes[0]-48]);
				int[] newcodes = new int[]{key.codes[0], c[key.codes[0]-48]};
				key.codes = newcodes;
			}
		}
	}

	public RandomPWDKeyboard(Context context, int xmlLayoutResId, int modeId) {
		super(context, xmlLayoutResId, modeId);
	}
	
	public RandomPWDKeyboard(Context context, int layoutTemplateResId,
			CharSequence characters, int columns, int horizontalPadding) {
		super(context, layoutTemplateResId, characters, columns, horizontalPadding);
	}
	
	private String getRandomString(){
    	StringBuilder sb = new StringBuilder();
    	int[] seed = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random ran = new Random();
		for (int i = 0; i < seed.length; i++) {
			int j = ran.nextInt(seed.length - i);
			sb.append(seed[j]);
			seed[j] = seed[seed.length - 1 - i];
		}
    	return sb.toString();
    }
}
