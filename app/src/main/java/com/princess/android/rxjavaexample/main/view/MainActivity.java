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
import com.princess.android.rxjavaexample.data.model.User;
import com.princess.android.rxjavaexample.di.components.ActivityComponent;
import com.princess.android.rxjavaexample.network.ApiClient;
import com.princess.android.rxjavaexample.network.ApiService;
import com.princess.android.rxjavaexample.utils.MyDividerItemDecoration;
import com.princess.android.rxjavaexample.utils.PrefUtils;
import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.databinding.ActivityMainBinding;
import com.princess.android.rxjavaexample.utils.RecyclerTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private Context context;
    private ApiService apiService;
    private List<Note> noteList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private MyClickHandlers handlers;
    TextView noNoteView;

    //@Inject NoteViewModel viewModel;

    String note = "Seriously, fixing bugs sucks! :)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ActivityComponent.component(this).inject(this);
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);


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
            registerUser(this);
        } else {
            fetchAllNotes();
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
                updateNote(note.getId(), inputNote.getText().toString(), position);
            } else {
                // create new note
                createNote(inputNote.getText().toString());
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
                deleteNote(noteList.get(position).getId(), position);
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
            Log.e("ADD NOTE","Fab clicked");
            showNoteDialog(false, null, -1);
        }

    }

    //These are supposed to be in the Repository
    /**
     * Registering new user
     * sending unique id as device identification
     **/
    public void registerUser(Context context){
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
                                PrefUtils.storeApiKey(context, user.getApiKey());
                                Log.e(TAG, "Device is registered successfully! ApiKey: " +
                                        PrefUtils.getApiKey(context));
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
                        .map(notes -> {
                            //Sort the notes by Id
                            Collections.sort(notes, (n1, n2) -> n2.getId() - n1.getId());
                            return notes;
                        })
                        .subscribeWith(new DisposableSingleObserver<List<Note>>() {
                            @Override
                            public void onSuccess(List<Note> notes) {
                                noteList.clear();
                                noteList.addAll(notes);
                                noteAdapter.notifyDataSetChanged();
                                toggleEmptyNotes();
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
                                Log.e(TAG, "New note created: " + note.getId() +" " + note.getNote() +
                                        note.getTimestamp());
                                //Add new Item
                                noteList.add(0, note);
                                noteAdapter.notifyItemInserted(0);
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
                                noteAdapter.notifyItemChanged(position);
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
                                noteAdapter.notifyItemRemoved(position);

                                toggleEmptyNotes();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Error: " + e.getMessage());
                            }
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
