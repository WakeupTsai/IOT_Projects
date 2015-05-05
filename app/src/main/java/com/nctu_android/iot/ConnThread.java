package com.nctu_android.iot;

/**
 * Created by USER on 2015/5/5.
 */
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;


public class ConnThread extends Thread {

    String num1, num2;
    Activity activity;
    TextView tv;

    ConnThread(Activity activity, TextView tv,
               String num1, String num2) {
        this.activity = activity;
        this.tv = tv;
        this.num1 = num1;
        this.num2 = num2;
    }

    @Override
    public void run() {
        String url = "http://140.113.254.225:7777/";

        HttpGet request = new HttpGet(url);
        request.addHeader("User-Agent", " Mozilla/5.0 (Windows NT 6.3; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0");
        String result = "";
        try {
            HttpResponse response = new DefaultHttpClient().execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(
                        response.getEntity());
                JSONObject jo = new JSONObject(result);
                int mul = jo.getInt("multiplication");
                int add = jo.getInt("addition");
                connsucc(mul, add);
            } else {
                connerror();
            }
        } catch (Exception e) {
            connerror();
        }
    }

    void connsucc(final int mul, final int add) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String result = "Multiplication: " + mul +
                        "\n" + "Addition: " + add;
                tv.setText(result);
            }
        });
    }

    void connerror() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "連線失敗",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
