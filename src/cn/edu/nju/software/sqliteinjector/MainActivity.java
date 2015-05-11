package cn.edu.nju.software.sqliteinjector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.view.View.OnClickListener;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.oauth.BaiduOAuth.OAuthListener;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;

import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	public final static String mbApiKey = "GnTt27nrFkEkZ7fhtpoXFyzo";//请替换申请客户端应用时获取的Api Key串
	public final static String mbRootPath =  "/apps/HookLog"; //用户测试的根
    SharedPreferences spf;
	private Button login;
	private Button getQuota;
	public static String mbOauth = null;
	private Handler mbUiThreadHandler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		spf = this.getPreferences(MODE_PRIVATE);
		mbOauth = spf.getString("AccessToken", null);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	
	mbUiThreadHandler = new Handler();
	login = (Button) this.findViewById(R.id.login);

	getQuota = (Button) this.findViewById(R.id.getquota);
		
	login.setOnClickListener(new OnClickListener() {
	        @Override
	public void onClick(View v) {
	BaiduOAuth oauthClient = new BaiduOAuth();
	oauthClient.startOAuth(MainActivity.this, mbApiKey, new String[]{"basic", "netdisk"}, new BaiduOAuth.OAuthListener() {
	                @Override
	public void onException(String msg) {
	Toast.makeText(getApplicationContext(), "Login failed " + msg, Toast.LENGTH_SHORT).show();
	                }
	                @Override
	public void onComplete(BaiduOAuthResponse response) {
	if(null != response){
	mbOauth = response.getAccessToken();
	SharedPreferences.Editor editor = spf.edit(); 
	//用putString的方法保存数据 
	editor.putString("AccessToken", mbOauth); 

	Toast.makeText(getApplicationContext(), "Token: " + mbOauth + "    User name:" + response.getUserName(), 6).show();
	                    }
	                }
	                @Override
	public void onCancel() {
	Toast.makeText(getApplicationContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
	                }
	            });
	        }
	    });

	getQuota.setOnClickListener(new Button.OnClickListener(){
	public void onClick(View v) {
	test_getQuota();
	            } 
	    });
	}

	private void test_getQuota(){

	if(null != mbOauth){
		    Thread workThread = new Thread(new Runnable(){
		public void run() {
			BaiduPCSClient api = new BaiduPCSClient();
			api.setAccessToken(mbOauth);
	final BaiduPCSActionInfo.PCSQuotaResponse info = api.quota();

		mbUiThreadHandler.post(new Runnable(){
	public void run(){
					if(null != info){
		if(0 == info.status.errorCode){
		Toast.makeText(getApplicationContext(), "Quota :" + info.total + "  used: " + info.used, Toast.LENGTH_SHORT).show();
	                            }
		else{
	Toast.makeText(getApplicationContext(), "Quota failed: " + info.status.errorCode + "  " + info.status.message, Toast.LENGTH_SHORT).show();
	                            }
	                        }
	                    }
	                });
	            }
	        }); 

		workThread.start();
	   }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
