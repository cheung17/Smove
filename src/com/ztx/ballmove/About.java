package com.ztx.ballmove;

import com.ztx.ballmove.*;  

import android.app.Activity;
import android.os.Bundle;
               //此类为关于游戏activity
public class About extends Activity{
		   @Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
            setContentView(R.layout.about_xml);
            CloseAllActivities.getInstance().addActivity(this);
		}
}
