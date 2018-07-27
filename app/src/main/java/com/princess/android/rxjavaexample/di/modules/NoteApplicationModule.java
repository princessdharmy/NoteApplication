package com.princess.android.rxjavaexample.di.modules;

import android.content.Context;

import com.princess.android.rxjavaexample.NoteApplication;
import com.princess.android.rxjavaexample.common.CommonNoteService;
import com.princess.android.rxjavaexample.common.PrefUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NoteApplicationModule {
    
    @Provides
    Context provideContext(NoteApplication noteApplication){
        return noteApplication.getApplicationContext();
    }

    @Singleton
    @Provides
    PrefUtils providePrefUtils(Context context){
        return new PrefUtils(context);
    }

    @Singleton
    @Provides
    CommonNoteService provideCommonNoteService(){
        return new CommonNoteService();
    }
}
