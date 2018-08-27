package com.princess.android.rxjavaexample.main.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.data.model.User;
import com.princess.android.rxjavaexample.data.repository.NoteRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class NoteViewModel extends ViewModel {

    public NoteRepository noteRepository;
    private LiveData<List<Note>> listLiveData;
    private List<Note> noteList;

    @Inject
    public NoteViewModel() {
        this.noteRepository = new NoteRepository();
        this.noteList = new ArrayList<>();
    }

    public LiveData<List<Note>> getListLiveData(String authorisation) {
        if(listLiveData == null)
            listLiveData = noteRepository.fetchAllNotes(authorisation);
        return listLiveData;
    }


    public LiveData<User> registerUser(String authorisation){
        return noteRepository.registerUser(authorisation);
    }

    public LiveData<Note> addNote(String authorisation, String note){
        return noteRepository.createNote(authorisation, note);
    }

    public void updateNote(String authorisation, int noteId, String note, int position){
        this.noteRepository.updateNote(authorisation, noteId, note, position);
    }

    public void deleteNote(String authorisation, int noteId, int position){
        this.noteRepository.deleteNote(authorisation, noteId, position);
    }
}

