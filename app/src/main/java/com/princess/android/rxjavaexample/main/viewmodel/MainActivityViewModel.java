package com.princess.android.rxjavaexample.main.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.princess.android.rxjavaexample.NoteApplication;
import com.princess.android.rxjavaexample.data.repository.NoteRepository;


import javax.inject.Inject;

public class MainActivityViewModel extends ViewModel {

    private NoteRepository noteRepository;

    public MainActivityViewModel(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /*public void registerUser(){
        this.noteRepository.registerUser();
    }*/
    public void getAllNotes(){
        this.noteRepository.fetchAllNotes();
    }
    public void addNote(String note){
        this.noteRepository.createNote(note);
    }
    public void updateNote(int noteId, String note, int position){
        this.noteRepository.updateNote(noteId, note, position);
    }
    public void deleteNote(int noteId, int position){
        this.noteRepository.deleteNote(noteId, position);
    }
}
