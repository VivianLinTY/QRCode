package com.wtfff.qrcode.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.wtfff.qrcodetool.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class DecodeActivity extends AbtractActivity implements SurfaceHolder.Callback, Camera.PreviewCallback{
	
//	private TextView bar_tv;
	private EditText qrcode_tv;
	private Button btn_scan,btn_back;
	private SurfaceView scanner;
    private SurfaceHolder surfaceHolder;
    private Camera myCamera;
    private boolean isCameraOpen;
    private Bitmap image;
    private RelativeLayout scanner_layout;
    private ImageView scanner_mask=null;
    private static final int TAG_SET_QRCODE=0;
    private static final int TAG_CLEAR_QRCODE=1;
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    private String code = "";
    private Runnable autofs;
    
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
          switch(msg.what)
          {
           		case TAG_SET_QRCODE:
           			qrcode_tv.setText(code);
           			break;
           		case TAG_CLEAR_QRCODE:
           			qrcode_tv.setText("");
           			break;
          }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_decode);
		qrcode_tv=(EditText) findViewById(R.id.qrcode_tv);
		scanner_layout=(RelativeLayout) findViewById(R.id.scanner_layout);
//		bar_tv=(TextView) findViewById(R.id.bar_tv);
		btn_scan=(Button) findViewById(R.id.btn_scan);
		btn_scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				code="";
				Message msg = new Message();
          	 	msg.what = TAG_CLEAR_QRCODE;
          	 	mHandler.sendMessage(msg);
				mHandler.post(autofs);
			}
		});
		btn_back=(Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
//		try
//		{
//		    String barString = decode(getBitmapFromImageView(bar_img));
//		    String qrString = decode(getBitmapFromImageView(qrcode_img));
//		    qrcode_tv.setText(qrString);
//		    bar_tv.setText(barString);
//		}
//		catch (WriterException e)
//		{
//		    e.printStackTrace();
//		}
		findControl();
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	mHandler.removeCallbacks(autofs);
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	mHandler.removeCallbacks(autofs);
    	if(myCamera!=null)
    	{
	    	myCamera.stopPreview();
			isCameraOpen = false;
			myCamera.release();
			myCamera = null;
    	}
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	mHandler.post(autofs);
    }
    
    private void findControl()
    {
    	scanner = (SurfaceView) findViewById(R.id.scanner);
    	surfaceHolder = scanner.getHolder();
    	surfaceHolder.addCallback(this);
    	surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
	{
		Camera.Parameters parameters = myCamera.getParameters();
//		parameters.setFocusMode("auto");
		myCamera.setParameters(parameters);
		myCamera.startPreview();
		myCamera.autoFocus(mAutoFocusCallback);
		isCameraOpen = true;
		autofs = new Runnable(){
			public void run() {
	        // TODO Auto-generated method stub
				if(code!=null && !code.contentEquals(""))
				{
					mHandler.removeCallbacks(autofs);
				}
				else
				{
					myCamera.autoFocus(mAutoFocusCallback);
//					if (isCameraOpen) {
//						myCamera.setOneShotPreviewCallback(DecodeActivity.this);
//					}
					mHandler.postDelayed(autofs,3000);
				}
			}
		};
		mHandler.postDelayed(autofs,3000);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		try
		{
			myCamera = Camera.open();
			myCamera.setPreviewDisplay(surfaceHolder);
			//鏡頭的方向和手機相差90度，所以要轉向
			myCamera.setDisplayOrientation(90);

		}
		catch (IOException e)
		{
			myCamera.release();
			myCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		myCamera.stopPreview();
		isCameraOpen = false;
		myCamera.release();
		myCamera = null;
	}
	
	private final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {
		public void onAutoFocus(boolean focused, Camera camera) {
			if (focused && isCameraOpen) {
				camera.setOneShotPreviewCallback(DecodeActivity.this);
			}
		}
	};
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		if (data != null)
		{
			Camera.Parameters parameters = camera.getParameters();
			int imageFormat = parameters.getPreviewFormat();

			if (imageFormat == ImageFormat.NV21)
			{
				// get full picture
				image = null;
				int w = parameters.getPreviewSize().width;
				int h = parameters.getPreviewSize().height;
				  
				Rect rect = new Rect(0, 0, w, h); 
				YuvImage img = new YuvImage(data, ImageFormat.NV21, w, h, null);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				  
				if (img.compressToJpeg(rect, 100, baos)) 
				{ 
					Log.d("vivian", "compressToJpeg");
					image =  BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
					int height=image.getHeight();
					int width=image.getWidth();
					if(width>0 && height>0)
					{
						int image_half=0;
						if(height>width)
							image_half=Math.round(width/3);
						else
							image_half=Math.round(height/3);
						int image_size=image_half*2;
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(imageM_size,imageM_size);
						lp.addRule(RelativeLayout.CENTER_IN_PARENT);
						scanner_mask=new ImageView(DecodeActivity.this);
						scanner_mask.setBackgroundDrawable(getResources().getDrawable(R.drawable.mask));
						scanner_mask.setLayoutParams(lp);
						scanner_layout.addView(scanner_mask);
						image = Bitmap.createBitmap(image,width/2-image_half, height/2-image_half,image_size,image_size);
//						try {
//							SaveImage(image,"/sdcard/test_crop.png");
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						if(image!=null)
						{
							code = decode(image);
							if(code!=null && !code.contentEquals(""))
							{
								Message msg = new Message();
		  	            	 	msg.what = TAG_SET_QRCODE;
		  	            	 	mHandler.sendMessage(msg);
							}
						}
					}
				}
		
			}
		}
	}
//    
//    private Bitmap getBitmapFromImageView(ImageView v)
//    {
//		final BitmapDrawable bitmapDrawable = (BitmapDrawable) v.getDrawable();
//		return bitmapDrawable.getBitmap();
//    }
    
    private String decode(Bitmap bMap)
    {
		String contents = null;
		int w = bMap.getWidth();
		int h = bMap.getHeight();
		int[] intArray = new int[w * h];
		//copy pixel data from the Bitmap into the 'intArray' array  
		bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
		LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		// use this otherwise ChecksumException
		Reader reader = new MultiFormatReader();
		try
		{
		    Result result = reader.decode(bitmap);
		    contents = result.getText();
		}
		catch (NotFoundException e)
		{
		    e.printStackTrace();
		}
		catch (ChecksumException e)
		{
		    e.printStackTrace();
		}
		catch (FormatException e)
		{
		    e.printStackTrace();
		}
		Log.d("vivian", "contents="+contents);
		return contents;
    }
    
    private void SaveImage(Bitmap input, String file_path) throws IOException {
    	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    	input.compress(Bitmap.CompressFormat.PNG, 100, bytes);
		try {
			FileOutputStream out = new FileOutputStream(file_path);
			out.write(bytes.toByteArray());
			out.flush();
			out.close();
		} catch (Exception e) {  
			e.printStackTrace();  
		}
	}
    

}
