package com.pm.plantcloudatlas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LoginActivity extends Activity {

	private EditText mUser; // �ʺű༭��
	private EditText mPassword; // ����༭��
	private String loginServletURL = "";
	private int loginCheckResult = 0;
	private CheckBox rem_pw;
	private CheckBox auto_login;
	private SharedPreferences sp;
	private String userid = ""; 

	final private String FGS_URL = "http://192.168.1.105:8080/PlantCloudAtlasAppWebpub/fdzl.jsp?yhid=";
	final private String DC_URL = "http://192.168.1.105:8080/PlantCloudAtlasAppWebpub/fdxx.jsp?yhid=";
	final private String JZ_URL = "http://192.168.1.105:8080/PlantCloudAtlasAppWebpub/fdtj.jsp?yhid=";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin); 



		//���ʵ������
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		mUser = (EditText)findViewById(R.id.login_user_edit);
		mPassword = (EditText)findViewById(R.id.login_passwd_edit);
		rem_pw = (CheckBox) findViewById(R.id.isjz);
		auto_login = (CheckBox) findViewById(R.id.iszd);





		// �жϼ�ס�����ѡ���״̬
		if (sp.getBoolean("ISCHECK", false)) {
			// ����Ĭ���Ǽ�¼����״̬
			rem_pw.setChecked(true);
			mUser.setText(sp.getString("USER_NAME", ""));
			mPassword.setText(sp.getString("PASSWORD", ""));
			// �ж��Զ���½��ѡ��״̬
			if (sp.getBoolean("AUTO_ISCHECK", false)) {
				// ����Ĭ�����Զ���¼״̬
				auto_login.setChecked(true);
				// ��ת����
				login_mainweixin(null);
			}
		}



		//������ס�����ѡ��ť�¼�
		rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (rem_pw.isChecked()) {
					System.out.println("��ס������ѡ��");
					sp.edit().putBoolean("ISCHECK", true).commit();
				}else {
					System.out.println("��ס����û��ѡ��");
					sp.edit().putBoolean("ISCHECK", false).commit();
					auto_login.setChecked(false);
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}

			}
		});

		//�����Զ���¼��ѡ���¼�
		auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (auto_login.isChecked()) {
					System.out.println("�Զ���¼��ѡ��");
					sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
					rem_pw.setChecked(true);
					sp.edit().putBoolean("ISCHECK", true).commit();

				} else {
					System.out.println("�Զ���¼û��ѡ��");
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}
			}
		});
	}

	public void login_mainweixin(View v) {
		if (isConnectInternet() == true) {
			if ("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString())) {
				Dialog.showDialog("��¼����", "�û��������벻��Ϊ�գ�\n��������ٵ�¼��", LoginActivity.this);
			} else {
				int loginState = checkLogin(mUser.getText().toString(), mPassword.getText().toString());

				if (loginState == 1) {
					if (rem_pw.isChecked()) {
						// ��ס�û��������롢
						Editor editor = sp.edit();
						editor.putString("USER_NAME", mUser.getText().toString());
						editor.putString("PASSWORD", mPassword.getText().toString());
						editor.commit();
					}

					userid = mUser.getText().toString();
					
					
					UserinfoActivity.setUserid(userid);
					ChangepwActivity.setUserid(userid);
					
					
					FgsActivity.setUrl(FGS_URL + userid);
					DcActivity.setUrl(DC_URL + userid);
					JzActivity.setUrl(JZ_URL + userid);


					Intent intent = new Intent();
					intent.setClass(LoginActivity.this,LoadingActivity.class);
					startActivity(intent);
					this.finish();
				} else if (loginState == 2) {
					Dialog.showDialog("��¼ʧ��", "�û��������벻��ȷ��\n������������룡", LoginActivity.this);
				} else if (loginState == 3) {
					Dialog.showDialog("ϵͳ��ʾ", "��������ʧ�ܣ�\n��������״̬��", LoginActivity.this);
				} else if (loginState == 4) {
					Dialog.showDialog("ϵͳ��ʾ", "��¼�쳣��\n��������״̬��", LoginActivity.this);
				} else if (loginState == 5) {
					Dialog.showDialog("ϵͳ��ʾ", "��¼��ʱ��\n��������״̬��", LoginActivity.this);
				}
			}
		} else {
			Dialog.showDialog("ϵͳ��ʾ", "û�п����������ӣ�\n��������״̬��", LoginActivity.this);
		}
	}

	public boolean isConnectInternet() {
		boolean netSataus = false;
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			netSataus = networkInfo.isAvailable();
		}
		return netSataus;
	}


	public int checkLogin(String userid, String password) {
		loginCheckResult = 0;
		loginServletURL ="http://192.168.1.105:8080/PlantCloudAtlasAppWebpub/LoginServlet";   
		loginServletURL += "?userid=" + userid + "&password=" + password;

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
}
