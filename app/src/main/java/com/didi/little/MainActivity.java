package com.didi.little;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //首页viewpager
    private ViewPager viewPager;
    //装载viewpager view
    private ArrayList<View> list_view=new ArrayList<View>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        initCompant();

    }
    // 初始化组件
    private void initCompant(){

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        LayoutInflater inflater=getLayoutInflater().from(this);

        View view_maillist=inflater.inflate(R.layout.maillist_view,null);

        list_view.add(view_maillist);

        viewPager.setAdapter(new MainViewPagerAdapter(list_view));

        // 滑动事件监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    // 退出自动登陆
    public void ExitUser(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("是否退出 ？");
        builder.setMessage("同时将取消自动登陆");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("exit",true);
                startActivity(intent);
                finish();
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
    //viewpager 适配器
    private class MainViewPagerAdapter extends PagerAdapter {
        private ArrayList<View> listview;
        public MainViewPagerAdapter(ArrayList<View> listvie){
            this.listview=listvie;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listview.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(listview.get(position),0);
            return list_view.get(position);
        }

        @Override
        public int getCount() {
            return listview.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
}
