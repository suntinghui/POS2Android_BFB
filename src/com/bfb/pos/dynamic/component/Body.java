package com.bfb.pos.dynamic.component;

import java.io.IOException;
import java.util.ArrayList;

import com.bfb.pos.dynamic.core.Event;
import com.bfb.pos.dynamic.core.ViewPage;

public class Body extends Component{
	
	private ArrayList<Event> events;
	private boolean cleanSession;
	
	public Body(ViewPage viewPage) {
		this.viewPage = viewPage;
		this.viewPage.setBody(this);
		events = new ArrayList<Event>();
	}
	
	public ArrayList<Event> getEvents() {
		return events;
	}
	public void addAnEvent(Event event) {
		this.events.add(event);
	}
	public boolean isCleanSession() {
		return cleanSession;
	}
	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}
	public void init() {
		try{
			if (null == this.events || 0 == this.events.size()) {
				return; 
			}
			for (Event event:this.events) {
				event.trigger();
			}
		}catch(IOException e){
		} catch (ViewException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected Component construction(ViewPage viewPage) {
		return new Body(viewPage);
	}
	
}
