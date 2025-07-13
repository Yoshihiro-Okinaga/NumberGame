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
	private NumberGameView mNumberGameView;
	
	//--------------------------
	// コンストラクタ
	//--------------------------
	public NumberGameHandler(NumberGameView numberGameView)
	{
		super();
		mNumberGameView = numberGameView;
	}
	
	//--------------------------
	// handleMessage
	//--------------------------
	@Override public void handleMessage(Message msg)
	{
		mNumberGameView.update();
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
