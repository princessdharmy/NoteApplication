package com.princess.android.rxjavaexample.main.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.princess.android.rxjavaexample.R;
import com.princess.android.rxjavaexample.common.ActivityNoteService;
import com.princess.android.rxjavaexample.common.CommonNoteService;
import com.princess.android.rxjavaexample.common.PrefUtils;
import com.princess.android.rxjavaexample.databinding.ActivityMainBinding;
import com.princess.android.rxjavaexample.main.viewmodel.MainActivityViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;

    @Inject PrefUtils prefUtils;
    @Inject CommonNoteService check;
    @Inject ActivityNoteService activityNoteService;
    @Inject MainActivityViewModel viewModel;

    String note = "I want some coffee";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        //viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        initBinding();
        getNotes();
    }

    private void initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setClickHandler(new MainActivityOnClickHandler());
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayCheck();
        displayConfirm();
    }

    private void displayCheck() {
        Log.e("CHECK", check.checkNote());
    }
    private void displayConfirm(){
        Log.e("CONFIRM", activityNoteService.confirmNote());
    }
    private void getNotes(){
        //viewModel.registerUser();
        //viewModel.getAllNotes();
        viewModel.addNote(note);
        viewModel.getAllNotes();
        viewModel.updateNote(0, "My name is Damilola", 0);
        //viewModel.deleteNote();
    }

    public class MainActivityOnClickHandler {
        public void addNote(View view) {
            Log.e("ADD NOTE","Fab clicked");
        }
    }
}
