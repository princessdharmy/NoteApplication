package com.princess.android.rxjavaexample;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.princess.android.rxjavaexample.di.components.ApplicationComponent;



public class NoteApplication extends MultiDexApplication{

    static NoteApplication instance;
    private ApplicationComponent applicationComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        applicationComponent = ApplicationComponent.component(this);
        applicationComponent.inject(this);

    }

    public static ApplicationComponent component(Context context){
        return ((NoteApplication)context.getApplicationContext()).applicationComponent;
    }

    public static synchronized NoteApplication getInstance(){
        return instance;
    }
}
