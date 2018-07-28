package com.princess.android.rxjavaexample.main.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.princess.android.rxjavaexample.R;
import com.princess.android.rxjavaexample.di.components.ActivityComponent;
import com.princess.android.rxjavaexample.utils.PrefUtils;
import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.data.model.User;
import com.princess.android.rxjavaexample.data.repository.NoteRepository;
import com.princess.android.rxjavaexample.databinding.ActivityMainBinding;
import com.princess.android.rxjavaexample.main.viewmodel.MainActivityViewModel;
import com.princess.android.rxjavaexample.network.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;

    @Inject MainActivityViewModel viewModel;
    @Inject NoteRepository noteRepository;

    private Context context;
    private List<Note> noteList = new ArrayList<>();
    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    String note = "I want some coffee";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ActivityComponent.component(this).inject(this);

        context = this;
        initBinding();
        checkRegistrationStatus();
    }

    private void initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setClickHandler(new MainActivityOnClickHandler());
    }

    /**
     * Check for stored Api Key in shared preferences
     * If not present, make api call to register the user
     * This will be executed when app is installed for the first time
     * or data is cleared from settings
     * */
    private void checkRegistrationStatus(){
        if(TextUtils.isEmpty(PrefUtils.getApiKey(this))){
            registerUser();
        } else {
            //viewModel.loadNotes();
        }
    }

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
                                PrefUtils.storeApiKey(getApplicationContext(), user.getApiKey());
                                Log.e("TAG", "Device is registered successfully! ApiKey: " +
                                        PrefUtils.getApiKey(getApplicationContext()));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("TAG", "Error: "+ e.getMessage());

                            }
                        })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public class MainActivityOnClickHandler {
        public void addNote(View view) {
            Log.e("ADD NOTE","Fab clicked");
        }
    }
}
