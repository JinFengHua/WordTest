package com.example.wordtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordtest.translateapi.TransApi;

import java.util.Map;

public class WordTranslate extends AppCompatActivity {
    //设置appid和securitykey,这两项可以去百度翻译API上申请获得
    private static final String APP_ID = "appid";
    private static final String SECURITY_KEY = "securityKey";

    private TextView to,method,start;
    private EditText from;
    private Map<String, String> results;
    private PopupMenu popupMenu;
    private String fromDirection = "zh",
            toDirection = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_translate);

        initComponent();
    }

    private void initComponent(){
        to = findViewById(R.id.translate_to);
        from = findViewById(R.id.translate_from);
        method = findViewById(R.id.translate_method);
        start = findViewById(R.id.translate_start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (APP_ID.equals("appid") || "securityKey".equals(SECURITY_KEY)){
                    Toast.makeText(WordTranslate.this, "未申请到百度翻译API开发者的AppID", Toast.LENGTH_SHORT).show();
                    return;
                }
//                开启线程,调用api获得翻译结果
                new Thread(new httpConnectThread(APP_ID,SECURITY_KEY)).start();
            }

        });
    }

    private Map<String, String> getResult(String appId, String securityKey) {
        TransApi api = new TransApi(appId,securityKey);
        String query = from.getText().toString();
        if (fromDirection.equals("zh")){
            return api.getTransResult(query,"zh","en");
        }else {
            return api.getTransResult(query,"en","zh");
        }
    }

    public class httpConnectThread implements Runnable{
        private String APP_ID;
        private String SECURITY_KEY;

        public httpConnectThread(String APP_ID, String SECURITY_KEY) {
            this.APP_ID = APP_ID;
            this.SECURITY_KEY = SECURITY_KEY;
        }

        @Override
        public void run() {
            results = getResult(APP_ID,SECURITY_KEY);
            final String toText = results.get("dst");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    to.setText(toText);
                }
            });
        }
    }

    public void menuClick(View view){
        popupMenu = new PopupMenu(this,view);
        getMenuInflater().inflate(R.menu.translate_direction_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.en_to_zh:
                        fromDirection = "en";
                        toDirection = "zh";
                        method.setText("当前模式:英译汉");
                        break;
                    case R.id.zh_to_en:
                        fromDirection = "zh";
                        toDirection = "en";
                        method.setText("当前模式:汉译英");
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
}