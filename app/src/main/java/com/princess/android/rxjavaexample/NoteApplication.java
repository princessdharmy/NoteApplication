package com.princess.android.rxjavaexample;

import android.app.Activity;
import android.app.Application;

import com.princess.android.rxjavaexample.di.components.DaggerNoteApplicationComponent;
import com.princess.android.rxjavaexample.common.PrefUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;


public class NoteApplication extends Application implements HasActivityInjector{

    static NoteApplication instance;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    PrefUtils prefUtils;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DaggerNoteApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    public static synchronized NoteApplication getInstance(){
        return instance;
    }
}
