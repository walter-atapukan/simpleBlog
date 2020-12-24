package com.example.blogs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {
    //db reference
    private DatabaseReference dbUser;
    String email;
    TextView textEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Informasi akun");

        textEmail = (TextView)findViewById(R.id.textEmail);
        //Get string
        email = getIntent().getExtras().getString("email");
        dbUser = FirebaseDatabase.getInstance().getReference().child("Users");

        textEmail.setText(email);
//        dbBlogID = dbBlog.child(postKey);
    }
}