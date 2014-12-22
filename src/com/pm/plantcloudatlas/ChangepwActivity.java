package com.pm.plantcloudatlas;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class ChangepwActivity extends Activity {
	private static String userid = "";
	private EditText et_ymm, et_xmm, et_xmmqr;
	private String loginServletURL = "";
	private int loginCheckResult = 0;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.changepw);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin); 
		
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		
		et_ymm = (EditText) findViewById(R.id.ymm);
		et_xmm = (EditText) findViewById(R.id.xmm);
		et_xmmqr = (EditText) findViewById(R.id.xmmqr);
	}
	
	
	public void changepassword(View source) {
		if ("".equals(et_ymm.getText().toString()) || "".equals(et_xmm.getText().toString()) || "".equals(et_xmmqr.getText().toString())) {
			Dialog.showDialog("����", "���������Ϊ�գ�\n���������룡", ChangepwActivity.this);
		} else if (et_xmm.getText().toString().equals(et_xmmqr.getText().toString()) == false){
			Dialog.showDialog("����", "������д�����벻һ�£�\n����ȷ���룡", ChangepwActivity.this);
		} else {
			int loginState = checkLogin(et_ymm.getText().toString(), et_xmm.getText().toString());
			
			if (loginState == 1) {
				AlertDialog isExit = new AlertDialog.Builder(this).create();
				isExit.setTitle("�޸ĳɹ�");
				isExit.setMessage("����ע�����µ�¼��\n�Ƿ�Ҫע����");
				isExit.setButton("ע��", listener);
				isExit.setButton2("��", listener);
				isExit.show();
			} else if (loginState == 2) {
				Dialog.showDialog("�޸�ʧ��", "ԭ���벻��ȷ��\n������������룡", ChangepwActivity.this);
			} else if (loginState == 3) {
				Dialog.showDialog("ϵͳ��ʾ", "������û����Ӧ��\n��������״̬��", ChangepwActivity.this);
			} else if (loginState == 4) {
				Dialog.showDialog("ϵͳ��ʾ", "��¼�쳣��\n��������״̬��", ChangepwActivity.this);
			} else if (loginState == 5) {
				Dialog.showDialog("ϵͳ��ʾ", "��¼��ʱ��\n��������״̬��", ChangepwActivity.this);
			}
		}
	}
	

	public int checkLogin(String ymm, String xmm) {
		loginCheckResult = 0;
		loginServletURL ="http://192.168.1.105:8080/PlantCloudAtlasAppWebpub/ChangepwServlet";   
		loginServletURL +="?yhid=" + userid + "&ymm=" + ymm + "&xmm=" + xmm;

		new Thread() {
			public void run() {
				try {
					//����servlet��doget����
					HttpGet request = new HttpGet(loginServletURL);

					//������ִ������,����url������ȡ��Ӧ
					HttpResponse response = new DefaultHttpClient().execute(request); 

					//��ȡ������,����200����ʾ���ӳɹ�,�������Ӧ
					if(response.getStatusLine().getStatusCode() == 200) {
						//��ȡ��Ӧ�е�����
						String result= EntityUtils.toString(response.getEntity());

						if (Integer.parseInt(result) == 1) {
							loginCheckResult = 1;
						} else if (Integer.parseInt(result) == 0) {
							loginCheckResult = 2;
						}
					}else {
						loginCheckResult = 3;
					}
				} catch (Exception e) {
					loginCheckResult = 4;
				}
			}
		}.start();	

		int s = 0;
		while (true) {
			if (s >= 20) {
				loginCheckResult = 5;
				break;
			}
			if (loginCheckResult != 0)
				break;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s ++;
		}
		return loginCheckResult;

	}
	
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				Editor editor = sp.edit();
				editor.putString("USER_NAME", null);
				editor.putString("PASSWORD", null);
				editor.putBoolean("ISCHECK", false);
				editor.putBoolean("AUTO_ISCHECK", false);
				editor.commit();

				Intent intent = new Intent();
				intent.setClass(ChangepwActivity.this,LoginActivity.class);
				startActivity(intent);

				MainActivity.getInstance().finish();
				SettingActivity.getInstance().finish();
				ChangepwActivity.this.finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				ChangepwActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};	
	
	public static String getUserid() {
		return userid;
	}


	public static void setUserid(String userid) {
		ChangepwActivity.userid = userid;
	}
}
