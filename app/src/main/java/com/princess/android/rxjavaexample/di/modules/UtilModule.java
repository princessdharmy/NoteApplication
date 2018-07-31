package com.princess.android.rxjavaexample.di.modules;

import android.content.Context;

import com.princess.android.rxjavaexample.di.scope.AppContext;
import com.princess.android.rxjavaexample.utils.PrefUtils;
import com.princess.android.rxjavaexample.utils.RecyclerTouchListener;
import com.princess.android.rxjavaexample.utils.ToastUtil;
import com.princess.android.rxjavaexample.utils.Utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class UtilModule {

    @Provides
    @Singleton
    PrefUtils providePrefUtils(){
        return new PrefUtils();
    }

    @Provides
    @Singleton
    Utils provideUtils(){
        return new Utils();
    }

    @Provides
    @Singleton
    ToastUtil providesToastUtil(@AppContext Context context){
        return new ToastUtil(context);
    }

}
