package com.example.weatherdemo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    private EditText input_city;
    private Button query_weather;
    private EditText display_weather;
    private static final String KEY="eafe057992984980b8490b9396557c02";
    private static final String TAG="Debug";
    
    public static final int SHOW_RESPONSE = 0;
    private Handler handle=new Handler(){
    	public void handleMessage(Message msg) {
    	    switch(msg.what) {
    	    case SHOW_RESPONSE:
    	    	String response = (String) msg.obj;
    	         display_weather.setText(response); 
    	       
    	         break;
    	    }
    	}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input_city=(EditText)findViewById(R.id.city);
        display_weather=(EditText)findViewById(R.id.content);
        query_weather=(Button)findViewById(R.id.query);
        query_weather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String city=input_city.getText().toString();
//				String httpUrl="https://api.heweather.com/x3/weather?cityid="+city+"&key="+KEY;
				//Ϊ�˱��ڲ��ԣ����ｫ����ֱ��д�����趨Ϊ����
		        String httpUrl="https://api.heweather.com/x3/weather?cityid=CN101280601&key=eafe057992984980b8490b9396557c02";	
				connectWithHttpURLConnection(httpUrl);

			}
		});
    
    
    }

    public void connectWithHttpURLConnection(final String httpUrl) {
    	//���������п��ܺ�ʱ�Ͼã������������߳̽��С���˿������߳�
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				URL url;
			 try {
				url = new URL(httpUrl);
				HttpURLConnection connection=(HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				Log.d(TAG,"Url��������");
				connection.connect();
				Log.d(TAG,"Url���ӳɹ�");
				//��ȡ������
				InputStream is=connection.getInputStream();
			    BufferedReader br=new BufferedReader(new InputStreamReader(is));
			    String line=null;
			    StringBuffer response=new StringBuffer();
			    //��ȡ�ַ�������
			    while((line=br.readLine())!=null) {
			    	response.append(line);
			    	
			    }
			    is.close();
			    br.close();
			    Log.d(TAG,response.toString());
			    Log.d(TAG,"�������������ص�json����");
			    //�޳�json�����е���������
			    String json=response.toString().split("\"HeWeather data service 3.0\":")[1];
			    String suggestion=parseJSONWithJSONObject(json,"suggestion");
			    Log.d(TAG,"��ɵ�һ�ν���");
			    String sport=parseJSONWithJSONObject("["+suggestion+"]","sport");
			    Log.d(TAG,"��ɵڶ��ν���");
			    String txt=parseJSONWithJSONObject("["+sport+"]","txt");
			    Log.d(TAG,"��ɵ����ν���");
			    //ʵ������������Ϣ
			    Message msg=new Message();
			    msg.what=SHOW_RESPONSE;
//			    msg.obj=response.toString();
			    msg.obj=txt;
			    handle.sendMessage(msg);
			} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG,"�޷���ȡ����������");
					e.printStackTrace();
		}
			   
				
				
	}}
   ).start();
    	
    }
    
    
    public String parseJSONWithJSONObject(String jsonData,String key) {
    	String value=null;
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			for(int i=0;i<jsonArray.length();i++) {
	    		JSONObject jsonObject=jsonArray.getJSONObject(i);
	    	    value=jsonObject.getString(key);
	    		Log.d(TAG,value);
	    		
	    	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
