package com.princess.android.rxjavaexample.di.components;

import android.app.Application;

import com.princess.android.rxjavaexample.NoteApplication;
import com.princess.android.rxjavaexample.di.modules.ActivityModule;
import com.princess.android.rxjavaexample.di.modules.ApplicationModule;
import com.princess.android.rxjavaexample.di.modules.NetworkModule;


import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(NoteApplication noteApplication);

    ActivityComponent plusActivityModule(ActivityModule activityModule);

    static ApplicationComponent component(Application application){
        return DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(application))
                .networkModule(new NetworkModule())
                .build();
    }
}
