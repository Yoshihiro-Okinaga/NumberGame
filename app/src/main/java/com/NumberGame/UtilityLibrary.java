//======================================================================
// UtilityLibrary.java
//
// UtilityLibrary
//======================================================================
package com.NumberGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Rect;

//======================================================================
// 
// UtilityLibrary
// 
//======================================================================
class UtilityLibrary
{
	//--------------------------
	// Assetsからテキストファイル読み込み
	//--------------------------
	public static String loadFileStringAssets(Context context, String fileName)
	{
		AssetManager as = context.getResources().getAssets();
		InputStream is = null;
		BufferedReader br = null;
		String strData = "";
		
		try{  
			try {  
				is = as.open(fileName);  
				br = new BufferedReader(new InputStreamReader(is));
				
				String str;
				while((str = br.readLine()) != null){
					strData += str + "\r\n";
				}
			}
			finally {  
				if (br != null) br.close();
			}
		}
		catch (IOException e) {  
			// Toast.makeText(this, "読み込み失敗", Toast.LENGTH_SHORT).show();
		}
		return strData;
	}
	//--------------------------
	// 文字と文字サイズから実サイズを得る
	//--------------------------
	public static Point getMessageSize(Paint paint, String message, int fontSize)
	{
		Point pnt = new Point();
		
		paint.setTextSize(fontSize);
		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		
		pnt.x = (int) paint.measureText(message);
		pnt.y = (int)( fontMetrics.descent - fontMetrics.ascent );
		
		return pnt;
	}
	
	//--------------------------
	// drawRect
	//--------------------------
	public static void drawRect(Paint paint, Canvas canvas, int x, int y, int width, int height)
	{
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(new Rect(x, y, x + width, y + height), paint);
	}
	
	//--------------------------
	// fillRect
	//--------------------------
	public static void fillRect(Paint paint, Canvas canvas, int x, int y, int width, int height)
	{
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(new Rect(x, y, x + width, y + height), paint);
	}
	
	//--------------------------
	// drawText
	//--------------------------
	public static void drawText(Paint paint, Canvas canvas, String str, int x, int y)
	{
		paint.setStyle(Paint.Style.FILL);
		canvas.drawText( str, x, y - paint.ascent() + paint.descent(), paint);
	}
}

//======================================================================
