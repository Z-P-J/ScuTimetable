package com.zpj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends BaseActivity {

    private Handler handler;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText xuehao;
    private EditText mima;
    private String[] jieshu={"第一节","第二节","第三节","第四节","","第五节","第六节","第七节","第八节","第九节","","第十节","第十一节","第十二节","第十三节"};
    private String[] shijian={"8:15-9:00","9:10-9:55","10:15-11:00","11:10-11:55","11:55-1:50","13:50-14:35","14:45-15:30","15:40-16:25","16:50-17:35","17:45-18:30","18:30-19:20","19:20-20:05","20:15-21:00","21:10-21:55","22:05-22:50"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        editor=pref.edit();

        if (pref.getBoolean("denglu",false)){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }else{
            initView();
        }
    }

    public void initView(){
        Button login=(Button)findViewById(R.id.login);
        xuehao=(EditText)findViewById(R.id.xuehao);
        mima=(EditText)findViewById(R.id.mima);
        String account=pref.getString("account","");
        String password=pref.getString("password","");
        xuehao.setText(account);
        mima.setText(password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (xuehao.getText().toString().equals("")||mima.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "请填写好账号信息。", Toast.LENGTH_SHORT).show();
                }else {
                    getSessionId();
                    handler=new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what==1){
                                String sessionId1=(String)msg.obj;
                                editor.putString("account",xuehao.getText().toString());
                                editor.putString("password",mima.getText().toString());
                                editor.putBoolean("denglu",true);
                                getTimetable(sessionId1);

                            }
                        }
                    };
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void getSessionId(){
        new Thread(){
            @Override
            public void run() {
                String sessionId="";
                try{
                    URL url=new URL("http://202.115.47.141/loginAction.do");
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.connect();
                    DataOutputStream out=new DataOutputStream(connection.getOutputStream());
                    String content="zjh="+xuehao.getText().toString()+"&mm="+mima.getText().toString();
                    out.writeBytes(content);
                    out.flush();
                    out.close();
                    String key="";
                    if (connection!=null){
                        for (int i=0;(key=connection.getHeaderFieldKey(i))!=null;i++){
                            if (key.equalsIgnoreCase("set-cookie")){
                                sessionId=connection.getHeaderField(key);
                                sessionId=sessionId.substring(0,sessionId.indexOf(";"));
                            }
                        }
                    }
                    connection.disconnect();
                    Log.i("rerere",sessionId);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Message msg=new Message();
                msg.obj=sessionId;
                msg.what=1;
                handler.sendMessage(msg);
            }
        }.start();
    }

    public static String getHtml(String sessionId){
        String urlContent=null;
        InputStream in = null;
        HttpURLConnection connection=null;
        OutputStream out;
        BufferedReader reader;
        StringBuffer sb=new StringBuffer("");
        try{
            URL url=new URL("http://202.115.47.141/xkAction.do?actionType=6");
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Cookie",sessionId);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.connect();
            int code=connection.getResponseCode();
            if (code==HttpURLConnection.HTTP_OK){
                String inputLine=null;
                reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),"GBK"));
                while ((inputLine=reader.readLine())!=null){
                    sb.append(inputLine);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (null!=in){
                    in.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if (null!=connection){
                connection.disconnect();
            }
        }
        urlContent=sb.toString();
        Log.i("aaaaaaaa",urlContent);
        //}
        //}.start();
        return urlContent;
    }

    public void getTimetable(final String session){
        new Thread(){
            @Override
            public void run() {
                try {
                    Document document= Jsoup.parse(getHtml(session));
                    Elements elements=document.select("td.pageAlign").select("tr");
                    for (int n=1;n<8;n++){

                        for(int i=1;i<=15;i++){
                            int m=n;
                            if (i==1){
                                m+=1;
                            }
                            if (i==5||i==11){
                                if (i==5){
                                    editor.putString(n+"_"+i,"午休");
                                }else{
                                    editor.putString(n+"_"+i,"晚饭");
                                }

                                i+=1;
                                m+=1;
                            }
                            String x=elements.get(i).select("td").get(m).text();
                            Log.i("xxxxxxx",x+i);
                            editor.putString(n+"_"+i,x);
                        }

                    }
                    editor.apply();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        for (int i=1;i<=15;i++){
            editor.putString(i+"_jieshu",jieshu[i-1]);
            editor.putString(i+"_shijian",shijian[i-1]);
        }

    }
}
