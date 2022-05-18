package com.example.namecard0001;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText inputName, inputTelNo, inputEmail;
    Button btnSave, btnSearch;
    String name, telNo, eMail;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        inputName = (EditText) findViewById(R.id.inputName);
        inputTelNo = (EditText) findViewById(R.id.inputTelNo);
        inputEmail = (EditText) findViewById(R.id.inputEmail);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = inputName.getText().toString();
                telNo = inputTelNo.getText().toString();
                eMail = inputEmail.getText().toString();

                dataInsert(name, telNo, eMail);
            }
        });

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = inputName.getText().toString();

                dataSearch(name);
            }
        });
    }

    //데이터 저장
    private void dataInsert(String name, String telNo, String eMail){
        new Thread(){
            @Override
            public void run(){

                try{
                    URL setURL = new URL("Http://10.0.2.2/insert.php/");
                    HttpURLConnection http;
                    http = (HttpURLConnection) setURL.openConnection();

                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("name").append("=").append(name).append("/").append(telNo).append("/").append(eMail).append("/");

                    OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8");
                    outStream.write(buffer.toString());
                    outStream.flush();

                    InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "utf-8");
                    final BufferedReader redear = new BufferedReader(tmp);

                    String str;

                    while((str=redear.readLine()) != null){
                        Log.e("서버측에서 받은 것", str);
                    }
                } catch (Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "입력실패", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        }.start();
    }


    //데이터 검색

    private void dataSearch(String name){
        new Thread(){
            @Override
            public void run(){

                try{
                    URL setURL = new URL("Http://10.0.2.2/search.php/");
                    HttpURLConnection http;
                    http = (HttpURLConnection) setURL.openConnection();

                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("name").append("=").append(name);

                    OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8");
                    outStream.write(buffer.toString());
                    outStream.flush();
                    Log.e("검색 모듈", "서버로 데이터 전달완료");

                    InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "utf-8");
                    final BufferedReader redear = new BufferedReader(tmp);

                    String str2;
                    StringBuilder builder = new StringBuilder();

                    while((str2=redear.readLine()) != null){
                        builder.append(str2+"\n");
                    }
                    String resultData = builder.toString();
                    final String[] sResult = resultData.split("/");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            inputName.setText(sResult[0]);
                            inputTelNo.setText(sResult[1]);
                            inputEmail.setText(sResult[2]);
                        }
                    });

                } catch (Exception e){
                    Log.e("검색 모듈", "서버로 데이터 전달 실패");
                }
            }
        }.start();
    }
}