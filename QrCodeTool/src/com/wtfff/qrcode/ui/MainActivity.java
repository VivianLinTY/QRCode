package com.wtfff.qrcode.ui;

import com.wtfff.qrcodetool.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AbtractActivity{
	Button btn_encoder;
	Button btn_decoder;
 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		btn_encoder=(Button) findViewById(R.id.btn_encoder);
		btn_encoder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent encoder = new Intent();
				encoder.setClass(MainActivity.this, EncodeActivity.class);  
                startActivity(encoder);
			}
		});
		btn_decoder=(Button) findViewById(R.id.btn_decoder);
		btn_decoder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent decoder = new Intent();
				decoder.setClass(MainActivity.this, DecodeActivity.class);  
                startActivity(decoder);
			}
		});
		
    }
}
