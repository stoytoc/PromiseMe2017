package com.example.hj.testproject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
public class AddHabitActivity extends AppCompatActivity {
    private PendingIntent pending_intent;
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    AlarmManager alarm_manager;
    private int alarmHour;
    private int alarmMinute;
    private int alarmIndex=0;
    Button button;
    final String Tag="hi";
    EditText editText;
    Spinner spin1;
    TextView countView;
    Spinner spin4;
    TextView setAlarm;

    String[] period={"매일","일주일","한달"};

    String[] alarm={"no","yes"};
    private TimePickerDialog.OnTimeSetListener listener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setAlarm.setText(hourOfDay+"시"+minute+"분에 알람이 울립니다.");
            alarmHour=hourOfDay;
            alarmMinute=minute;
        }
    };
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_habit);
        button=(Button)findViewById(R.id.save);
        editText=(EditText)findViewById(R.id.editText1);
        spin1=(Spinner)findViewById(R.id.spin1);
        countView=(EditText)findViewById(R.id.count);
        spin4=(Spinner)findViewById(R.id.spin4);
        button=(Button)findViewById(R.id.save);
        setAlarm=(TextView)findViewById(R.id.setAlarm);
        final Intent my_intent=new Intent(getApplicationContext(),AlarmReceiver.class);
        alarm_manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        setAlarm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                TimePickerDialog dialog = new TimePickerDialog(AddHabitActivity.this, TimePickerDialog.THEME_HOLO_LIGHT,listener, 00, 00, false);
                dialog.show();
            }
        });
        // updateLabel();
        dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,period);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapter1);
        ArrayAdapter<String> adapter4=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,alarm);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin4.setAdapter(adapter4);
        button.setOnClickListener(new View.OnClickListener() {//버튼 클릭시 각 항목에 저장되 있는 String정보들이 테이블로 들어감
            public void onClick(View v) {
                String id = editText.getText().toString();//EditText에 있는 text내용 불러오기
                TextView txt = (TextView) spin1.getSelectedView();//Spiner1번 text 가져오기
                String period = txt.getText().toString();
                String count=countView.getText().toString();
                TextView txt4 = (TextView) spin4.getSelectedView();//Spiner4번 text 가져오기
                String alarm = txt4.getText().toString();
                String sql=String.format(
                        "INSERT INTO item (_id, name, period, count, alarm,hour,minute)\n"+
                                "VALUES (NULL, '%s', '%s', '%s', '%s','%d','%d')",
                        id, period, count, alarm,alarmHour,alarmMinute);
                dbHelper.getWritableDatabase().execSQL(sql);
                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,alarmHour);
                calendar.set(Calendar.MINUTE,alarmMinute);
                calendar.set(Calendar.SECOND,0);
                if(alarmHour==0&&alarmMinute==0){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();}
                else{
                    my_intent.putExtra("extra","alarm on");
                    my_intent.putExtra("name",id);
                    SharedPreferences aa=getSharedPreferences("aa",0);
                    alarmIndex=aa.getInt("first",0);
                    pending_intent = PendingIntent.getBroadcast(AddHabitActivity.this,alarmIndex,my_intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.set(AlarmManager.RTC,calendar.getTimeInMillis(),pending_intent);}
                Toast.makeText(getApplicationContext(),(alarmIndex+1)+"번째 알람 입니다"+alarmHour+"시"+alarmMinute+"분 에 알람이 울립니다.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
