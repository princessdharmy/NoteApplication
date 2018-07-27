package com.princess.android.rxjavaexample.di.modules;


import com.princess.android.rxjavaexample.NoteApplication;
import com.princess.android.rxjavaexample.data.repository.NoteRepository;
import com.princess.android.rxjavaexample.main.view.MainActivityNoteAdapter;
import com.princess.android.rxjavaexample.main.viewmodel.MainActivityViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    NoteRepository noteRepository;

    @Provides
    MainActivityNoteAdapter provideNoteAdapter(){
        return new MainActivityNoteAdapter();
    }

    @Provides
    MainActivityViewModel provideViewModel(){
        return new MainActivityViewModel(noteRepository);
    }

}
