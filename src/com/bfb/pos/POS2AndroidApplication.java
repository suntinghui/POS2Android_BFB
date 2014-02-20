package com.bfb.pos;

import android.app.Application;
import android.util.Log;

public class POS2AndroidApplication extends Application{
	
	/**
	 * Called when the application is starting, before any other application
	 * objects have been created. Implementations should be as quick as possible
	 * (for example using lazy initialization of state) since the time spent in
	 * this function directly impacts the performance of starting the first
	 * activity, service, or receiver in a process. If you override this method,
	 * be sure to call super.onCreate().
	 * 
	 */
	@Override
	public void onCreate() {
		Log.e("app start", "Application onCreate...");
		
		super.onCreate();
	}

	/**
	 * Called when the application is stopping. There are no more application
	 * objects running and the process will exit. Note: never depend on this
	 * method being called; in many cases an unneeded application process will
	 * simply be killed by the kernel without executing any application code. If
	 * you override this method, be sure to call super.onTerminate().
	 * 
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}