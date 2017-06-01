package com.example.hj.testproject;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.RawDataPoint;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class StepActivity extends AppCompatActivity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    static TextView step;
    static Button step1;
    static Button step2;
    static Button step3;
    //static Button stepStart;
    static float goal=10000;
    public float stepT;
    Calendar calendar = Calendar.getInstance();
    private FitChart fitChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        //new AlarmHATT(getApplicationContext()).Alarm();
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        step1 = (Button)findViewById(R.id.step1);
        step2 = (Button)findViewById(R.id.step2);
        step3 = (Button)findViewById(R.id.step3);
        //프로그레스바
        final Resources resources = getResources();

        final Collection<FitChartValue> values = new ArrayList<>();
        fitChart = (FitChart)findViewById(R.id.fitChart);
        fitChart.setMinValue(0);
        fitChart.setMaxValue(100);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        Fitness.SensorsApi.remove( mApiClient, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mApiClient.disconnect();
                        }
                    }
                });
    }
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 1, TimeUnit.SECONDS )
                .build();
        Fitness.SensorsApi.add(mApiClient, request, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e("GoogleFit", "SensorApi successfully added");
                        } else {
                            Log.e("GoogleFit", "adding status: " + status.getStatusMessage());
                        }
                    }
                });
    }
    @Override
    public void onConnected(Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE )
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();
        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };
        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( StepActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {
                Log.e( "GoogleFit", "sendingIntentException " + e.getMessage() );
            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK ) {
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                    mApiClient.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }
    @Override
    public void onDataPoint(DataPoint dataPoint) {
        step = (TextView)findViewById(R.id.step);
        // stepStart = (Button)findViewById(R.id.stepStart);
        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            final String tmpVar = value.toString();
            final int stepInt = (Integer.parseInt(tmpVar)-21544);
            //final int stepInt = (Integer.parseInt(tmpVar)-3281);
            //value.setInt(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {/*
                    stepStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            value.setInt(0);
                            Log.d("확인하자", "value : " + value);
                            final String tmpVar = value.toString();
                            Log.d("확인하자", "tmpVar : " + tmpVar);
                            step.setText(0 + " 걸음");
                        }
                    });*/
                    step.setText(stepInt + " 걸음");
                    Log.d("ER", "Ready");
                    try {
                        step1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goal = 6000;
                                SharedPreferences test = getSharedPreferences("test",0);
                                SharedPreferences.Editor editor = test.edit();
                                editor.putFloat("first", goal);
                                //Toast.makeText(getApplicationContext(), "6000", Toast.LENGTH_SHORT).show();
                                // String tmpVar = value.toString();
                                Log.d("확인하자", "float/goal : "+Double.parseDouble(tmpVar)/goal);
                                Log.d("확인하자2", "Value : "+Double.parseDouble(tmpVar) +" 이고, Goal : "+goal);
                                fitChart.setValue((stepInt/goal)*100);
                                Log.d("확인하자퍼센트6", "(float/goal)*100 : "+(stepT/goal)*100);
                            }
                        });
                        step2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goal =8000;
                                SharedPreferences test = getSharedPreferences("test",0);
                                SharedPreferences.Editor editor = test.edit();
                                editor.putFloat("first", goal);
                                //Toast.makeText(getApplicationContext(), "8000", Toast.LENGTH_SHORT).show();
                                // String tmpVar = value.toString();
                                Log.d("확인하자", "float/goal : "+Double.parseDouble(tmpVar)/goal);
                                Log.d("확인하자2", "Value : "+Double.parseDouble(tmpVar) +" 이고, Goal : "+goal);
                                fitChart.setValue((stepInt/goal)*100);
                                Log.d("확인하자퍼센트8", "(float/goal)*100 : "+(stepT/goal)*100);
                            }
                        });
                        step3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goal = 10000;
                                SharedPreferences test = getSharedPreferences("test", 0);
                                SharedPreferences.Editor editor = test.edit();
                                editor.putFloat("first", goal);
                                //Toast.makeText(getApplicationContext(), "10000", Toast.LENGTH_SHORT).show();
                                Log.d("확인하자2", "Value : "+Double.parseDouble(tmpVar) +" 이고, Goal : "+goal);
                                fitChart.setValue((stepInt/goal)*100);
                                Log.d("확인하자퍼센트10", "(float/goal)*100 : "+(stepT/goal)*100);
                            }
                        });
                        stepT = (float)(Double.parseDouble(tmpVar));
                        fitChart.setValue((stepInt/goal)*100);
                    }catch(Exception e){
                        Log.d("Er", "에러 :"+e);
                    }

                }
            });
        }
    }/*
    public class AlarmHATT {
        private Context context;
        public AlarmHATT(Context context) {
            this.context=context;
        }
        public void Alarm() {
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(StepActivity.this, Steppush.class);
            PendingIntent sender = PendingIntent.getBroadcast(StepActivity.this, 0, intent, 0);
            SharedPreferences test = getSharedPreferences("test", 0);
            float firstDataStep = test.getFloat("step", 0);
            float firstDataGoal = test.getFloat("goal", 0);

            Log.d("알아보자2",  "step"+firstDataStep);
            //알람시간 calendar에 set해주기
            if(firstDataStep==firstDataGoal) {
                Log.d("알아보자", "시간 :" + "년" + calendar.get(Calendar.YEAR) + "월" + calendar.get(Calendar.MONTH) + "일" + calendar.get(Calendar.DATE) + "step"+stepT);
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
                        calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)); //시,분,초
                //알람 예약
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            }
        }
    }*/
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

}