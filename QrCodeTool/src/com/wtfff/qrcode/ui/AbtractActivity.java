package com.wtfff.qrcode.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class AbtractActivity extends Activity{
	protected int imageM_size=0;
	@Override 
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenH = dm.heightPixels;
		int screenW = dm.widthPixels;
		if(screenH>screenW)
			imageM_size = Math.round(screenW/3*2);
		else
			imageM_size = Math.round(screenH/3*2);
    }

}
