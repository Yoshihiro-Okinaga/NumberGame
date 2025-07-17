//======================================================================
// NumberGameHandler.java
//
// NumberGameハンドラ
//======================================================================
package com.NumberGame;

import android.os.Handler;
import android.os.Message;

//======================================================================
// 
// NumberGameHandler
// 
//======================================================================
public class NumberGameHandler extends Handler
{
	//--------------------------
	// Data
	//--------------------------
	private MainView mMainView;
	
	//--------------------------
	// コンストラクタ
	//--------------------------
	public NumberGameHandler(MainView MainView)
	{
		super();
		mMainView = MainView;
	}
	
	//--------------------------
	// handleMessage
	//--------------------------
	@Override public void handleMessage(Message msg)
	{
		mMainView.update();
	}
	
	//--------------------------
	// sleep
	//--------------------------
	public void sleep(long delayMillis)
	{
		this.removeMessages(0);
		sendMessageDelayed(obtainMessage(0), delayMillis);
	}
}

//======================================================================
