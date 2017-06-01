package com.example.hj.testproject;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    private DatabaseHelper helper;
    private DrawerLayout mDrawerLayout;
    private ListView listView;
    private ArrayList<Item> todayWorkList;
    private MyAdapter adapter;
    private int numOfItem;
    private int numOfSuccess=0;
    private double achievePercent;
    private ImageView change;
    private ImageView init;
    private String name,period,count;
    private ProgressBar progress;
    private TextView showPercent;
    private int selectedPos=-1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.todayWorkList);
        todayWorkList = new ArrayList<Item>();
        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddHabitActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navigation_item_images:
                        Intent intent1 = new Intent(getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent1);
                        Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_item_calender:
                        Intent intent = new Intent(getApplicationContext(),
                                DiaryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_Interface:
                        Intent logtest = new Intent(getApplicationContext(), interfaceActivity.class);
                        startActivity(logtest);
                        break;
                    case R.id.nav_sub_menu_item01:
                        Intent stepac = new Intent(getApplicationContext(), StepActivity.class);
                        startActivity(stepac);
                        // Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_sub_menu_item02:
                        startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                        break;
                }
                return true;
            }
        });
        requestToDatabase();
        showList();
    }
    /* protected  void onRestart(){
         super.onRestart();
         Intent intent=new Intent(getApplicationContext(),MainActivity.class);
         startActivity(intent);
     }*/
    protected void onStart(){
        super.onStart();
        numOfSuccess=0;
        numOfItem=0;
        Log.d("요청", "요청입니다.");
        SQLiteDatabase db;
        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        String TABLE_NAME = "item";
        todayWorkList.clear();
        Cursor c1 = db.rawQuery("select name, period, count,alarm from " + TABLE_NAME, null);
        while (c1.moveToNext()) {
            Item objItem = new Item(c1.getString(0),c1.getString(1),c1.getString(2),c1.getString(3));
            if(c1.getString(3).compareTo("yes")==0)numOfSuccess++;

            todayWorkList.add(objItem);
        }
        numOfItem=todayWorkList.size();
        SharedPreferences aa=getSharedPreferences("aa",0);
        SharedPreferences.Editor editor=aa.edit();
        editor.putInt("first",numOfItem);
        editor.putInt("second",numOfSuccess);
        editor.putInt("third",numOfItem-numOfSuccess);
        c1.close();
        achievePercent=(double)numOfSuccess/numOfItem;
        achievePercent=achievePercent*100;
        progress.setProgress((int)achievePercent);
        int a=(int)achievePercent;
        editor.putInt("four",a);
        editor.commit();
        String s=String.valueOf(a);
        String b=new String("달성률:"+s+"%");
        showPercent.setText(b);
        if(a>=0&&a<30){change.setBackgroundResource(R.drawable.start);   }
        else if(a>=30&&a<50){change.setBackgroundResource(R.drawable.start1);
        }
        else if(a>=50&&a<80){change.setBackgroundResource(R.drawable.start2);
        }
        else if(a>=80){change.setBackgroundResource(R.drawable.start3);
        }
        adapter.notifyDataSetChanged();
    }
    private void requestToDatabase() {
    }
    private void showList(){
        progress=(ProgressBar)findViewById(R.id.progress_bar);
        showPercent=(TextView)findViewById(R.id.percent);
        change=(ImageView)findViewById(R.id.start);
        adapter = new MyAdapter(this.getBaseContext());
        helper = new DatabaseHelper(getApplicationContext());
        listView.setAdapter(adapter);
        numOfItem=adapter.getCount();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPos=position;
                AlertDialog.Builder alertDig=new AlertDialog.Builder(view.getContext());
                alertDig.setTitle(R.string.alert_title_question);
                alertDig.setNegativeButton(
                        R.string.button_no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick( DialogInterface dialog, int which ) {
                                dialog.dismiss();  // AlertDialog를 닫는다.
                            }
                        }
                );
                alertDig.setPositiveButton(R.string.button_yes,new DialogInterface.OnClickListener(){//예 버튼 클릭하면 아이템 삭제하는 것
                    int num=0;
                    String TABLE_NAME = "item";
                    public void onClick(DialogInterface dialog,int which){
                        try{
                            Item deleteItem=(Item)adapter.getItem(selectedPos);
                            String name=deleteItem.getName();
                            todayWorkList.remove(selectedPos);
                            String sql=String.format(
                                    "DELETE FROM item\n"+
                                            "WHERE name='%s'",name) ;
                            helper.getWritableDatabase().execSQL(sql);
                            Toast.makeText(getApplicationContext(),name+"항목이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                            numOfItem=todayWorkList.size();
                            SharedPreferences aa=getSharedPreferences("aa",0);
                            SharedPreferences.Editor editor=aa.edit();
                            editor.putInt("first",numOfItem);
                            editor.putInt("second",numOfSuccess);
                            editor.putInt("third",numOfItem-numOfSuccess);
                            Cursor c1 = helper.getWritableDatabase().rawQuery("select name, period, count, alarm from " + TABLE_NAME, null);
                            while (c1.moveToNext()) {
                                Item objItem = new Item(c1.getString(0),c1.getString(1), c1.getString(2),c1.getString(3));
                                if(c1.getString(3).compareTo("yes")==0)num++;}
                            numOfSuccess=num;
                            achievePercent=(double)numOfSuccess/numOfItem;
                            achievePercent=achievePercent*100;
                            progress.setProgress((int)achievePercent);
                            int a=(int)achievePercent;
                            editor.putInt("four",a);
                            editor.commit();
                            String s=String.valueOf(a);
                            String b=new String("달성률:"+s+"%");
                            showPercent.setText(b);
                            if(a>=0&&a<30){change.setBackgroundResource(R.drawable.start);   }
                            else if(a>=30&&a<50){change.setBackgroundResource(R.drawable.start1);
                            }
                            else if(a>=50&&a<80){change.setBackgroundResource(R.drawable.start2);
                            }
                            else if(a>=80){change.setBackgroundResource(R.drawable.start3);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        catch(SQLException e){e.printStackTrace();}
                    }
                });
                alertDig.setMessage("선택한 습관을 삭제하시겠습니까?");
                alertDig.show();

                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                ImageView image=(ImageView)view.findViewById(R.id.check_valid);
                Item component=(Item)adapter.getItem(position);
                name=component.getName();
                period=component.getPeriod();
                count=component.getCount();
                String check=component.getAlarm();
                if(check.compareTo("no")==0){
                    image.setBackgroundResource(R.drawable.on_check);
                    numOfSuccess++;
                    System.out.println("습관 성공 갯수:"+numOfSuccess);
                    SharedPreferences aa=getSharedPreferences("aa",0);
                    SharedPreferences.Editor editor=aa.edit();
                    editor.putInt("first",numOfItem);
                    editor.putInt("second",numOfSuccess);
                    editor.putInt("third",numOfItem-numOfSuccess);
                    achievePercent=(double)numOfSuccess/numOfItem;
                    achievePercent=achievePercent*100;
                    progress.setProgress((int)achievePercent);
                    int a=(int)achievePercent;
                    editor.putInt("four",a);
                    editor.commit();
                    String s=String.valueOf(a);
                    String b=new String("달성률:"+s+"%");
                    showPercent.setText(b);
                    //Toast.makeText(getApplicationContext(),"현재 달성률:"+form.format(achievePercent)+"%",Toast.LENGTH_SHORT).show();
                    check="yes";
                    todayWorkList.remove(position);
                    todayWorkList.add(position,new Item(name,period,count,check));
                    try{
                        String sql=String.format(
                                "UPDATE item\n"+
                                        "SET alarm = '%s'\n "+
                                        "WHERE name ='%s'",
                                check,component.getName());
                        helper.getWritableDatabase().execSQL(sql);
                        helper.close();
                    }
                    catch(SQLiteException e){e.printStackTrace();}
                }
                else{
                    image.setBackgroundResource(R.drawable.non_check);
                    numOfSuccess--;
                    System.out.println("습관 성공 갯수:"+numOfSuccess);
                    achievePercent=(double)numOfSuccess/numOfItem;
                    achievePercent=achievePercent*100;
                    progress.setProgress((int)achievePercent);
                    int a=(int)achievePercent;
                    String s=String.valueOf(a);
                    String b=new String("달성률:"+s+"%");
                    showPercent.setText(b);
                    //Toast.makeText(getApplicationContext(),"현재 달성률:"+form.format(achievePercent)+"%",Toast.LENGTH_SHORT).show();
                    check="no";
                    todayWorkList.remove(position);
                    todayWorkList.add(position,new Item(name,period,count,check));
                    try{
                        String sql=String.format(
                                "UPDATE item\n"+
                                        "SET alarm = '%s'\n "+
                                        "WHERE name ='%s'",
                                check,component.getName());
                        helper.getWritableDatabase().execSQL(sql);
                        helper.close();
                    }
                    catch(SQLiteException e){e.printStackTrace();}
                }
                int a=(int)achievePercent;
                if(a>=0&&a<30){change.setBackgroundResource(R.drawable.start);   }
                else if(a>=30&&a<50){change.setBackgroundResource(R.drawable.start1);
                }
                else if(a>=50&&a<80){change.setBackgroundResource(R.drawable.start2);
                }
                else if(a>=80){change.setBackgroundResource(R.drawable.start3);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    private class MyAdapter extends ArrayAdapter<Item> {
        public MyAdapter(Context context) {
            super(context, R.layout.row_todaywork, todayWorkList);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).
                        inflate(R.layout.row_todaywork, parent, false);
            }
            init=(ImageView)convertView.findViewById(R.id.check_valid);
            Item objWork  = getItem(position);
            if(objWork.getAlarm().compareTo("no")==0){
                init.setBackgroundResource(R.drawable.non_check);
            }
            else if(objWork.getAlarm().compareTo("yes")==0)
            {
                init.setBackgroundResource(R.drawable.on_check);
            }
            TextView txtTitle= (TextView) convertView.findViewById(R.id.row_title);
            TextView count=(TextView)convertView.findViewById(R.id.count);
            TextView period=(TextView)convertView.findViewById(R.id.period);
            period.setText(objWork.getPeriod());
            count.setText(objWork.getCount()+"회");
            txtTitle.setText(objWork.getName());
            return convertView;
        }
    }
    //뒤로가기 두번
    long pressTime;
    @Override
    public void onBackPressed(){
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - pressTime;
        if(intervalTime <2000){
            super.onBackPressed();
            finishAffinity();
        }else{
            pressTime = currentTime;
            Toast.makeText(this,"한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }
    //재 로그인 요청
    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}