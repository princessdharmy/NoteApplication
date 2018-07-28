package com.princess.android.rxjavaexample.di.modules;

import android.app.Application;
import android.content.Context;

import com.princess.android.rxjavaexample.di.scope.AppContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module(includes = {NetworkModule.class, UtilModule.class})
public class ApplicationModule {

    public Application application;

    public ApplicationModule(Application application){
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication(){
        return application;
    }

    @Provides
    @Singleton
    @AppContext
    Context providesContext(){
        return application;
    }
}
    



