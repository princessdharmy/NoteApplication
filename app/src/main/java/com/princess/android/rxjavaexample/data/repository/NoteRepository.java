package com.princess.android.rxjavaexample.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.princess.android.rxjavaexample.network.ApiClient;
import com.princess.android.rxjavaexample.utils.PrefUtils;
import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.data.model.User;
import com.princess.android.rxjavaexample.network.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class NoteRepository {

    private static final String TAG = NoteRepository.class.getSimpleName();

    private ApiService apiService;
    private List<Note> noteList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public NoteRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }


    /**
     * Registering new user
     * sending unique id as device identification
     **/

    public LiveData<User> registerUser(String authorisation){
        // unique id to identify the device
        String uniqueId = UUID.randomUUID().toString();

        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        disposable.add(
                apiService
                .register(authorisation, uniqueId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        userMutableLiveData.postValue(user);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Register Error: "+ e.getMessage());

                    }
                })
        );
        return userMutableLiveData;
    }


/**
     * Fetching all notes from api
     * The received items will be in random order
     * map() operator is used to sort the items in descending order by Id
     */
    public LiveData<List<Note>> fetchAllNotes(String authorisation) {

        final MutableLiveData<List<Note>> listMutableLiveData = new MutableLiveData<>();
        disposable.add(
                apiService.fetchAllNotes(authorisation)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(notes -> {
                            //Sort the notes by Id
                            Collections.sort(notes, (n1, n2) -> n2.getId() - n1.getId());
                            return notes;
                        })
                        .subscribeWith(new DisposableSingleObserver<List<Note>>() {
                            @Override
                            public void onSuccess(List<Note> notes) {
                                listMutableLiveData.postValue(notes);

                                Log.e(TAG, "Fetched Note: " + noteList.toString());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Fetch Error: " + e.getMessage());
                            }
                        })
        );
        return listMutableLiveData;
    }


    /**
     * Creating new note
     */

    public LiveData<Note> createNote(String authorisation, String note) {

        final MutableLiveData<Note> noteMutableLiveData = new MutableLiveData<>();
        disposable.add(
                apiService.createNote(authorisation, note)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Note>() {
                            @Override
                            public void onSuccess(Note note) {

                                if (!TextUtils.isEmpty(note.getError())) {
                                    Log.e(TAG, note.getError());
                                }

                                Log.e(TAG, "New note created: " + note.getId() + " " + note.getNote() +
                                        note.getTimestamp());

                                noteMutableLiveData.postValue(note);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Add Error: " + e.getMessage());
                            }
                        })
        );
        return noteMutableLiveData;
    }


    /**
     * Updating a note
     */

    public LiveData<Note> updateNote(String authorisation, int noteId, final String note,
                           final int position) {

        final MutableLiveData<Note> updateMutableLiveData = new MutableLiveData<>();
        disposable.add(
                apiService.updateNote(authorisation, noteId, note)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Log.e(TAG, "Note updated");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Error: " + e.getMessage());
                            }
                        })
        );
        return updateMutableLiveData;
    }


    /**
     * Delete a note
     */

    public LiveData<Note> deleteNote(String authorisation, final int noteId, final int position) {

        final MutableLiveData<Note> deleteMutableLiveData = new MutableLiveData<>();
        disposable.add(
                apiService.deleteNote(authorisation, noteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Log.e(TAG, "Note deleted! " + noteId);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Error: " + e.getMessage());
                            }
                        })
        );
        return deleteMutableLiveData;
    }
}

