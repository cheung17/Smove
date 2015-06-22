package com.ztx.ballmove;




import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
    //此类为呈现游戏进行的activity
public class MainActivity extends Activity {
     static TextView score;
	 static TextView best;
     static SharedPreferences sf;
     static int myBest,myscore;
     static Editor editor ;
     
     static boolean isSilent=false;
     ImageView notSilent,silent;
	 static int dies;
     
     public static MainActivity mainActivity=null;
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         score=(TextView) findViewById(R.id.score);
         best=(TextView) findViewById(R.id.best);
         score.setText("0");
         CloseAllActivities.getInstance().addActivity(this);
         sf=getSharedPreferences("best_score",MODE_PRIVATE);
         editor =sf.edit();
         myBest=sf.getInt("best",0);
         dies=sf.getInt("dies",0);
        // Toast.makeText(this, dies+"", 1).show();
         //setBest(myBest);
         best.setText(myBest+"");
         silent=(ImageView) findViewById(R.id.bubofang);
         notSilent=(ImageView) findViewById(R.id.bofang);
         notSilent.setVisibility(View.VISIBLE);//显示
         silent.setVisibility(View.GONE);//隐藏
         MySurfaceView.surfaceIsLive=true;
      
        
         silent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				silent.setVisibility(View.GONE); //隐藏静音
				notSilent.setVisibility(View.VISIBLE);//显示音量图标
			    isSilent=false;	 //可以播放音效
			}
		});
         notSilent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			  silent.setVisibility(View.VISIBLE);
			  notSilent.setVisibility(View.GONE);
			  isSilent=true;			 	
			}
		});
     }
     
     
     public MainActivity(){
 		mainActivity=this;      //构造方法
 		
 	} 
     public static MainActivity getMainActivity() {    //生成get方法
 		return mainActivity;  
 	}
	public static void setScore(int s) {
		score.setText(s+"");
		
		if(s>myBest){
			setBest(s);
			//得分
			//myBest=s;
		}
		
	}
	
	
	public static void setBest(int b) {
		myBest=b;
		editor.putInt("best",b);
		editor.commit();
		//best.setText(""+b);		
	} 
	
	private long firstTime = 0;  
	   @Override  
	public boolean onKeyUp(int keyCode, KeyEvent event) {  
	       // TODO Auto-generated method stub  
	       switch(keyCode)  
	       {  
	       case KeyEvent.KEYCODE_BACK:  
	            long secondTime = System.currentTimeMillis();   
	             if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出  
	                 Toast.makeText(this, "再按一次退出游戏", Toast.LENGTH_SHORT).show();   
	                 firstTime = secondTime;//更新firstTime  
	                 return true;   
	             } else {                                                    //两次按键小于2秒时，退出应用  
	            //System.exit(0);
	            CloseAllActivities.getInstance().exit(); //退出整个应用
	           // MainActivity.getMainActivity().g
	             }   
	           break;  
	       }  
	     return super.onKeyUp(keyCode, event);  
	   }  
	 public void saveDies(int d) {		 
		editor.putInt("dies", d);		
		editor.commit();
		dies=d;
	}
	 protected void onPause() {  //home键 调用 
			// TODO Auto-generated method stub
			super.onPause();
			MySurfaceView.surfaceIsLive=false;
		}
	   @Override
	protected void onStop() {
		super.onStop();
		MySurfaceView.surfaceIsLive=false;
	}
	   @Override
	protected void onResume() {
		super.onResume();
		MySurfaceView.surfaceIsLive=true;
		
	}
	   @Override
	protected void onStart() {
		
		super.onStart();
		
		MySurfaceView.surfaceIsLive=true;
		
	}
	   @Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		MySurfaceView.surfaceIsLive=true;
	}
	
   
}

