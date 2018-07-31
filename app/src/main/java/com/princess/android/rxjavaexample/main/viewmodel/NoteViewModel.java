/*
package com.princess.android.rxjavaexample.main.viewmodel;


import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.data.repository.NoteRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class NoteViewModel extends ViewModel {

    public NoteRepository noteRepository;
    private List<Note> noteList;

    @Inject
    public NoteViewModel(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        this.noteList = new ArrayList<>();
    }

    public List<Note> getNotes(){
        return noteRepository.getNotes();
    }

    public void registerUser(Context context){
        this.noteRepository.registerUser(context);
    }

    public List<Note> getAllNotes(){
        this.noteRepository.fetchAllNotes();
        return null;
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
*/
