package com.nctu_android.iot;

import android.app.Activity;

import android.graphics.Color;
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
        final Button sp1 = (Button) this.findViewById(R.id.space1);
        final Button sp2 = (Button) this.findViewById(R.id.space2);
        final Button sp3 = (Button) this.findViewById(R.id.space3);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String value = data.getString("value");
                String[] distance = value.split(",");
                tv01.setText(value);



                if(Integer.parseInt(distance[0])>50)
                    sp1.setBackgroundColor(Color.GREEN);
                else
                    sp1.setBackgroundColor(Color.RED);

                if(Integer.parseInt(distance[1])>50)
                    sp2.setBackgroundColor(Color.GREEN);
                else
                    sp2.setBackgroundColor(Color.RED);

                if(Integer.parseInt(distance[2])>50)
                    sp3.setBackgroundColor(Color.GREEN);
                else
                    sp3.setBackgroundColor(Color.RED);
            }
        };

        final Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String result = getUrlContent("http://nscl.ngrok.io/m2m/applications/tttttttest/containers/bbbbbbbbbbbla/contentInstances/latest");

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
                String distance1 = data.getString("d0");
                String distance2 = data.getString("d1");
                String distance3 = data.getString("d2");

                return distance1+","+distance2+","+distance3;

            } else {
                return "讀取失敗：\n"+response.getStatusLine().getStatusCode();
            }
        } catch (Exception e) {
            return "讀取失敗:\n"+e.toString();
        }
    }

}