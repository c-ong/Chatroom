package com.lukasz.chat.surface;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.lukasz.chat.Main;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class Panel extends GLSurfaceView implements SurfaceHolder.Callback
{
	public static float mWidth;
	public static float mHeight;
	private ViewThread mThread;
	private ArrayList<Element>mElements = new ArrayList<Element>();

	private int xpos;
	private int ypos;
	private boolean grabed = false;
	private boolean type = false;
	private boolean moving = false;
	
	private Main main;
	
	public Panel(Context context,AttributeSet attrs)
	{
		super(context,attrs);
		getHolder().addCallback(this);
		mThread = new ViewThread(this);
	}
		
	public void doDraw(long elapse, Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);
		synchronized(mElements)
		{
			for(Element element : mElements)
			{
				element.doDraw(canvas);
			}
		}
	}

	public void moveAll(float x, float y, float z)
	{
		for(Element element : mElements)
		{
			element.sensMove(x, y, z);
		}
	}
	
	public void animate(long elapsedTime)
	{
		synchronized(mElements)
		{
			for(int i=0;i<mElements.size();i++)
			{
				if(moving == false)
				{
					mElements.get(i).pos(xpos,ypos);
					mElements.get(i).animate(elapsedTime);
				}
				else
				{
					mElements.get(i).pos(xpos,ypos);
				}
			}	
			
			
		}
	}
	
	public void change()
	{
		if(type == true)
		{
			type = false;
		}
		else
		{
			type = true;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int index = 0;
		
		synchronized(mElements)
		{
			int action = event.getAction();
				xpos = (int) event.getX();
				ypos = (int) event.getY();
				
				switch(action)
				{
				   case MotionEvent.ACTION_DOWN:
					   
					   int len = mElements.size();
					   
					   for(int i = 0;i < len;i++)
					   {
						   if(grabed == false)
						   {
							   if((int) event.getX() < mElements.get(i).getXpos() + mElements.get(i).getW() && (int) event.getX() > mElements.get(i).getXpos())
							   {
								   if((int) event.getY() < mElements.get(i).getYpos() + mElements.get(i).getH() && (int) event.getY() > mElements.get(i).getYpos())
								   {										
									   mElements.get(i).move = true;		
									   index = i;
									   grabed = true;
									   moving = false;
								   }
							   }
							   else
							   {
								   if(moving == false)
								   {
									   mElements.get(i).pos(xpos,ypos);
									   mElements.get(i).countDif();
								   }								   								   
							   }
							   
							   
						   }
					   }
					   
					   if(grabed == false)
					   {
						   mElements.add(new Element(getResources(), (int) event.getX(), (int) event.getY()));
						   main.addObject(event.getX(), event.getY());
						   moving = true;
					   }
					   
				   break;
				   case MotionEvent.ACTION_UP:
					   grabed = false;
					   moving = false;
					   
					   int ln = mElements.size();
					   
					   main.sendMove(index,mElements.get(index).getXpos(),mElements.get(index).getYpos());
					   
					   for(int j = 0;j < ln;j++)
				   	   {				   		  
						   mElements.get(j).move = false; 
				   	   }
				   break;
				}				
			}		
		return true;
	}
	
	public void addNew(String data)
	{
		try
		{
			JSONObject d = new JSONObject(data);
			mElements.add(new Element(getResources(), d.getInt("x"), d.getInt("y")));
		}
		catch(JSONException e)
		{
			Log.e("JSON","error " + e.toString());
		}
	}
	
	public void moveObject(String data) 
	{
		try
		{
			JSONObject d = new JSONObject(data);
			mElements.get(d.getInt("i")).goTO(d.getInt("x"), d.getInt("y"));
		}
		catch(JSONException e)
		{
			Log.e("JSON","error " + e.toString());
		}		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) 
	{
		mWidth = width;
		mHeight = height;					
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		setEGLContextClientVersion(2);
		
		if(!mThread.isAlive())
		{
			mThread = new ViewThread(this);
			mThread.setRunning(true);
			mThread.start();						
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		if(mThread.isAlive())
		{
			mThread.setRunning(false);
		}		
	}
	
	public void destroy()
	{
		this.destroy();
		mThread.setRunning(false);
	}

	public void setMain(Main m) 
	{
		this.main = m;
	}
}
