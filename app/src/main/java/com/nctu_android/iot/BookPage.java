package com.nctu_android.iot;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

//應該要改成從資料庫抓，而不是存catch
public class BookPage extends Activity {

    private Button btn;
    private Switch swi;
    private int id ;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        Intent intent = getIntent();
        id = intent.getIntExtra("Park_id",1);

        btn = (Button) this.findViewById(R.id.back);
        btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                db.close();
                finish();
            }

        });


        DBOpenHelper openhelper = new DBOpenHelper(this);
        db = openhelper.getWritableDatabase();
        int status = ParkDB.getStatus(db,id);
        int book = ParkDB.getBook(db,id);
        //Toast.makeText(BookPage.this, "id="+id+"  "+status+"/"+book, Toast.LENGTH_SHORT).show();



        swi = (Switch) this.findViewById(R.id.switch1);
        if(book==1){
            swi.setChecked(true);
        }
        else{
            swi.setChecked(false);
        }


        swi.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
        @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(BookPage.this, "ON", Toast.LENGTH_SHORT).show();
                    ParkDB.editBook(db,id,1);

                } else {
                    Toast.makeText(BookPage.this, "OFF", Toast.LENGTH_SHORT).show();
                    ParkDB.editBook(db,id,0);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }
}
