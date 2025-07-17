//======================================================================
// MainView.java
// 
// MainView
//======================================================================
package com.NumberGame;

import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.net.Uri;
import java.util.Random;
import java.lang.Math;

//======================================================================
// 
// MainView
// 
//======================================================================
public class MainView extends View implements MediaPlayer.OnCompletionListener
{
	//----------------------------------------------------------------------
	// 
	// 変数
	// 
	//----------------------------------------------------------------------
	//--------------------------
	// 定数
	//--------------------------
	// ゲームステータス
	private final int STAT_SELECTMODE = 0;
	private final int STAT_MAKEPROBLEM = 1;
	private final int STAT_WAITINPUTANSWER = 2;
	private final int STAT_ANSWER = 3;
	private final int STAT_RESULT = 4;
	private final int STAT_NUM = 5;
	
	// サウンド
	private final int SE_DECIDE = 0;
	private final int SE_CANCEL = 1;
	private final int SE_RIGHT = 2;
	private final int SE_MISS = 3;
	private final int SE_NUM = 4;
	
	// 四則演算のタイプ
	private final int TYPE_PLUS = 0;
	private final int TYPE_MINUS = 1;
	private final int TYPE_TIMES = 2;
	private final int TYPE_DEVIDE = 3;
	private final int TYPE_NUM = 4;
	private final int TYPE_EQUAL = TYPE_NUM;
	private final int TYPE_ANSWER = 5;
	private final int TYPE_ALL_NUM = 6;
	
	// 向き
	private final int ORIENTATION_PORTRAIT = 0;
	private final int ORIENTATION_LANDSCAPE = 1;
	private final int ORIENTATION_NUM = 2;
	
	// その他
	private final int NUMBER_MAX = 9;
	private final int LEVEL_MAX = 4;
	private final int MAKEPROBLEM_CALC_LIMIT = 5;
	private final int ERROR_CALC_NUM = 0x80000000;
	
	// その他定数
	private final int GAME_FPS = 60;
	private final int GAME_FPS_PER = 16;
	private final int PARENTHESESRATE = 8;
	private final int ANSWER_WAIT_FRAME = 15;
	private final int SELECT_LEVEL_WAIT_FRAME = 8;
	private final int RESULT_WAIT_FRAME = 8;
	
	//--------------------------
	// 定数・定型データ
	//--------------------------
	private int [][] mTypeFramePosX = new int[ ORIENTATION_NUM ][ TYPE_NUM ];
	private int [][] mTypeFramePosY = new int[ ORIENTATION_NUM ][ TYPE_NUM ];
	private int [][] mTypeFontPosX = new int[ ORIENTATION_NUM ][ TYPE_NUM ];
	private int [][] mTypeFontPosY = new int[ ORIENTATION_NUM ][ TYPE_NUM ];
	
	private int [] mProblemPosY = new int[ ORIENTATION_NUM ];
	private int [] mGameTimerPosX = new int[ ORIENTATION_NUM ];
	private int [] mGameTimerPosY = new int[ ORIENTATION_NUM ];
	
	private int [][] mLevelFramePosX = new int[ ORIENTATION_NUM ][ LEVEL_MAX ];
	private int [][] mLevelFramePosY = new int[ ORIENTATION_NUM ][ LEVEL_MAX ];
	private int [][] mLevelFontPosX = new int[ ORIENTATION_NUM ][ LEVEL_MAX ];
	private int [][] mLevelFontPosY = new int[ ORIENTATION_NUM ][ LEVEL_MAX ];
	private int mLevelFrameWidth = -1;
	private int mLevelFrameHeight = -1;
	
	private int [] mCalcNumberNum = new int[ LEVEL_MAX ];
	private int [] mCalcNumberMax = new int[ LEVEL_MAX ];
	
	private String [] mTypeString = { "+", "-", "x", "÷", "=", "○" };
	private String [] mLevelString = { "Level1", "Level2", "Level3", "Level4" };
	
	private int mTypeFontWidth;
	private int mTypeFontHeight;
	private int mTypeFontSizeHalf;
	
	private int mProblemFontSize;
	
	private int mFontSize2;
	private int mFontSize4;
	private int mFontSize10;
	private int mFontSize16;
	private int mFontSize24;
	private int mFontSize32;
	private int mFontSize40;
	private int mFontSize48;
	private int mFontSize64;
	private int mFontSize80;
	private int mFontSize96;
	private int mFontSize120;
	private int mFontSize160;
	private int mFontSize240;
	private int mFontSize320;
	private int mFontSize480;
	
	//--------------------------
	// SystemData
	//--------------------------
	private int mTouchPosX = 0;
	private int mTouchPosY = 0;
	private boolean mbTouched = false;
	private boolean mbPreTouched = false;
	private boolean mbCheckConstData = false;
	
	private Paint mPaint;
	private int mViewWidth = 500;
	private int mViewHeight = 500;
	private NumberGameHandler mNumberGameHandler;
	private Bitmap mBitmap;
	private Random mRandom = new Random();
	
	//--------------------------
	// GameData
	//--------------------------
	private int mStatus = STAT_SELECTMODE;
	private int mFrameCnt = 0;
	private int mTouchCnt = -1;
	private int mTouchType = -1;
	private int mAnswer = -1;
	private int mLevel = 0;
	private int mOrientationType = ORIENTATION_PORTRAIT;
	
	private int mNumberNum;
	private int mNumberMax;
	private int mCalcTypesNum;
	private int mCalcNum;
	private int mCalcParentheses;
	private int mProblemNum;
	
	private int mCorrectNum;
	private int mMissNum;
	private int mResultCnt;
	private int mGameTime;
	
	private int [] mNumberData = new int[ NUMBER_MAX ];
	private int [] mNumberDataTemp = new int[ NUMBER_MAX ];
	private int [] mNumberDataHalf = new int[ NUMBER_MAX ];
	private int [] mCalcTypesOrder = new int[ NUMBER_MAX ];
	private MediaPlayer [] mSeMediaPlayer = null;
	
	//----------------------------------------------------------------------
	// 
	// メソッド
	// 
	//----------------------------------------------------------------------
	//--------------------------
	// コンストラクタ
	//--------------------------
	public MainView(Context context)
	{
		super(context);
		
		mPaint = new Paint();
		
		mNumberGameHandler = new NumberGameHandler(this);
		mbCheckConstData = false;
		//Resources r = context.getResources();
		//mBitmap = BitmapFactory.decodeResource(r, R.drawable.ns);
		setClickable(true);
		initializeConstData();
		initializeSoundData();
	}
	
	//--------------------------
	// onDraw
	//--------------------------
	@Override protected void onDraw(Canvas canvas)
	{
		// 背景のクリア
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(mFontSize2);
		UtilityLibrary.fillRect(mPaint, canvas, 0, 0, mViewWidth, mViewHeight);
		
		//--------------------------
		// 
		// メインゲーム
		// 
		//--------------------------
		if( mStatus >= STAT_MAKEPROBLEM && mStatus <= STAT_ANSWER ) {
			//--------------------------
			// 問題文表示
			//--------------------------
			int stringSizeWidth = 0;
			for( int i = 0; i < mNumberNum; i++ ) {
				
				if( i == mCalcParentheses * 2 ) {
					Point sizeParentheses = UtilityLibrary.getMessageSize(mPaint, "(", mProblemFontSize);
					stringSizeWidth += sizeParentheses.x;
				}
				
				String str = "";
				if( ( i & 1 ) != 0 ) {
					str = mTypeString[ mNumberData[ i ] ];
				}
				else {
					str = "" + mNumberData[ i ];
				}
				
				Point sizeStr = UtilityLibrary.getMessageSize(mPaint, str, mProblemFontSize);
				stringSizeWidth += sizeStr.x;
				
				if( mCalcParentheses >= 0 && i == mCalcParentheses * 2 + 2 ) {
					
					Point sizeParentheses = UtilityLibrary.getMessageSize(mPaint, ")", mFontSize40);
					stringSizeWidth += sizeParentheses.x;
				}
			}
			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(mProblemFontSize);
			mPaint.setTextAlign(Paint.Align.LEFT);
			
			int problemDispX = ( mViewWidth - stringSizeWidth ) / 2;
			for( int i = 0; i < mNumberNum; i++ ) {
				
				if( i == mCalcParentheses * 2 ) {
					Point sizeParentheses = UtilityLibrary.getMessageSize(mPaint, "(", mProblemFontSize);
					UtilityLibrary.drawText(mPaint, canvas, "(", problemDispX, mProblemPosY[ mOrientationType ]);
					problemDispX += sizeParentheses.x;
				}
				
				String str = "";
				if( ( i & 1 ) != 0 ) {
					if( mNumberData[ i ] == TYPE_ANSWER && mTouchCnt > 0 ) {
						str = mTypeString[ mAnswer ];
						
						Point sizeAnswer = UtilityLibrary.getMessageSize(mPaint, mTypeString[ TYPE_ANSWER ], mProblemFontSize);
						Point sizeTrueAnswer = UtilityLibrary.getMessageSize(mPaint, mTypeString[ mAnswer ], mProblemFontSize);
						
						mPaint.setColor(Color.RED);
						
						UtilityLibrary.drawText(mPaint, canvas, str, problemDispX + ( ( sizeAnswer.x - sizeTrueAnswer.x ) / 2 ), mProblemPosY[ mOrientationType ]);
						
						problemDispX += sizeAnswer.x;
						mPaint.setColor(Color.WHITE);
						continue;
					}
					else {
						str = mTypeString[ mNumberData[ i ] ];
					}
				}
				else {
					str = "" + mNumberData[ i ];
				}
				
				Point sizeStr = UtilityLibrary.getMessageSize(mPaint, str, mProblemFontSize);
				UtilityLibrary.drawText(mPaint, canvas, str, problemDispX, mProblemPosY[ mOrientationType ]);
				problemDispX += sizeStr.x;
				
				if( mCalcParentheses >= 0 && i == mCalcParentheses * 2 + 2 ) {
					Point sizeParentheses = UtilityLibrary.getMessageSize(mPaint, ")", mProblemFontSize);
					UtilityLibrary.drawText(mPaint, canvas, ")", problemDispX, mProblemPosY[ mOrientationType ]);
					problemDispX +=  sizeParentheses.x;
				}
			}
			
			//--------------------------
			// 答えパネル
			//--------------------------
			for( int i = 0; i < TYPE_NUM; i++ ) {
				
				if( i == mTouchType && mTouchCnt >= 0 ) {
					mPaint.setColor(Color.GRAY);
					UtilityLibrary.fillRect(mPaint, canvas, mTypeFramePosX[ mOrientationType ][ i ], mTypeFramePosY[ mOrientationType ][ i ], mTypeFontWidth, mTypeFontHeight);
					mPaint.setColor(Color.WHITE);
				}
				
				UtilityLibrary.drawRect(mPaint, canvas, mTypeFramePosX[ mOrientationType ][ i ], mTypeFramePosY[ mOrientationType ][ i ], mTypeFontWidth, mTypeFontHeight);
				
				mPaint.setTextSize(mFontSize64);
				UtilityLibrary.drawText(mPaint, canvas, mTypeString[ i ], mTypeFontPosX[ mOrientationType ][ i ], mTypeFontPosY[ mOrientationType ][ i ]);
			}

			if (mTouchCnt > 0) {
				if( mTouchType == mAnswer ) {
					UtilityLibrary.drawText(mPaint, canvas, "正解", mFontSize64, mFontSize40 * 9);
				}
				else {
					UtilityLibrary.drawText(mPaint, canvas, "不正解", mFontSize64, mFontSize40 * 9);
				}
			}
			
			//--------------------------
			// タイマー
			//--------------------------
			/*
			NSString* pTimerStr = [self getGameTimeString];
			[self drawString:pTimerStr x:mGameTimerPosX[ mOrientationType ] y:mGameTimerPosY[ mOrientationType ] font:mpFont24];
			
			if( mStatus == STAT_ANSWER ) {
				NSString* pStrAns = nil;
				UIFont* pFontAns = nil;
				if( mTouchType == mAnswer ) { pStrAns = @"○"; pFontAns = mpFont160; }
				else { pStrAns = @"×"; pFontAns = mpFont240; }
				
				[self setColor_r:ANSWER_FONT_COLOR_R g:ANSWER_FONT_COLOR_G b:ANSWER_FONT_COLOR_B];
				
				CGSize answerRect = [self getDrawUIFontSize:pStrAns font:pFontAns];
				int answerDispPosX = mTypeFramePosX[ mOrientationType ][ mTouchType ] + mTypeFontSizeHalf - answerRect.width * 0.5f;
				int answerDispPosY = mTypeFramePosY[ mOrientationType ][ mTouchType ] + mTypeFontSizeHalf - answerRect.height * 0.5f;
				
				[self drawString:pStrAns x:answerDispPosX y:answerDispPosY font:pFontAns];
			}
			*/
		}
		
		//--------------------------
		// 
		// モード選択
		// 
		//--------------------------
		if( mStatus == STAT_SELECTMODE ) {
			mPaint.setColor(Color.WHITE);
			for( int i = 0; i < LEVEL_MAX; i++ ) {
				if( i == mLevel && mTouchCnt >= 0 ) {
					mPaint.setColor(Color.GRAY);
					UtilityLibrary.fillRect(mPaint, canvas, mLevelFramePosX[ mOrientationType ][ i ], mLevelFramePosY[ mOrientationType ][ i ], mLevelFrameWidth, mLevelFrameHeight);
					mPaint.setColor(Color.WHITE);
				}
				UtilityLibrary.drawRect(mPaint, canvas, mLevelFramePosX[ mOrientationType ][ i ], mLevelFramePosY[ mOrientationType ][ i ], mLevelFrameWidth, mLevelFrameHeight);
				
				mPaint.setTextSize(mFontSize48);
				mPaint.setTextAlign(Paint.Align.LEFT);
				UtilityLibrary.drawText(mPaint, canvas, mLevelString[ i ], mLevelFontPosX[ mOrientationType ][ i ], mLevelFontPosY[ mOrientationType ][ i ]);
			}
		}
		
		//--------------------------
		// 
		// リザルト
		// 
		//--------------------------
		if( mStatus == STAT_RESULT ) {
			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(mFontSize40);
			mPaint.setTextAlign(Paint.Align.LEFT);
			
			String correctStr = "○:" + mCorrectNum;
			UtilityLibrary.drawText(mPaint, canvas, correctStr, mFontSize64, mFontSize40 * 3);
			
			String missStr = "×:" + mMissNum;
			UtilityLibrary.drawText(mPaint, canvas, missStr, mFontSize64, mFontSize40 * 4);
			
			//String timerStr = getGameTimeString();
			//canvas.drawText( timerStr, mFontSize64, mFontSize40 * 5, mPaint );
		}
		
		/*
		int w = mBitmap.getWidth();
		int h = mBitmap.getHeight();
		Rect src = new Rect(0, 0, w, h);
		Rect dst = new Rect(mPosX, mPosY + 100, mPosX + w, mPosY + 100 + h);
		//canvas.drawBitmap(mBitmap, src, dst, null);
		*/
	}
	
	//--------------------------
	// onSizeChanged
	//--------------------------
	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mViewWidth = w;
		mViewHeight = h;
		
		super.onSizeChanged(w, h, oldw, oldh);
		if( !mbCheckConstData ) {
			mbCheckConstData = true;
			initializeConstData();
		}
		
		update();
	}
	
	//--------------------------
	// onTouchEvent
	//--------------------------
	@Override public boolean onTouchEvent(MotionEvent event)
	{
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			{
				float newPosX = event.getX();
				float newPosY = event.getY();
				if (mTouchPosX != newPosX || mTouchPosY != newPosY) {
					mTouchPosX = (int) newPosX;
					mTouchPosY = (int) newPosY;
					mbTouched = true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mbTouched = false;
			break;
		}
		
		return true;
	}
	
	//--------------------------
	// update
	//--------------------------
	public void update()
	{
		Configuration config = getResources().getConfiguration();
		// Portrait(縦長)
		if(config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mOrientationType = ORIENTATION_PORTRAIT;
		} 
		// Landscape(横長)
		else {
			mOrientationType = ORIENTATION_LANDSCAPE;
		}
		
		mFrameCnt++;
		
		if( mStatus == STAT_SELECTMODE ) {
		}
		
		int touchCount = 0;
		if( mbTouched && !mbPreTouched ) { touchCount = 1; }
		
		for( int i = 0; i < touchCount; i++ ) {
			//--------------------------
			// 
			// 回答入力待ち
			// 
			//--------------------------
			if( mStatus == STAT_WAITINPUTANSWER && ( mbTouched && !mbPreTouched ) ) {
				
				for( int j = 0; j < TYPE_NUM; j++ ) {
					
					if( mTouchPosX < mTypeFramePosX[ mOrientationType ][ j ] ) { continue; }
					if( mTouchPosY < mTypeFramePosY[ mOrientationType ][ j ] ) { continue; }
					if( mTouchPosX > mTypeFramePosX[ mOrientationType ][ j ] + mTypeFontWidth ) { continue; }
					if( mTouchPosY > mTypeFramePosY[ mOrientationType ][ j ] + mTypeFontHeight ) { continue; }
					
					mTouchCnt = ANSWER_WAIT_FRAME;
					mTouchType = j;
					
					mStatus = STAT_ANSWER;
					if( mTouchType == mAnswer ) {
						mCorrectNum++;
						callSoundSe( SE_RIGHT );
					}
					else {
						mMissNum++;
						callSoundSe( SE_MISS );
					}
					break;
				}
			}
			
			//--------------------------
			// 
			// 問題選択待ち
			// 
			//--------------------------
			if( mStatus == STAT_SELECTMODE && ( mbTouched && !mbPreTouched ) ) {
				
				for( int j = 0; j < LEVEL_MAX; j++ ) {
					
					if( mTouchPosX < mLevelFramePosX[ mOrientationType ][ j ] ) { continue; }
					if( mTouchPosY < mLevelFramePosY[ mOrientationType ][ j ] ) { continue; }
					if( mTouchPosX > mLevelFramePosX[ mOrientationType ][ j ] + mLevelFrameWidth ) { continue; }
					if( mTouchPosY > mLevelFramePosY[ mOrientationType ][ j ] + mLevelFrameHeight ) { continue; }
					
					mTouchCnt = SELECT_LEVEL_WAIT_FRAME;
					mLevel = j;
					
					callSoundSe( SE_RIGHT );
					break;
				}
			}
		}
		
		//--------------------------
		// 
		// リザルト
		// 
		//--------------------------
		if( mStatus == STAT_RESULT ) {
			
			mResultCnt++;
			if( mResultCnt > RESULT_WAIT_FRAME && ( mbTouched && !mbPreTouched ) ) {
				
				callSoundSe( SE_RIGHT );
				mStatus = STAT_SELECTMODE;
			}
		}
		
		//--------------------------
		// 
		// 選択待ち
		// 
		//--------------------------
		if( mTouchCnt >= 0 ) {
			
			mTouchCnt--;
			if( mTouchCnt == 0 ) {
				
				if( mStatus == STAT_ANSWER ) {
					
					if( mCorrectNum + mMissNum >= mProblemNum ) {
						
						mResultCnt = 0;
						mStatus = STAT_RESULT;
					}
					else {
						
						mStatus = STAT_MAKEPROBLEM;
					}
				}
				if( mStatus == STAT_SELECTMODE ) {
					
					mCorrectNum = 0;
					mMissNum = 0;
					mProblemNum = 10;
					mGameTime = 0;
					
					mNumberNum = mCalcNumberNum[ mLevel ];
					mNumberMax = mCalcNumberMax[ mLevel ];
					mCalcNum = 0;
					mTouchType = -1;
					mCalcTypesNum = ( mNumberNum - 1 ) / 2;
					
					mStatus = STAT_MAKEPROBLEM;
					
				}
			}
		}
		
		//--------------------------
		// 
		// 問題作成
		// 
		//--------------------------
		if( mStatus == STAT_MAKEPROBLEM ) {
			
			makeProbrem();
			
			mStatus = STAT_WAITINPUTANSWER;
		}
		
		if( mStatus >= STAT_MAKEPROBLEM && mStatus <= STAT_ANSWER ) {
			
			mGameTime++;
			if( mGameTime > 99 * 60 * GAME_FPS + 59 * GAME_FPS + GAME_FPS ) {
				
				mGameTime = 99 * 60 * GAME_FPS + 59 * GAME_FPS + GAME_FPS;
			}
		}
		
		// システム
		mbPreTouched = mbTouched;
		invalidate();
		
		mNumberGameHandler.sleep(GAME_FPS_PER);
	}
	
	//----------------------------------------------------------------------
	// 
	// システム補助関連
	// 
	//----------------------------------------------------------------------
	//--------------------------
	// 定数データ初期化
	//--------------------------
	private void initializeConstData()
	{
		int viewWidth = mViewWidth;
		int viewHeight = mViewHeight;
		if( viewWidth > viewHeight ) {
			int temp = viewWidth;
			viewWidth = viewHeight;
			viewHeight = temp;
		}
		
		//--------------------------
		// フォントサイズ
		//--------------------------
		mFontSize2  = viewWidth / 160;
		mFontSize4  = viewWidth / 80;
		mFontSize10 = viewWidth / 32;
		mFontSize16 = viewWidth / 20;
		mFontSize24 = viewWidth * 3 / 40;
		mFontSize32 = viewWidth / 10;
		mFontSize40 = viewWidth / 8;
		mFontSize48 = viewWidth * 3 / 20;
		mFontSize64 = viewWidth / 5;
		mFontSize80 = viewWidth / 4;
		mFontSize96 = viewWidth * 3 / 10;
		mFontSize120 = viewWidth * 3 / 8;
		mFontSize160 = viewWidth / 2;
		mFontSize240 = viewWidth * 3 / 4;
		mFontSize320 = viewWidth;
		mFontSize480 = viewWidth * 3 / 2;
		
		//--------------------------
		// 位置情報
		//--------------------------
		int [] screenWidth = { mViewWidth, mViewHeight };
		int [] screenHeight = { mViewHeight, mViewWidth };
		int screenQuarter = viewWidth / 4;
		int screenQuarterHalf = screenQuarter / 2;
		
		mProblemFontSize = mFontSize40;
		
		for( int i = 0; i < ORIENTATION_NUM; i++ ) {
			// +-x/のサイズ
			int typeFontWidth = -1;
			int typeFontHeight = -1;
			for( int j = 0; j < TYPE_NUM; j++ ) {
				Point fontSize = UtilityLibrary.getMessageSize(mPaint, mTypeString[j], mFontSize64);
				if( typeFontWidth < fontSize.x ) { typeFontWidth = fontSize.x; }
				if( typeFontHeight < fontSize.y ) { typeFontHeight = fontSize.y; }
			}
			mTypeFontWidth = typeFontWidth + mFontSize16;
			mTypeFontHeight = typeFontHeight + mFontSize16;
			
			// 前計算
			int verticalNum = ( int ) Math.sqrt(TYPE_NUM);
			int horizonNum = TYPE_NUM / verticalNum;
			
			// 問題分+Time
			mProblemPosY[ i ] = mFontSize32;
			mGameTimerPosY[ i ] = screenHeight[ i ] * 9 / 10;
			
			// +-x/の位置
			for( int j = 0; j < TYPE_NUM; j++ ) {
				
				int startPosX = ( screenWidth[ i ] - ( typeFontWidth * horizonNum + typeFontWidth * ( horizonNum - 1 ) ) ) / 2;
				int startPosY = mProblemPosY[ i ] + mFontSize32 + mFontSize64;
				
				mTypeFontPosX[ i ][ j ] = startPosX + typeFontWidth * 3 / 2 * ( j / verticalNum );
				mTypeFontPosY[ i ][ j ] = startPosY + typeFontHeight * 3 / 2 * ( j % verticalNum );
				
				mTypeFramePosX[ i ][ j ] = mTypeFontPosX[ i ][ j ] - mFontSize16 / 2;
				mTypeFramePosY[ i ][ j ] = mTypeFontPosY[ i ][ j ] - mFontSize16 / 2;
			}
		}
		mGameTimerPosX[ ORIENTATION_PORTRAIT ] = viewWidth / 2;
		mGameTimerPosX[ ORIENTATION_LANDSCAPE ] = viewHeight / 2;
		
		//--------------------------
		// レベルの位置情報
		//--------------------------
		Point fontSize = UtilityLibrary.getMessageSize(mPaint, mLevelString[ 0 ], mFontSize48);
		mLevelFrameWidth = fontSize.x + mFontSize16;
		mLevelFrameHeight = fontSize.y + mFontSize16;
		
		for( int i = 0; i < ORIENTATION_NUM; i++ ) {
			// 前計算
			int verticalNum = ( int ) Math.sqrt(LEVEL_MAX);
			int horizonNum = LEVEL_MAX / verticalNum;
			for( int j = 0; j < LEVEL_MAX; j++ ) {
				
				if( i == ORIENTATION_PORTRAIT ) {
					mLevelFontPosX[ i ][ j ] = ( screenWidth[ i ] - mLevelFrameWidth ) / 2;
					mLevelFontPosY[ i ][ j ] = mFontSize48 + mFontSize96 * j;
				}
				else {
					int startPosX = ( screenWidth[ i ] - ( mLevelFrameWidth * horizonNum + mLevelFrameWidth * ( horizonNum - 1 ) ) ) / 2;
					mLevelFontPosX[ i ][ j ] = startPosX + mLevelFrameWidth * 3 / 2 * ( j % horizonNum );
					mLevelFontPosY[ i ][ j ] = mFontSize48 + mLevelFrameHeight * 3 / 2 * ( j / horizonNum );
				}
				
				mLevelFramePosX[ i ][ j ] = mLevelFontPosX[ i ][ j ] - mFontSize16 / 2;
				mLevelFramePosY[ i ][ j ] = mLevelFontPosY[ i ][ j ] - mFontSize16 / 2;
			}
		}
		
		//--------------------------
		// 計算情報
		//--------------------------
		mCalcNumberNum[ 0 ] = 5;
		mCalcNumberNum[ 1 ] = 5;
		mCalcNumberNum[ 2 ] = 7;
		mCalcNumberNum[ 3 ] = 7;
		
		mCalcNumberMax[ 0 ] = 10;
		mCalcNumberMax[ 1 ] = 20;
		mCalcNumberMax[ 2 ] = 10;
		mCalcNumberMax[ 3 ] = 20;
	}
	
	//--------------------------
	// 問題作成
	//--------------------------
	private void makeProbrem()
	{
		mCalcNum = 0;
		
		while( true ) {
			
			// 符号から
			calcCalcTypesData();
			for( int i = 0; i < mNumberNum - 1; i++ ) {
				
				if( ( i & 1 ) != 0 ) { continue; }
				
				mNumberData[ i ] = ( mRandom.nextInt(1000000) % ( mNumberMax - 1 ) ) + 1;
			}
			
			// 次に括弧を
			mCalcParentheses = -1;
			for( int i = 0; i < mCalcTypesNum - 1; i++ ) {
				
				if( mNumberData[ i * 2 + 1 ] <= TYPE_MINUS && mCalcTypesNum > 2 ) {
					int rate = mRandom.nextInt(1000000) % PARENTHESESRATE;
					if( rate == 0 ) {
						mCalcParentheses = i;
						break;
					}
				}
			}
			
			// 計算順序
			for( int i = 0; i < mCalcTypesNum - 1; i++ ) {
				
				if( mCalcParentheses == i ) {
					mCalcTypesOrder[ 0 ] = i;
					continue;
				}
				int index = 0;
				for( int j = 0; j < mCalcTypesNum - 1; j++ ) {
					
					if( i == j ) { continue; }
					if( j == mCalcParentheses ) { index++; continue; }
					
					if( j < i ) {
						if( mNumberDataHalf[ j * 2 + 1 ] >= mNumberDataHalf[ i * 2 + 1 ] ) {
							index++;
						}
						continue;
					}
					
					if( mNumberDataHalf[ j * 2 + 1 ] > mNumberDataHalf[ i * 2 + 1 ] ) {
						index++;
					}
				}
				mCalcTypesOrder[ index ] = i;
			}
			
			// 次に妥当性のチェックと計算結果
			for( int i = 0; i < mNumberNum; i++ ) {
				
				mNumberDataTemp[ i ] = mNumberData[ i ];
			}
			
			boolean bRight = true;
			for( int i = 0; i < mCalcTypesNum - 1; i++ ) {
				
				int typesIndex = mCalcTypesOrder[ i ] * 2 + 1;
				int resultData = getCalclationNumber( mNumberDataTemp[ typesIndex - 1 ],
													  mNumberDataTemp[ typesIndex ],
													  mNumberDataTemp[ typesIndex + 1 ]);
				
				if( resultData == ERROR_CALC_NUM ) { bRight = false; break; }
				
				mNumberDataTemp[ typesIndex - 1 ] = resultData;
				for( int j = typesIndex; j < mNumberNum; j++ ) {
					
					int nextData = -1;
					if( j + 2 < NUMBER_MAX ) { nextData = mNumberDataTemp[ j + 2 ]; }
					
					mNumberDataTemp[ j ] = nextData;
				}
				for( int j = i + 1; j < mCalcTypesNum - 1; j++ ) {
					
					if( mCalcTypesOrder[ j ] > mCalcTypesOrder[ i ] ) { mCalcTypesOrder[ j ]--; }
				}
				
			}
			if( bRight && mNumberDataTemp[ 0 ] >= 0 && mNumberDataTemp[ 0 ] < 100 ) { break; }
			
			mCalcNum++;
		}
		
		mNumberData[ mNumberNum - 1 ] = mNumberDataTemp[ 0 ];
		
		int randomData = mRandom.nextInt(1000000);
		int answerIndex = ( randomData % ( mCalcTypesNum - 1 ) ) * 2 + 1;
		
		mAnswer = mNumberData[ answerIndex ];
		mNumberData[ answerIndex ] = TYPE_ANSWER;
	}
	
	//----------------------------------------------------------------------
	// 
	// 計算補助関連
	// 
	//----------------------------------------------------------------------
	//----------------------
	// 計算補助関連
	// 
	// num1 types num2 の結果
	// ただし、ふさわしくないばあいには ERROR_CALC_NUM を返す
	//----------------------
	private int getCalclationNumber( int num1, int types, int num2 )
	{
		int finalAnswer = 0;
		switch( types ) {
		//--------------------------
		// +
		//--------------------------
		case TYPE_PLUS:
			
			if( num2 == 0 ) { return ( ERROR_CALC_NUM ); }
			if( num1 == 2 && num2 == 2 ) { return ( ERROR_CALC_NUM ); }
			
			finalAnswer = num1 + num2;
			break;
		//--------------------------
		// -
		//--------------------------
		case TYPE_MINUS:
			
			if( num2 == 0 ) { return ( ERROR_CALC_NUM ); }
			if( num1 == 4 && num2 == 2 ) { return ( ERROR_CALC_NUM ); }
			
			finalAnswer = num1 - num2;
			break;
		//--------------------------
		// x
		//--------------------------
		case TYPE_TIMES:
			
			if( num1 == 0 ) { return ( ERROR_CALC_NUM ); }
			if( num2 < 2 ) { return ( ERROR_CALC_NUM ); }
			if( num1 == 2 && num2 == 2 ) { return ( ERROR_CALC_NUM ); }
			
			finalAnswer = num1 * num2;
			break;
		//--------------------------
		// ÷
		//--------------------------
		case TYPE_DEVIDE:
			
			if( num1 == 0 ) { return ( ERROR_CALC_NUM ); }
			if( num2 < 2 ) { return ( ERROR_CALC_NUM ); }
			if( num1 % num2 != 0 ) { return ( ERROR_CALC_NUM ); }
			if( num1 == 4 && num2 == 2 ) { return ( ERROR_CALC_NUM ); }
			
			finalAnswer = num1 / num2;
			break;
		}
		return ( finalAnswer );
	}
	
	//--------------------------
	// numberの約数の数を得る
	//--------------------------
	private void calcCalcTypesData()
	{
		mNumberData[ ( mCalcTypesNum - 1 ) * 2 + 1 ] = TYPE_EQUAL;
		
		int typeNum = TYPE_NUM;
		if( mCalcNum > MAKEPROBLEM_CALC_LIMIT ) { typeNum--; }
		
		for( int i = 0; i < mCalcTypesNum - 1; i++ ) {
			
			mNumberData[ i * 2 + 1 ] = mRandom.nextInt(1000000) % typeNum;
			if( mNumberData[ i * 2 + 1 ] < 0 ) { i--; continue; }
			
			mNumberDataHalf[ i * 2 + 1 ] = ( mNumberData[ i * 2 + 1 ] >> 1 );
		}
	}
	
	//--------------------------
	// numberの約数の数を得る
	//--------------------------
	private int getDevidesNumberNum( int number )
	{
		int numberNum = 0;
		for( int i = 1; i <= number; i++ ) {
			
			if( ( number % i ) == 0 ) {
				numberNum++;
			}
		}
		return ( numberNum );
	}
	
	//--------------------------
	// numberの約数を得る
	//--------------------------
	private int getDevidesNumberNumber( int number, int index )
	{
		int numberNum = 0;
		for( int i = 1; i <= number; i++ ) {
			
			if( ( number % i ) == 0 ) {
				if( index == numberNum ) {
					return ( i );
				}
				numberNum++;
			}
		}
		return ( -1 );
	}
	
	//--------------------------
	// ゲームタイムの文字列を得る
	//--------------------------
	/*
	private String getGameTimeString()
	{
		int minutes = mGameTime / ( 60 * GAME_FPS );
		int minutesSub = mGameTime % ( 60 * GAME_FPS );
		int seconds = minutesSub / GAME_FPS;
		int secondsSub = minutesSub % GAME_FPS;
		int seconds2 = secondsSub * 10 / GAME_FPS;
		
		NSString* gameTimeString = [NSString stringWithFormat:@"%02d : %02d : %d", minutes, seconds, seconds2];
		return ( gameTimeString );
	}
	*/
	
	//----------------------------------------------------------------------
	// 
	// サウンド関連
	// 
	//----------------------------------------------------------------------
	//--------------------------
	// サウンド初期化
	//--------------------------
	private void initializeSoundData()
	{
		if( mSeMediaPlayer != null ) { return; }
		
		int [] seData =
		{
			R.raw.affex_0001,
			R.raw.affex_0002,
			R.raw.affex_0001,
			R.raw.affex_0002,
		};
		
		mSeMediaPlayer = new MediaPlayer[SE_NUM];
		for( int i = 0; i < SE_NUM; i++ ) {
			
			mSeMediaPlayer[i] = MediaPlayer.create(getContext(),seData[i]);
		}
	}
	
	//--------------------------
	// サウンド再生
	//--------------------------
	private void callSoundSe(int index )
	{
		mSeMediaPlayer[index].start();
		mSeMediaPlayer[index].setOnCompletionListener(this);
	}
	
	//--------------------------
	// サウンド再生終了時に呼ばれる
	//--------------------------
	public void onCompletion(MediaPlayer mediaPlayer)
	{
		//mediaPlayer.stop();
		//mediaPlayer.setOnCompletionListener(null);
	}
}

//======================================================================
