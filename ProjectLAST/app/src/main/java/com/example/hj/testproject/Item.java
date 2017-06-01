package com.example.hj.testproject;

/**
 * Created by duscj on 2017-05-11.
 */

public class Item {
    //_id, name, period, count, category, alarm
    private int id;
    private String name;
    private String period;
    private String count;
    private String alarm;

    public Item(){
    }
    // Select할 경우
    public Item(String name, String period, String count,String alarm){
        this.name = name;
        this.period = period;
        this.count = count;
        this.alarm= alarm;
    }

    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }
}
