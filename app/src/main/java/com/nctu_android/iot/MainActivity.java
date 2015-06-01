package com.nctu_android.iot;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Base64;
import android.widget.Toast;

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



        final ImageButton btn01 = (ImageButton) this.findViewById(R.id.button01);
        final Button sp1 = (Button) this.findViewById(R.id.space1);
        final Button sp2 = (Button) this.findViewById(R.id.space2);
        final Button sp3 = (Button) this.findViewById(R.id.space3);

        //將收集到的資訊做處理
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String value = data.getString("value");
                //String value="70,50,20";
                String[] distance = value.split(",");


                if(Integer.parseInt(distance[0])>50) {
                    sp1.getBackground().setColorFilter(0xFF00FF00, android.graphics.PorterDuff.Mode.MULTIPLY );
                    sp1.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "this space is empty", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
                else {
                    sp1.getBackground().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY );
                    sp1.setOnClickListener(new Button.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,BookPage.class);
                            startActivity(intent);
                        }

                    });
                }

                if(Integer.parseInt(distance[1])>50) {
                    sp2.getBackground().setColorFilter(0xFF00FF00, android.graphics.PorterDuff.Mode.MULTIPLY );
                    sp2.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "this space is empty", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
                else {
                    sp2.getBackground().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY );
                    sp2.setOnClickListener(new Button.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,BookPage.class);
                            startActivity(intent);
                        }

                    });
                }

                if(Integer.parseInt(distance[2])>50) {
                    sp3.getBackground().setColorFilter(0xFF00FF00, android.graphics.PorterDuff.Mode.MULTIPLY );
                    sp3.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "this space is empty", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
                else {
                    sp3.getBackground().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY );
                    sp3.setOnClickListener(new Button.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,BookPage.class);
                            startActivity(intent);
                        }

                    });
                }
            }
        };

        final Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String result = getUrlContent("http://nscl.ngrok.io/m2m/applications/project/containers/park/contentInstances/latest");

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", result);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };

        //手動點擊更新資料
        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(runnable).start();
                Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        //定期自動更新資料
        final int delay = 1000; // 1 second
        final int period = 60000; // 60 second
        final Handler refreshData= new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),"Refresh",Toast.LENGTH_LONG).show();
                new Thread(runnable).start();
                refreshData.postDelayed(this, period);
            }
        };
        refreshData.postDelayed(r, delay);
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
                return "讀取失敗1：\n"+response.getStatusLine().getStatusCode();
            }
        } catch (Exception e) {
            return "讀取失敗2:\n"+e.toString();
        }
    }

}