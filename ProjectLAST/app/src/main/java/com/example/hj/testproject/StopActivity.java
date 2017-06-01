   package com.example.hj.testproject;
           import android.app.AlarmManager;
           import android.app.PendingIntent;
           import android.content.Intent;
           import android.support.v7.app.AppCompatActivity;
           import android.os.Bundle;
           import android.view.View;
           import android.widget.Button;
           import android.widget.TextView;
           import android.widget.Toast;

public class StopActivity extends AppCompatActivity {
    Button stop;
    TextView showTime;
    PendingIntent pending_intent;
    AlarmManager alarm_manager;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        stop=(Button)findViewById(R.id.stop);
        showTime=(TextView)findViewById(R.id.showTime);
        alarm_manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        Intent getIntent=getIntent();
        message=getIntent.getExtras().getString("name");
        message=getIntent.getExtras().getString("name");
        showTime.setText(message +"할 시간이 되었어요!");

        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(),AlarmReceiver.class);
                intent.putExtra("extra","alarm off");
                pending_intent = PendingIntent.getBroadcast(StopActivity.this,2,
                        intent,PendingIntent.FLAG_UPDATE_CURRENT);
                sendBroadcast(intent);
                alarm_manager.cancel(pending_intent);
                Toast.makeText(getApplicationContext(),"알람을 해제합니다.",Toast.LENGTH_SHORT).show();
                Intent intentA=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intentA);
                finish();
            }
        });
    }
}

