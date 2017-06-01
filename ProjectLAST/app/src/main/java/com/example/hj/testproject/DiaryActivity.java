package com.example.hj.testproject;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity {
    DatePicker picker;
    EditText diary;
    Button button;
    String filename;
    int cYear,cMonth,cDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        setTitle("Daily diary");
        picker=(DatePicker)findViewById(R.id.datePicker);
        diary=(EditText)findViewById(R.id.editDiary);
        button=(Button)findViewById(R.id.button);
        Calendar cal=Calendar.getInstance();
        cYear=cal.get(Calendar.YEAR);
        cMonth=cal.get(Calendar.MONTH);
        cDay=cal.get(Calendar.DAY_OF_MONTH);
        picker.init(cYear,cMonth,cDay,new DatePicker.OnDateChangedListener(){
            public void onDateChanged(DatePicker view,int year,int monthOfYear,int
                    dayOfMonth){
                filename=Integer.toString(year)+"-"+Integer.toString(monthOfYear+1)+"-"+
                        Integer.toString(dayOfMonth)+".txt";
                String str=readDiary(filename);
                diary.setText(str);
                button.setEnabled(true);
            }});
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                FileOutputStream outFs;
                try{
                    outFs=openFileOutput(filename,MODE_ENABLE_WRITE_AHEAD_LOGGING);
                    String str=diary.getText().toString();
                    outFs.write(str.getBytes());
                    outFs.close();
                    Toast.makeText(getApplicationContext(),filename+"이 저장되었습니다.",Toast.LENGTH_SHORT).show();
                }
                catch(FileNotFoundException e){e.printStackTrace();}
                catch(IOException e){}

            }
        });
    }
    private String readDiary(String Name){
        String diaryStr=null;
        FileInputStream inFs;
        try{
            inFs=openFileInput(Name);
            byte[] txt=new byte[500];
            inFs.read(txt);
            inFs.close();
            diaryStr=(new String(txt)).trim();
            button.setText("수정");       }
        catch(IOException e){
            diary.setHint("오늘 하루를 기록하세요!");
            button.setText("저장");
        }
        return diaryStr;
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
