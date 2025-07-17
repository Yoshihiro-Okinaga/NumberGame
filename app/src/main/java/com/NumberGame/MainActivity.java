//======================================================================
// MainActivity.java
//
// NumberGame
//======================================================================
package com.NumberGame;

import android.app.Activity;
import android.os.Bundle;
import android.content.res.Configuration;

//======================================================================
// 
// MainActivity
// 
//======================================================================
public class MainActivity extends Activity
{
	//--------------------------
	// Data
	//--------------------------
	private MainView mMainView;
	
	//--------------------------
	// onCreate
	//--------------------------
	@Override public void onCreate(Bundle savedInstanceState)
	{
		mMainView = new MainView(this);
		
		super.onCreate(savedInstanceState);
		setContentView(mMainView);
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
