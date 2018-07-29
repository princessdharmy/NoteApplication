package com.princess.android.rxjavaexample.utils;

import android.util.Log;
import android.view.View;

import com.princess.android.rxjavaexample.data.model.Note;

import javax.inject.Singleton;

@Singleton
public class MyClickHandlers {

    public void addNote(View view) {
        Log.e("ADD NOTE","Fab clicked");
    }

    public void onNoteClicked(Note note){
        Log.e("TAG","Note clicked");
    }
}
