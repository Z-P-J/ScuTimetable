package com.zpj;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private long firstTime=0;
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    TextView textView;
    String s;
    String s1;
    ViewPager vp;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    private static String sessionId;
    private List<FragmentItem> fragmentItemList;
    FragmentItemAdapter fragmentItemAdapter;
    private RecyclerView recyclerView;
    private Handler handler;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Calendar calendar;
    private String[] jieshu={"第一节","第二节","第三节","第四节","","第五节","第六节","第七节","第八节","第九节","","第十节","第十一节","第十二节","第十三节"};
    private String[] shijian={"8:15-9:00","9:10-9:55","10:15-11:00","11:10-11:55","11:55-1:50","13:50-14:35","14:45-15:30","15:40-16:25","16:50-17:35","17:45-18:30","18:30-19:20","19:20-20:05","20:15-21:00","21:10-21:55","22:05-22:50"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp=(ViewPager)findViewById(R.id.vp);
        //fragmentItemList=new ArrayList<>();
        initView();
        calendar=Calendar.getInstance();
        int xingqi=calendar.get(Calendar.DAY_OF_WEEK);
        if (xingqi==1){
            //vp.setCurrentItem(6);
            vp.setCurrentItem(6);
        }else{
            vp.setCurrentItem(xingqi-1);
            //vp.setCurrentItem(5);
            //vp.setCurrentItem(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                switchBut(1);
                vp.setCurrentItem(0);
                break;
            case R.id.button2:
                switchBut(2);
                vp.setCurrentItem(1);
                break;
            case R.id.button3:
                switchBut(3);
                vp.setCurrentItem(2);
                break;
            case R.id.button4:
                switchBut(4);
                vp.setCurrentItem(3);
                break;
            case R.id.button5:
                switchBut(5);
                vp.setCurrentItem(4);
                break;
            case R.id.button6:
                switchBut(6);
                vp.setCurrentItem(5);
                break;
            case R.id.button7:
                switchBut(7);
                vp.setCurrentItem(6);
                break;
        }
    }

    public void switchBut(int i){
        switch (i){
            case 1:
                //button1.setBackgroundColor(Color.parseColor("#0ac215"));
                button1.setBackgroundColor(Color.parseColor("#9979cdcd"));
                button2.setBackgroundColor(Color.parseColor("#99ffffff"));
                button3.setBackgroundColor(Color.parseColor("#99ffffff"));
                button4.setBackgroundColor(Color.parseColor("#99ffffff"));
                button5.setBackgroundColor(Color.parseColor("#99ffffff"));
                button6.setBackgroundColor(Color.parseColor("#99ffffff"));
                button7.setBackgroundColor(Color.parseColor("#99ffffff"));
                break;
            case 2:
                button1.setBackgroundColor(Color.parseColor("#99ffffff"));
                button2.setBackgroundColor(Color.parseColor("#9979cdcd"));
                button3.setBackgroundColor(Color.parseColor("#99ffffff"));
                button4.setBackgroundColor(Color.parseColor("#99ffffff"));
                button5.setBackgroundColor(Color.parseColor("#99ffffff"));
                button6.setBackgroundColor(Color.parseColor("#99ffffff"));
                button7.setBackgroundColor(Color.parseColor("#99ffffff"));
                break;
            case 3:
                button1.setBackgroundColor(Color.parseColor("#99ffffff"));
                button2.setBackgroundColor(Color.parseColor("#99ffffff"));
                button3.setBackgroundColor(Color.parseColor("#9979cdcd"));
                button4.setBackgroundColor(Color.parseColor("#99ffffff"));
                button5.setBackgroundColor(Color.parseColor("#99ffffff"));
                button6.setBackgroundColor(Color.parseColor("#99ffffff"));
                button7.setBackgroundColor(Color.parseColor("#99ffffff"));
                break;
            case 4:
                button1.setBackgroundColor(Color.parseColor("#99ffffff"));
                button2.setBackgroundColor(Color.parseColor("#99ffffff"));
                button3.setBackgroundColor(Color.parseColor("#99ffffff"));
                button4.setBackgroundColor(Color.parseColor("#9979cdcd"));
                button5.setBackgroundColor(Color.parseColor("#99ffffff"));
                button6.setBackgroundColor(Color.parseColor("#99ffffff"));
                button7.setBackgroundColor(Color.parseColor("#99ffffff"));
                break;
            case 5:
                button1.setBackgroundColor(Color.parseColor("#99ffffff"));
                button2.setBackgroundColor(Color.parseColor("#99ffffff"));
                button3.setBackgroundColor(Color.parseColor("#99ffffff"));
                button4.setBackgroundColor(Color.parseColor("#99ffffff"));
                button5.setBackgroundColor(Color.parseColor("#9979cdcd"));
                button6.setBackgroundColor(Color.parseColor("#99ffffff"));
                button7.setBackgroundColor(Color.parseColor("#99ffffff"));
                break;
            case 6:
                button1.setBackgroundColor(Color.parseColor("#99ffffff"));
                button2.setBackgroundColor(Color.parseColor("#99ffffff"));
                button3.setBackgroundColor(Color.parseColor("#99ffffff"));
                button4.setBackgroundColor(Color.parseColor("#99ffffff"));
                button5.setBackgroundColor(Color.parseColor("#99ffffff"));
                button6.setBackgroundColor(Color.parseColor("#9979cdcd"));
                button7.setBackgroundColor(Color.parseColor("#99ffffff"));
                break;
            case 7:
                button1.setBackgroundColor(Color.parseColor("#99ffffff"));
                button2.setBackgroundColor(Color.parseColor("#99ffffff"));
                button3.setBackgroundColor(Color.parseColor("#99ffffff"));
                button4.setBackgroundColor(Color.parseColor("#99ffffff"));
                button5.setBackgroundColor(Color.parseColor("#99ffffff"));
                button6.setBackgroundColor(Color.parseColor("#99ffffff"));
                button7.setBackgroundColor(Color.parseColor("#9979cdcd"));
                break;
        }
    }

    public void initView(){
        button1=(Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2=(Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button3=(Button)findViewById(R.id.button3);
        button3.setOnClickListener(this);
        button4=(Button)findViewById(R.id.button4);
        button4.setOnClickListener(this);
        button5=(Button)findViewById(R.id.button5);
        button5.setOnClickListener(this);
        button6=(Button)findViewById(R.id.button6);
        button6.setOnClickListener(this);
        button7=(Button)findViewById(R.id.button7);
        button7.setOnClickListener(this);

        //button1.setBackgroundColor(Color.GREEN);


        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {


                Fragment fragment=null;
                switch (position){
                    case 0:
                        fragment=new Fragment1();
                        break;
                    case 1:
                        fragment=new Fragment2();
                        break;
                    case 2:
                        fragment=new Fragment3();
                        break;
                    case 3:
                        fragment=new Fragment4();
                        break;
                    case 4:
                        fragment=new Fragment5();
                        break;
                    case 5:
                        fragment=new Fragment6();
                        break;
                    case 6:
                        fragment=new Fragment7();
                        break;
                    default:
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 7;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                return super.instantiateItem(container, position);
            }


        });
        vp.addOnPageChangeListener(new MyPagerChangeListener());
    }

    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    switchBut(1);
                    break;
                case 1:
                    switchBut(2);
                    break;
                case 2:
                    switchBut(3);
                    break;
                case 3:
                    switchBut(4);
                    break;
                case 4:
                    switchBut(5);
                    break;
                case 5:
                    switchBut(6);
                    break;
                case 6:
                    switchBut(7);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis()-firstTime>2000){
            Toast.makeText(this, "再次点击退出！", Toast.LENGTH_SHORT).show();
            firstTime=System.currentTimeMillis();
        }else {
            ActivityCollector.finishAll();
        }
    }
}
