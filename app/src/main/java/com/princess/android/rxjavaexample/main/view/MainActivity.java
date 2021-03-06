package com.princess.android.rxjavaexample.main.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.princess.android.rxjavaexample.R;
import com.princess.android.rxjavaexample.di.components.ActivityComponent;
import com.princess.android.rxjavaexample.main.viewmodel.NoteViewModel;
import com.princess.android.rxjavaexample.utils.MyDividerItemDecoration;
import com.princess.android.rxjavaexample.utils.PrefUtils;
import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.databinding.ActivityMainBinding;
import com.princess.android.rxjavaexample.utils.RecyclerTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private List<Note> noteList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private MyClickHandlers handlers;
    TextView noNoteView;

    @Inject NoteViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ActivityComponent.component(this).inject(this);

        initBinding();
        noNoteView = findViewById(R.id.txt_empty_notes_view);
        checkRegistrationStatus();
        initRecyclerView();
    }

    private void initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setClickHandler(new MyClickHandlers(this));
    }

    private void initRecyclerView(){
        noteAdapter = new NoteAdapter(this, noteList);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(noteAdapter);
        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Check for stored Api Key in shared preferences
     * If not present, make api call to register the user
     * This will be executed when app is installed for the first time
     * or data is cleared from settings
     * */
    private void checkRegistrationStatus(){
        if(TextUtils.isEmpty(PrefUtils.getApiKey(this))){
            registerUser(PrefUtils.getApiKey(this));
        } else {
            fetchAllNotes(PrefUtils.getApiKey(this));
        }
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput =
                new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) :
                getString(R.string.lbl_new_note_title));

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", (dialogBox, id) -> {

                })
                .setNegativeButton("cancel",
                        (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Show toast message when no text is entered
            if (TextUtils.isEmpty(inputNote.getText().toString())) {
                Toast.makeText(MainActivity.this, "Enter note!",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                alertDialog.dismiss();
            }

            // check if user updating note
            if (shouldUpdate && note != null) {
                // update note by it's id
                updateNote(PrefUtils.getApiKey(this), note.getId(),
                        inputNote.getText().toString(), position);
            } else {
                // create new note
                createNote(PrefUtils.getApiKey(this), inputNote.getText().toString());
            }
        });
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, (dialog, which) -> {
            if (which == 0) {
                showNoteDialog(true, noteList.get(position), position);
            } else {
                deleteNote(PrefUtils.getApiKey(this), noteList.get(position).getId(),
                        position);
            }
        });
        builder.show();
    }

    private void toggleEmptyNotes() {
        if (noteList.size() > 0) {
            noNoteView.setVisibility(View.GONE);
        } else {
            noNoteView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Showing a Snackbar with error message
     * The error body will be in json format
     * {"error": "Error message!"}
     */
    private void showError(Throwable e) {
        String message = "";
        try {
            if (e instanceof IOException) {
                message = "No internet connection!";
            } else if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                String errorBody = error.response().errorBody().string();
                JSONObject jObj = new JSONObject(errorBody);

                message = jObj.getString("error");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred! Check LogCat.";
        }

        /*Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();*/
    }


    public class MyClickHandlers {

        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void addNote(View view) {
            showNoteDialog(false, null, -1);
        }

    }


    private void registerUser(String auth){
        viewModel.registerUser(auth).observe(this,user -> {
            if(user != null){
                PrefUtils.storeApiKey(this, user.getApiKey());
                Log.e(TAG, "Device is registered successfully! ApiKey: " +
                        PrefUtils.getApiKey(this));
            }

        });
    }

    private void fetchAllNotes(String auth){
        viewModel.getListLiveData(auth).observe(this, notes -> {
            if(notes != null){
                noteList.clear();
                noteList.addAll(notes);
                noteAdapter.notifyDataSetChanged();
                toggleEmptyNotes();
            }
        });
    }

    private void createNote(String auth, String note){
        viewModel.addNote(auth, note).observe(this, note1 -> {
            noteList.add(0, note1);
            noteAdapter.notifyItemInserted(0);
        });
    }

    private void updateNote(String auth, int noteId, final String note, final int position){
        viewModel.updateNote(auth,noteId,note,position);
            Note n = noteList.get(position);
            n.setNote(note);

            //Update note
            noteList.set(position, n);
    }

    private void deleteNote(String auth, final int noteId, final int position){
        viewModel.deleteNote(auth,noteId,position);

            //Remove a note
            noteList.remove(position);
            noteAdapter.notifyItemRemoved(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
