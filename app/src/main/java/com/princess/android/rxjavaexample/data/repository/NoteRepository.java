package com.princess.android.rxjavaexample.data.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.princess.android.rxjavaexample.common.PrefUtils;
import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.data.model.User;
import com.princess.android.rxjavaexample.network.ApiClient;
import com.princess.android.rxjavaexample.network.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class NoteRepository {

    private Context context;
    private static final String TAG = NoteRepository.class.getSimpleName();

    @Inject
    ApiService apiService = ApiClient.getClient().create(ApiService.class);

    private List<Note> noteList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public NoteRepository() {
    }



    /**
     * Registering new user
     * sending unique id as device identification
     **/
    public void registerUser(){
        // unique id to identify the device
        String uniqueId = UUID.randomUUID().toString();

        disposable.add(
                apiService
                .register(uniqueId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        //Storing user API key in preferences
                        PrefUtils.storeApiKey(user.getApiKey());
                        Log.e(TAG, "Device is registered successfully! ApiKey: " +
                                PrefUtils.getApiKey());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error: "+ e.getMessage());

                    }
                })
        );
    }

    /**
     * Fetching all notes from api
     * The received items will be in random order
     * map() operator is used to sort the items in descending order by Id
     */
    public void fetchAllNotes(){
        disposable.add(
                apiService.fetchAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<Note>, List<Note>>() {
                    @Override
                    public List<Note> apply(List<Note> notes) throws Exception {
                        //Sort the notes by Id
                        Collections.sort(notes, new Comparator<Note>() {
                            @Override
                            public int compare(Note n1, Note n2) {
                                return n2.getId() - n1.getId();
                            }
                        });
                        return notes;
                    }
                })
                .subscribeWith(new DisposableSingleObserver<List<Note>>() {
                    @Override
                    public void onSuccess(List<Note> notes) {
                        noteList.clear();
                        noteList.addAll(notes);
                        Log.e(TAG, "Fetched Note: " + noteList.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error: "+ e.getMessage());
                    }
                })
        );
    }

    /**
     * Creating new note
     */
    public void createNote(String note){
        disposable.add(
                apiService.createNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Note>() {
                    @Override
                    public void onSuccess(Note note) {
                        if(!TextUtils.isEmpty(note.getError())){
                            Log.e(TAG, note.getError());
                        }
                        Log.e(TAG, "New note created: " + note.getId() + note.getNote() +
                                note.getTimestamp());
                        //Add new Item
                        noteList.add(0, note);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                })
        );
    }

    /**
     * Updating a note
     */
    public void updateNote(int noteId, final String note, final int position){
        disposable.add(
                apiService.updateNote(noteId, note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Note updated");

                        Note n = noteList.get(position);
                        n.setNote(note);

                        //Update note
                        noteList.set(position, n);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                })
        );
    }

    /**
     * Delete a note
     */
    public void deleteNote(final int noteId, final int position){
        disposable.add(
                apiService.deleteNote(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Note deleted! " + noteId);

                        //Remove a note
                        noteList.remove(position);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                })
        );
    }
}