package com.lukasz.chat;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MessageReceiver 
{
	private CustomHandler myHandler;
	private String message;
	private String name;
	private LayoutInflater inflater;
	private Animation a;
	
	private Main main;
	
	public MessageReceiver(Main ac, LayoutInflater i,ScrollView chatScroll,LinearLayout chatList)
	{
		this.inflater = i;
		this.main = ac;
		a = AnimationUtils.loadAnimation(ac.getBaseContext(), R.anim.in);
		myHandler = new CustomHandler();		
	}
	
	public void onMessageReceive(String event,String data)
	{		
		if(event.compareToIgnoreCase("new_message") == 0)
		{
			try
			{
				JSONObject d = new JSONObject(data);
				name = d.getString("name");
				message = d.getString("message");
				myHandler.sendEmptyMessage(0);
			}
			catch(JSONException e)
			{
				Log.e("JSON","error " + e.toString());
			}
		}
		else if(event.compareToIgnoreCase("client-new_message") == 0)
		{
			try
			{
				JSONObject d = new JSONObject(data);
				name = d.getString("name");
				message = d.getString("message");
				myHandler.sendEmptyMessage(0);
			}
			catch(JSONException e)
			{
				Log.e("JSON","error " + e.toString());
			}
		}
	}
	
	class CustomHandler extends Handler
	{
	    @Override
	    public void handleMessage(Message msg) 
	    {
	        super.handleMessage(msg);
	        addMessage(name,message,"#efefef");
	    }
	}
	
	
	public void addMessage(String name, String mess, String colour)
	{
		View chatMessage = inflater.inflate(R.layout.text, null);
		
		Date date = new Date();
		
		RelativeLayout back = (RelativeLayout)chatMessage.findViewById(R.id.back);
		back.setBackgroundColor(Color.parseColor(colour));
		
		TextView text = (TextView)chatMessage.findViewById(R.id.name);
		text.setText(name+":");
		
		int hour = date.getHours();
		String h = String.valueOf(hour);
		if(hour<10)
		{
			h = "0"+h;
		}
		
		int min = date.getMinutes();
		String m = String.valueOf(min);
		if(min<10)
		{
			m = "0"+m;
		}
		
		int sec = date.getSeconds();
		String s = String.valueOf(sec);
		if(sec<10)
		{
			s = "0"+s;
		}
		
		text = (TextView)chatMessage.findViewById(R.id.time);		
		text.setText(h+":"+m +":"+s);
		
		text = (TextView)chatMessage.findViewById(R.id.message);
		text.setText(mess);
		
		main.addView(chatMessage);
		chatMessage.startAnimation(a);
	}
}
