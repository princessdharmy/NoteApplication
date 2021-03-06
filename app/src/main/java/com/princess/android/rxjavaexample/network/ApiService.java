package com.princess.android.rxjavaexample.network;

import com.princess.android.rxjavaexample.data.model.Note;
import com.princess.android.rxjavaexample.data.model.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiService {

    //Register new user
    @FormUrlEncoded
    @POST("notes/user/register")
    Single<User> register(@Header("Authorization") String Authorisation, @Field("device_id") String deviceId);

    //Create note
    @FormUrlEncoded
    @POST("notes/new")
    Single<Note> createNote(@Header("Authorization") String Authorisation, @Field("note") String note);

    // Fetch all notes
    @GET("notes/all")
    Single<List<Note>> fetchAllNotes(@Header("Authorization") String Authorisation);

    // Update single note
    @FormUrlEncoded
    @PUT("notes/{id}")
    Completable updateNote(@Header("Authorization") String Authorisation, @Path("id") int noteId, @Field("note") String note);

    // Delete note
    @DELETE("notes/{id}")
    Completable deleteNote(@Header("Authorization") String Authorisation, @Path("id") int noteId);
}
