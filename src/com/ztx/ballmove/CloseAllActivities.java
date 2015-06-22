package com.ztx.ballmove;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
   

   //此类用于关闭所有Activity
public class CloseAllActivities extends Application{
	private List<Activity> mList = new LinkedList<Activity>();  
    //为了实现每次使用该类时不创建新的对象而创建的静态对象  
    private static CloseAllActivities instance;   
    //构造方法  
    private CloseAllActivities(){}  
    //实例化一次  
    public synchronized static CloseAllActivities getInstance(){   
        if (null == instance) {   
            instance = new CloseAllActivities();   
        }   
        return instance;   
    }   
    // add Activity    
    public void addActivity(Activity activity) {   
        mList.add(activity);   
    }   
    //关闭每一个list内的activity  
    public void exit() {   
        try {   
            for (Activity activity:mList) {   
                if (activity != null)   
                    activity.finish();   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            System.exit(0);   
        }   
    }   
    //杀进程  
    public void onLowMemory() {   
        super.onLowMemory();       
        System.gc();   
    }    

}
