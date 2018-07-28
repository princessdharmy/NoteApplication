package com.princess.android.rxjavaexample.di.components;

import android.app.Activity;

import com.princess.android.rxjavaexample.NoteApplication;
import com.princess.android.rxjavaexample.di.modules.ActivityModule;
import com.princess.android.rxjavaexample.di.scope.PerActivity;
import com.princess.android.rxjavaexample.main.view.MainActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    static ActivityComponent component(Activity activity){
        return NoteApplication.component(activity).plusActivityModule(new ActivityModule(activity));
    }
}
