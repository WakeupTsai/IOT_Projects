package com.nctu_android.iot;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn01 = (Button) this.findViewById(R.id.button01);
        final TextView tv01 = (TextView) this.findViewById(R.id.textview01);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                tv01.setText(data.getString("value"));
            }
        };

        final Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String result = getUrlContent("http://nscl.ngrok.io/m2m/applications/SmartMeter/containers/blablabla/contentInstances/latest");

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", result);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };

        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(runnable).start();
            }
        });
    }



    /**
     * 獲取url對應的網頁內容
     * @param url
     * @return
     */
    private String getUrlContent(String url) {

        HttpGet getRequest = new HttpGet(url);
        getRequest.addHeader("User-Agent", " Mozilla/5.0 (Windows NT 6.3; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0");
        try {
            HttpResponse response = new DefaultHttpClient().execute(getRequest);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = ""; //存儲讀取的一行內容
                String NL = System.getProperty("line.separator");  //換行符
                while ((line = in.readLine()) != null) {
                    sb.append(line+NL);
                }

                in.close();

                //JSON parser
                JSONObject jo = new JSONObject(sb.toString());
                JSONObject contentInstance  = jo.getJSONObject("contentInstance");
                JSONObject content = contentInstance.getJSONObject("content");
                String code = content.getString("$t");

                byte[] temp = Base64.decode(code, Base64.DEFAULT);
                String distance = new String(temp, "UTF-8");

                JSONObject data = new JSONObject(distance);
                String distance1 = data.getString("distance1");
                String distance2 = data.getString("distance2");
                String distance3 = data.getString("distance3");


                return "distance1: "+distance1+"\ndistance2: "+distance2+"\ndistance3: "+distance3;

            } else {
                return "讀取失敗：\n"+response.getStatusLine().getStatusCode();
            }
        } catch (Exception e) {
            return "讀取失敗:\n"+e.toString();
        }
    }

}