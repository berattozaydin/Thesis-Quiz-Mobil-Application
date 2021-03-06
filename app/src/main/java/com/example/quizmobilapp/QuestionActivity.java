package com.example.quizmobilapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {
    TextView tv_question,txtTimer;
    ImageView imageView;
    Button a,b,c,d;
    Random random;
    int currentpost;
    int score=0;
    int questionsayisi=0,totalQuestionNumber=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        tv_question=findViewById(R.id.tv_Question);
        imageView=findViewById(R.id.imageView2);
        txtTimer=findViewById(R.id.txt_Timer);
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                txtTimer.setText(f.format(min) + ":" + f.format(sec));
            }
            public void onFinish() {
                txtTimer.setText("00:00:00");
                Toast.makeText(QuestionActivity.this,"Süreniz Bitti Hesaptan Çıkış Yapıldı Uygulamayı Kapatın",Toast.LENGTH_SHORT).show();
                UserActivity userActivity = new UserActivity();
                userActivity.logout();
                System.exit(0);
            }
        }.start();
        a=findViewById(R.id.btn_a);
        b=findViewById(R.id.btn_b);
        c=findViewById(R.id.btn_c);
        d=findViewById(R.id.btn_d);
        random = new Random();
        String data;
        Intent intent = getIntent();
        data=intent.getStringExtra("data");
        String url =  getString(R.string.api_server)+"/quiz"+"/"+data;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http= new Http(QuestionActivity.this,url);
                http.setToken(true);
                http.send();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                JSONArray jsonArry = response.getJSONArray("sorulars");
                                ArrayList<HashMap<String,String>> array = new ArrayList<>();
                                for(int i=0;i<jsonArry.length();i++){
                                    HashMap<String,String> quest = new HashMap<>();
                                    JSONObject obj = jsonArry.getJSONObject(i);
                                    quest.put("question",obj.getString("question"));
                                    quest.put("image_question",obj.getString("image_question"));
                                    quest.put("a",obj.getString("a"));
                                    quest.put("b",obj.getString("b"));
                                    quest.put("c",obj.getString("c"));
                                    quest.put("d",obj.getString("d"));
                                    quest.put("correctanswer",obj.getString("correctanswer"));
                                    totalQuestionNumber++;
                                    array.add(quest);
                                }
                                setdata(questionsayisi,array);
                                a.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (array.get(questionsayisi).get("correctanswer").trim().toLowerCase().equals(a.getTag().toString().trim().toLowerCase())) {
                                            score++;
                                        }
                                        if(totalQuestionNumber==questionsayisi+1){
                                            jsonDataSend(totalQuestionNumber,score,data);
                                            finish();
                                        }else{
                                            questionsayisi++;
                                            setdata(questionsayisi,array);
                                        }
                                    }
                                });
                                b.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view){
                                        if (array.get(questionsayisi).get("correctanswer").trim().toLowerCase().equals(b.getTag().toString().trim().toLowerCase())) {
                                            score++;
                                        }
                                        if(totalQuestionNumber==questionsayisi+1){
                                            jsonDataSend(totalQuestionNumber,score,data);
                                            finish();
                                        }else{
                                            questionsayisi++;
                                            setdata(questionsayisi,array);
                                        }
                                    }
                                });
                                c.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (array.get(questionsayisi).get("correctanswer").trim().toLowerCase().equals(c.getTag().toString().trim().toLowerCase())) {
                                            score++;
                                        }
                                        if(totalQuestionNumber==questionsayisi+1){
                                           jsonDataSend(totalQuestionNumber,score,data);
                                            finish();
                                        }else{
                                            questionsayisi++;
                                            setdata(questionsayisi,array);
                                        }
                                    }
                                });
                                d.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (array.get(currentpost).get("correctanswer").trim().toLowerCase().equals(d.getTag().toString().trim().toLowerCase())) {
                                            score++;
                                        }
                                        if(totalQuestionNumber==questionsayisi+1){
                                            jsonDataSend(totalQuestionNumber,score,data);
                                            finish();
                                        }else{
                                            questionsayisi++;
                                            setdata(questionsayisi,array);
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }}).start();
    }
    private String temizle(String metin){
        String temizMetin = "";
        for (int i = 0; i < metin.length(); i++) {
           if(i==0){
               i+=3;
           }
            if (metin.charAt(i) >= 'A' && metin.charAt(i) <= 'Z' || metin.charAt(i) >= 'a' && metin.charAt(i) <= 'z' || metin.charAt(i)==' ') {
                temizMetin += metin.charAt(i);
            }
            if(i==(metin.length()-4)){
                break;
            }
        }
        return temizMetin;
    }
    private void setdata(int currentpost,ArrayList<HashMap<String,String>> array) {
        tv_question.setText(temizle(array.get(currentpost).get("question").toString()));
        if(array.get(currentpost).get("image_question").toString()!=null)
        {
            Picasso.get().load("http://beratozaydin.org/"+array.get(currentpost).get("image_question").toString()).into(imageView);
        }
        a.setText(temizle(array.get(currentpost).get("a").toString()));
        b.setText(temizle(array.get(currentpost).get("b").toString()));
        c.setText(temizle(array.get(currentpost).get("c").toString()));
        d.setText(temizle(array.get(currentpost).get("d").toString()));
    }
    public void jsonDataSend(int totalQuestionNumber,int score,String data){
        String url = getString(R.string.api_server)+"/quiz"+"/"+data+"/sonucs";
        JSONObject params= new JSONObject();
        try {
            params.put("slug",data);
            params.put("dogrusayisi",score);
        }catch (JSONException e){
            e.printStackTrace();
        }
        String yolla = params.toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http= new Http(QuestionActivity.this,url);
                http.setMethod("POST");
                http.setData(yolla);
                http.setToken(true);
                http.send();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if(code == 200){
                            Toast.makeText(QuestionActivity.this,"Sınavınız Bitti Uygulamayı Kapatın",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(QuestionActivity.this,"Sınavınız Bitti Uygulamayı Kapatın",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}