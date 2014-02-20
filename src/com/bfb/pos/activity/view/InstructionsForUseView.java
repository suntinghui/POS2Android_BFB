package com.bfb.pos.activity.view;

import java.io.IOException;
import java.io.InputStream;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bfb.pos.util.AssetsUtil;
import com.bfb.pos.R;

public class InstructionsForUseView extends LinearLayout{
	
	private TextView instructionsText = null;
	
	public InstructionsForUseView(Context context) {
		super(context);
		
		init(context, null);
	}

	public InstructionsForUseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.instruction);
		String instructionId = ta.getString(R.styleable.instruction_instructionId);
		ta.recycle();
		
		init(context, instructionId);
	}
	
	private void init(Context context, String instructionId){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.instructions_use_view, this);
		
		instructionsText = (TextView) this.findViewById(R.id.instructionText);
		instructionsText.setText(getInstructionContent(instructionId));
	}
	
	public String getInstructionContent(String instructionId){
		if (null == instructionId)
			return "";
		
		try{
			InputStream stream = AssetsUtil.getInputStreamFromPhone("instructions.xml");
			KXmlParser parser = new KXmlParser(); 
			parser.setInput(stream,"utf-8");
	        int eventType = parser.getEventType();
	        while(eventType!=XmlPullParser.END_DOCUMENT){  
	            switch(eventType){  
	            case XmlPullParser.START_TAG:
	                if(instructionId.equalsIgnoreCase(parser.getName())){
	                	return parser.getAttributeValue(null, "value").replace("/n", "\n");
	                }  
	                 
	                break;
	            }  
	            eventType = parser.next();//进入下一个元素并触发相应事件  
	        }
	        
		}catch(IOException e){
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	public TextView getInstructionsText() {
		return instructionsText;
	}
	
}
