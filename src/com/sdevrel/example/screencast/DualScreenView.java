package com.sdevrel.example.screencast;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


public class DualScreenView extends SurfaceView implements Callback
{	
    private String TAG = "DualScreenView";
    
	private MainDrawThread m_Thread = null;
	private SurfaceHolder m_Holder = null;
	private Context m_Context = null;
	private boolean m_bThreadStartFlag = false;
	
	private int nWidth = 0;
	private int nHeight = 0;
	private String mDisplayText = "0x0";
	private int nTxtPosY = 0;
	private int nTxtPosX = 0;
	private int nIntervalCount = 0;
	
	private int nTextSize = 100;
	private int nTextMovDist = 30;
	
	public DualScreenView(Context context, AttributeSet attr)
	{
		super(context, attr);
		
		m_Holder = getHolder();
		m_Holder.setFormat(PixelFormat.RGBA_8888);
		m_Context = context;
		
		m_Thread = new MainDrawThread(m_Holder, m_Context);
		
		m_Holder.addCallback(this);
		
		setFocusable(true);
	}
	
	private void initViewSize(int nWidth, int nHeight)
	{
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		
		nTxtPosY = nHeight/2 + nTextSize/4;
		nTxtPosX = 0;
		
		mDisplayText = String.format("%dx%d", nWidth, nHeight);
		
		Log.d(TAG, "Surface Size " + "width - " + nWidth + " height - " + nHeight);
		
		nIntervalCount = 0;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Log.d(TAG, "Surface Changed!!");
		initViewSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.d(TAG, "Surface Created");
		
		initViewSize(getWidth(), getHeight());
		
		if (m_Thread == null)
		{
			m_Thread = new MainDrawThread(m_Holder, m_Context);
			m_bThreadStartFlag = true;
			m_Thread.start();			
			return;
		}

		if ( !m_bThreadStartFlag)
		{
			m_bThreadStartFlag = true;
			m_Thread.start();
		}
		
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "Surface Destroyed!!");
		
		if (m_Thread != null)
			m_Thread.StopThread();
	}
	
	class MainDrawThread extends Thread
	{

		SurfaceHolder m_Holder;
		Context m_Context;
		
		Paint mTxtPaint;
		Paint mTxtTimePaint;
		
		Paint mRectBGPaint;
		Paint mRectBoardPaint;
		
		boolean bForwardFlag = true;
		
		int nStrokeWidth = 20;

		public MainDrawThread(SurfaceHolder holder, Context context)
		{
			m_Holder = holder;
			m_Context = context;
			
			mTxtPaint = new Paint();
			mTxtPaint.setTextSize(nTextSize);
			mTxtPaint.setTextAlign(Align.CENTER);
			mTxtPaint.setAntiAlias(true);
			
			mTxtPaint.setColor(Color.BLACK);		
			
			mTxtTimePaint = new Paint();
			mTxtTimePaint.setTextSize(nTextSize/1.2f);
			mTxtTimePaint.setTextAlign(Align.LEFT);
			mTxtTimePaint.setAntiAlias(true);			
			mTxtTimePaint.setColor(Color.BLACK);
			
			mRectBoardPaint = new Paint();
			mRectBoardPaint.setColor(Color.WHITE);
			mRectBoardPaint.setStrokeWidth(nStrokeWidth);
			
			mRectBGPaint = new Paint();
			mRectBGPaint.setColor(Color.GREEN);
			mRectBGPaint.setStrokeWidth(0);
		}

		public void StopThread()
		{
			if (m_Thread != null)
			{				
				m_Thread = null;
				m_bThreadStartFlag = false;
			}
		}

		public void DrawAll(Canvas canvas)
		{
			canvas.drawRect(new Rect(0, 0, nWidth, nHeight), mRectBoardPaint);
			canvas.drawRect(new Rect(nStrokeWidth, nStrokeWidth, nWidth-nStrokeWidth, nHeight-nStrokeWidth), mRectBGPaint);
			
			if(nIntervalCount++ == 20)
			{
				if(nTxtPosX >= nWidth)
				{
					nTxtPosX -= nTextMovDist;
					bForwardFlag = false;
				}
				else if(nTxtPosX <= 0)
				{
					nTxtPosX += nTextMovDist;
					bForwardFlag = true;
				}
				else
				{
					if(bForwardFlag == true)
						nTxtPosX += nTextMovDist;
					else
						nTxtPosX -= nTextMovDist;
				}
				
				nIntervalCount = 0;
			}								
			
			canvas.drawText(mDisplayText, nTxtPosX, nTxtPosY, mTxtPaint);
			
		}

		public void run()
		{			
			try
			{
				Canvas canvas = null;
				
				while (m_Thread != null && m_bThreadStartFlag)
				{	
					canvas = m_Holder.lockCanvas();
					
					try
					{	
						synchronized (m_Holder)
						{
							if (canvas != null)
								DrawAll(canvas);
						}
					}
					finally
					{
						if (canvas != null)
							m_Holder.unlockCanvasAndPost(canvas);
					}
				}
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
		}

	};
}
