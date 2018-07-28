package com.princess.android.rxjavaexample.di.modules;


import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    Context provideContext(){
        return activity;
    }

    @Provides
    Activity provideActivity(){
        return activity;
    }
}
