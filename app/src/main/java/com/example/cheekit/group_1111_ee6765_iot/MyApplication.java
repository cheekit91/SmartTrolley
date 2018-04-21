package com.example.cheekit.group_1111_ee6765_iot;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheekit on 11/16/2017.
 */

public class MyApplication extends Application {

    private long someVariable;

    public long getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(long someVariable) {
        this.someVariable = someVariable;
    }

    public float lat=(float)40.81;

    public float longt=(float)-73.96;


    private String userId =new String();
    String getUserId(){return userId;}
    void setUserId(String input){userId=input;}
}

