package com.princess.android.rxjavaexample.di.modules;

import com.princess.android.rxjavaexample.common.ActivityNoteService;


import dagger.Module;
import dagger.Provides;

@Module
public class NoteActivityModule {


    @Provides
    ActivityNoteService provideActivityNoteService(){
        return new ActivityNoteService();
    }
}
