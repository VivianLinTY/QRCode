package com.wtfff.qrcode.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.wtfff.qrcodetool.R;

public class EncodeActivity extends AbtractActivity{
	
	EditText user_enter_edt;
	Button btn_sure,btn_close,btn_back,btn_save;
	ImageView qrcode_img;
    ImageView bar_img;
    Dialog mDialog;
    Bitmap qrcode,barcode;
    String content="";
 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_encode);
		user_enter_edt=(EditText) findViewById(R.id.user_enter_edt);
		btn_sure=(Button) findViewById(R.id.btn_sure);
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				content = user_enter_edt.getText().toString();
				if(content!=null && !content.contentEquals(""))
				{
					try
					{
						qrcode=encodeAsBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
						barcode=encodeAsBitmap(content, BarcodeFormat.CODE_39, 400, 70);
					}
					catch (WriterException e)
					{
					    e.printStackTrace();
					}
					catch (IllegalArgumentException e)
					{
					    e.printStackTrace();
					}
					catch (Exception e)
					{
					    e.printStackTrace();
					}
					qrcode_img.setImageBitmap(qrcode);
					bar_img.setImageBitmap(barcode);
					if(qrcode!=null || barcode!=null)
						mDialog.show();
				}
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
		mDialog=new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.setContentView(R.layout.dialog_encode);
		qrcode_img=(ImageView) mDialog.findViewById(R.id.qrcode_img);
		bar_img=(ImageView) mDialog.findViewById(R.id.bar_img);
		btn_close=(Button) mDialog.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				if(qrcode!=null)
					qrcode.recycle();
				if(barcode!=null)
					barcode.recycle();
				qrcode=null;
				barcode=null;
			}
		});
		btn_save=(Button) mDialog.findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				if(qrcode_img!=null)
				{
					try {
						SaveImage(qrcode,"/sdcard/"+content+"_qrcode.png");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					qrcode.recycle();
				}
				if(barcode!=null)
				{
					try {
						SaveImage(barcode,"/sdcard/"+content+"_barcode.png");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					barcode.recycle();
				}
				qrcode=null;
				barcode=null;
			}
		});
    }
 
    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) throws WriterException
    {
		if (contents.length() == 0) return null;
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;
		HashMap<EncodeHintType, String> hints = null;
		String encoding = null;
		for (int i = 0; i < contents.length(); i++)
		{
		    if (contents.charAt(i) > 0xFF)
		    {
				encoding = "UTF-8";
				break;
		    }
		}
		if (encoding != null)
		{
		    hints = new HashMap<EncodeHintType, String>(2);
		    hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(contents, format, desiredWidth, desiredHeight, hints);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++)
		{
		    int offset = y * width;
		    for (int x = 0; x < width; x++)
		    {
		    	pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
		    }
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
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
