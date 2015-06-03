package com.nctu_android.iot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

//繼承android.app.Service
public class bookService extends Service {

    SQLiteDatabase db;



    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.postDelayed(showTime, 1000);
        super.onStart(intent, startId);
        //開啟db
        DBOpenHelper openhelper = new DBOpenHelper(this);
        db = openhelper.getWritableDatabase();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        super.onDestroy();
    }

    private Runnable showTime = new Runnable() {
        public void run() {
            //log目前時間
            Log.i("time:", new Date().toString());

            new Thread(runnable).start();

            handler.postDelayed(this, 1000);
        }
    };

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String value = data.getString("value");
            //String value="20,50,70";

            //Toast.makeText(bookService.this, "1", Toast.LENGTH_SHORT).show();

            String[] distance = value.split(",");


            if(Integer.parseInt(distance[0])>50) {
                ParkDB.editStatus(db,1,0);
                if(ParkDB.getBook(db,1)==1){
                    setVibrate(1000);
                    ParkDB.editBook(db,1,0);
                    notifi(1);
                }
            }
            else {
                ParkDB.editStatus(db,1,1);
            }

            if(Integer.parseInt(distance[1])>50) {
                ParkDB.editStatus(db,2,0);
                if(ParkDB.getBook(db,2)==1){
                    setVibrate(1000);
                    ParkDB.editBook(db,2,0);
                    notifi(2);
                }
            }
            else {
                ParkDB.editStatus(db,2,1);
            }

            if(Integer.parseInt(distance[2])>50) {
                ParkDB.editStatus(db,3,0);
                if(ParkDB.getBook(db,3)==1){
                    setVibrate(1000);
                    ParkDB.editBook(db,3,0);
                    notifi(3);

                }
            }
            else {
                ParkDB.editStatus(db,3,1);
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
                //return "讀取失敗1：\n"+response.getStatusLine().getStatusCode();
                Toast.makeText(bookService.this, "Can not get information from internet.", Toast.LENGTH_SHORT).show();
                return "-1,-1,-1";
            }
        } catch (Exception e) {
            //return "讀取失敗2:\n"+e.toString();
            Toast.makeText(bookService.this,"Can not get information from internet.",Toast.LENGTH_SHORT).show();
            return "-1,-1,-1";
        }
    }

    //控制震動的function
    public void setVibrate(int time){
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(time);
    }

    //跳出提醒
    public void notifi(int id){//Get a reference to the NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);


        //Instantiate the Notification
        int icon = android.R.drawable.ic_dialog_alert;
        CharSequence tickerText = "您預約的車位空出來了";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //Define the Notification's expanded message and Intent
        Context context = getApplicationContext();
        CharSequence contentTitle = "您預約的"+id+"號車位空出來了";
        CharSequence contentText = "趕快把車子開過去吧!!!!!";
        //Intent notificationIntent = new Intent(AndroidStatusBarNotifications.this, AndroidStatusBarNotifications.class);
        Intent notificationIntent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(bookService.this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        //Pass the Notification to the NotificationManager
        mNotificationManager.notify(1, notification);
    }

}