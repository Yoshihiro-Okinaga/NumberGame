//======================================================================
// NumberGameActivity.java
//
// NumberGame
//======================================================================
package com.NumberGame;

import android.app.Activity;
import android.os.Bundle;
import android.content.res.Configuration;

//======================================================================
// 
// NumberGameActivity
// 
//======================================================================
public class NumberGameActivity extends Activity
{
	//--------------------------
	// Data
	//--------------------------
	private NumberGameView mNumberGameView;
	
	//--------------------------
	// onCreate
	//--------------------------
	@Override public void onCreate(Bundle savedInstanceState)
	{
		mNumberGameView = new NumberGameView(this);
		
		super.onCreate(savedInstanceState);
		setContentView(mNumberGameView);
	}
	
	//--------------------------
	// onConfigurationChanged
	//--------------------------
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}
}

//======================================================================
