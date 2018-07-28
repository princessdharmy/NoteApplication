package com.princess.android.rxjavaexample.main.viewmodel;


import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.data.repository.NoteRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class MainActivityViewModel extends ViewModel {

    public NoteRepository noteRepository;
    private List<Note> noteList;

    @Inject
    public MainActivityViewModel(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        this.noteList = new ArrayList<>();
    }

    public void loadNotes(){
        noteList.addAll(noteRepository.getNotes());
        Log.e("NOTES: ", String.valueOf(noteList.addAll(noteRepository.getNotes())));
    }

    public List<Note> getAllNotes(){
        return noteList;
    }
}
