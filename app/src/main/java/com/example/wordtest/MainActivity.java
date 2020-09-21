package com.example.wordtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    private ImageView word_form,word_search;
    private TextView start,translateStart,testNum;
    private SeekBar seekBar;
    private int maxNum = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        隐藏标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*隐藏任务栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindComponent();
        Connector.getDatabase();
        Word word = DataSupport.findFirst(Word.class);
        if (word == null){
            readTxt(this,"Result.txt");
        }

        word_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WordForm.class);
                startActivity(intent);
            }
        });

        word_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WordSearch.class);
                startActivity(intent);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxNum < 1){
                    Toast.makeText(v.getContext(),"单词总数不能为0",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this,WordTest.class);
                intent.putExtra("maxNum",maxNum);
                startActivity(intent);
            }
        });

        translateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WordTranslate.class);
                startActivity(intent);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                testNum.setText("测试单词总数:"+progress);
                maxNum = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void bindComponent(){
        word_form = findViewById(R.id.word_form);
        word_search = findViewById(R.id.word_search);
        start = findViewById(R.id.bt_start_test);
        translateStart = findViewById(R.id.word_translate);
        seekBar = findViewById(R.id.seekbar);
        testNum = findViewById(R.id.test_num);
    }

    private void readTxt(Context context,String name){
    BufferedReader reader = null;
    String temp = null;

    try{
        InputStream is = context.getAssets().open(name);
        reader = new BufferedReader(new InputStreamReader(is));
        while ((temp = reader.readLine()) != null){
            Word word = new Word();
            String[] words = temp.split("=>");
            word.setWord(words[0]);
            word.setInterpretation(words[1]);
            word.save();
        }
        reader.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (reader != null){
            try{
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

}