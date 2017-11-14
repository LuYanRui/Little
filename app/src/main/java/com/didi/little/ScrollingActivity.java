package com.didi.little;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.feezu.liuli.timeselector.TimeSelector;


public class ScrollingActivity extends AppCompatActivity {

    private String user_name;
    private String user_number;
    private TextView tv_number,tv_timesle;
    private EditText edit_title,edit_message,edit_action;
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initCompant();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        user_name=getIntent().getStringExtra("name");
        user_number=getIntent().getStringExtra("number");

        getSupportActionBar().setTitle(user_name);
        tv_number.setText("手机："+user_number);

    }
    private void initCompant(){
        tv_number=(TextView) findViewById(R.id.tv_number);
        tv_timesle=(TextView) findViewById(R.id.tv_timesele);
        edit_title=(EditText) findViewById(R.id.editText_title);
        edit_message=(EditText) findViewById(R.id.editText_message);
        edit_action=(EditText) findViewById(R.id.editText_action);
        btn_start=(Button) findViewById(R.id.btn_ale_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        edit_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ScrollingActivity.this);
                builder.setTitle("请选择动作");
                builder.setItems(new String[]{"打电话", "发短信"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edit_action.setText(i==1?"发短信":"打电话");
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling,menu);
        return true;
    }
    public void TimeSelectAle(View view){
        selectTime();
    }
    public void CallNumber(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+user_number));
        startActivity(intent);

    }
    private void selectTime(){
        TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                tv_timesle.setText(time);
            }
        }, Utils.getCurtimeHH(), "2030-12-12 18:20");
        timeSelector.show();
    }
    // 发送短信
    public void SendNumber(View view){
        Intent intsent = new Intent();
        intsent.setAction(Intent.ACTION_SENDTO);
        intsent.setData(Uri.parse("smsto:"+user_number));
        intsent.putExtra("sms_body", "");
        startActivity(intsent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_daochu:
                startActivity(new Intent(this,EditContactActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
