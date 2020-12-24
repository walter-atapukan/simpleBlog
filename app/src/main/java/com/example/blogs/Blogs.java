package com.example.blogs;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Blogs extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
