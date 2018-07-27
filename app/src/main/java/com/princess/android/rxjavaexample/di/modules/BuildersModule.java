package com.princess.android.rxjavaexample.di.modules;

import com.princess.android.rxjavaexample.main.view.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector(modules = {NoteActivityModule.class, MainActivityModule.class})
    abstract MainActivity bindMainActivity();
}
