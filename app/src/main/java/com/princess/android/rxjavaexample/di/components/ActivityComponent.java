package com.princess.android.rxjavaexample.di.components;

import com.princess.android.rxjavaexample.di.modules.MainActivityModule;
import com.princess.android.rxjavaexample.main.viewmodel.MainActivityViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {MainActivityModule.class})
public interface ActivityComponent {

    void inject(MainActivityViewModel mainActivityViewModel);

}
