package com.pm.plantcloudatlas;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.graphics.Bitmap;



public class DcActivity extends Activity {

	static ProgressWebView wv;
	static String url;
	private long timeout = 10000;
	private Handler mHandler = new Handler();
	private Timer timer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dc);

		wv = (ProgressWebView) findViewById(R.id.webView1);

		//�Զ���Ӧ��Ļ
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setLoadWithOverviewMode(true);

		//֧�� Javascript
		wv.getSettings().setJavaScriptEnabled(true);  
		
		//��ʹ�û���
		wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		//�õ�����
		wv.requestFocus();




		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl); 
				DcActivity.loadoptionurl("file:///android_asset/nonet.html");
			}
		});
	}

	public static void loadoptionurl(String optionurl) {
		wv.loadUrl(optionurl);
	}

	public static void loadurl() {
		wv.loadUrl(url);
	}

	public static void stoploadurl() {
		wv.stopLoading();
	}

	public static void goback() {
		wv.goBack();
	}

	public static void setUrl(String inputurl) {
		url = inputurl; 
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			// �����˳��Ի���
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// ���öԻ������
			isExit.setTitle("ϵͳ��ʾ");
			// ���öԻ�����Ϣ
			isExit.setMessage("ȷ��Ҫ�˳���");
			// ���ѡ��ť��ע�����
			isExit.setButton("ȷ��", listener);
			isExit.setButton2("ȡ��", listener);
			// ��ʾ�Ի���
			isExit.show();

		}

		return false;

	}
	/**�����Ի��������button����¼�*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				break;
			default:
				break;
			}
		}
	};	
}
