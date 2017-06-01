package com.example.hj.testproject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Network;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import org.apache.http.client.HttpClient;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.hj.testproject.R.id.setAlarm;
public class interfaceActivity extends AppCompatActivity {
    private PendingIntent pending_intent;
    private FirebaseAuth firebaseAuth;
   // static TextView idtext;
    static TextView nametext;
    static TextView providertext;
    static TextView countNumber;
    private Button save;
    static NetworkImageView profileimg;
    private ImageLoader imageLoader;
    private TextView yes;
    private TextView rate;
    private int numOfData;
    private int successRate;
    AlarmManager alarm_manager;
    String numData;
    String rateSuccess;
    int hour,minute;
    private TimePickerDialog.OnTimeSetListener listener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int  Minute) {
            hour=hourOfDay;
            minute=Minute;
            Intent intent=new Intent(getApplicationContext(),BroadcastD.class);
            alarm_manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Calendar calendar=Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,minute);
            calendar.set(Calendar.SECOND,0);
            intent.putExtra("extra","Time");
            pending_intent = PendingIntent.getBroadcast(interfaceActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            if(successRate==100){
                alarm_manager.set(AlarmManager.RTC,calendar.getTimeInMillis(),pending_intent);
            }
            else{
                alarm_manager.set(AlarmManager.RTC,calendar.getTimeInMillis(),pending_intent);
                alarm_manager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),(1000*60),pending_intent);
            }
            yes.setText(hour+"시"+minute+"분");
            Toast.makeText(getApplicationContext(),hour + "시" + minute + "분에 알림이 시작됩니다.",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_activiy);
        SharedPreferences aa=getSharedPreferences("aa",0);
        successRate=aa.getInt("four",0);
        rateSuccess=Integer.toString(successRate);

        countNumber=(TextView)findViewById(R.id.countReceive);
        numOfData=aa.getInt("first",0);
        numData=Integer.toString(numOfData);
        countNumber.setText(numData);
        alarm_manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(getApplicationContext(),BroadcastD.class);
        pending_intent = PendingIntent.getBroadcast(interfaceActivity.this,0,
                intent,PendingIntent.FLAG_ONE_SHOT);
        yes=(TextView)findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                TimePickerDialog dialog = new TimePickerDialog(interfaceActivity.this, TimePickerDialog.THEME_HOLO_LIGHT,listener, 00, 00, false);
                dialog.show();
                           }
        });
        save=(Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(),BroadcastD.class);
                pending_intent = PendingIntent.getBroadcast(interfaceActivity.this,0,
                        intent,PendingIntent.FLAG_UPDATE_CURRENT);
                //intent.putExtra("extra","alarm off");
                sendBroadcast(intent);
                alarm_manager.cancel(pending_intent);
                Toast.makeText(getApplicationContext(),"알림을 해제합니다.",Toast.LENGTH_SHORT).show();

            }
        });

        imageLoader = new ImageLoader(Volley.newRequestQueue(this), new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        profileimg = (NetworkImageView) findViewById(R.id.profileImage);
       // idtext = (TextView) findViewById(R.id.IdReceive);
        nametext = (TextView) findViewById(R.id.nameReceive);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();
                // UID specific to the provider
               // String uid = profile.getUid();
                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();
                //profileimg.setImageURI(photoUrl);
                profileimg.setImageUrl(user.getPhotoUrl().toString(), imageLoader);
                //idtext.setText(email);
                nametext.setText(name);
                profileimg.setVisibility(View.VISIBLE);

            };
        }
        Button btn = (Button)findViewById(R.id.logout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(interfaceActivity.this, SignInActivity.class));
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}