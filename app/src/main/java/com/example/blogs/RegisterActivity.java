package com.example.blogs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerEmail, registerPassword;
    private Button registerRegister, registerLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference dbUsers;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Selamat datang di simpleBlog");

        registerPassword = (EditText)findViewById(R.id.registerEmail);
        registerEmail = (EditText)findViewById(R.id.registerPassword);
        registerLogin = (Button)findViewById(R.id.registerRegister);
        registerRegister = (Button)findViewById(R.id.registerLogin);

        mAuth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress = new ProgressDialog(this);

        registerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
        registerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear top means user cannot go back to prev activity
                Intent move = new Intent(RegisterActivity.this, LoginActivity1.class);
                move.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(move);
            }
        });
    }

    private void startRegister() {
        String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress.setMessage("Mendaftar...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb = dbUsers.child(userId);
                        currentUserDb.child("email").setValue(email);
                        currentUserDb.child("id").setValue(userId);

                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Berhasil mendaftar, Silahkan login", Toast.LENGTH_LONG).show();
                        //Clear top means user cannot go back to prev activity
                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity1.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }else{
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Gagal mendaftar...", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}