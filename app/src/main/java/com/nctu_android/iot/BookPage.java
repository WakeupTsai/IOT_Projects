package com.nctu_android.iot;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;


public class BookPage extends Activity {

    private Button btn;
    private Switch swi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        btn = (Button) this.findViewById(R.id.back);
        btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        swi = (Switch) this.findViewById(R.id.switch1);
        swi.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    Toast.makeText(BookPage.this, "ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BookPage.this, "OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
