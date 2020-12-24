package com.example.blogs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity1 extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginButton, loginRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference dbUsers;
    private ProgressDialog mProgress;
    private SignInButton mGoogleBtn;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleClient;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        loginEmail = (EditText)findViewById(R.id.loginEmail);
        loginPassword = (EditText)findViewById(R.id.loginPassword);
        loginButton = (Button)findViewById(R.id.loginButton);



        mAuth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUsers.keepSynced(true);
        //progress
        mProgress = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
//        loginRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Clear top means user cannot go back to prev activity
//                Intent move = new Intent(LoginActivity1.this, RegisterActivity.class);
//                move.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(move);
//            }
//        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private void checkLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            loginEmail.requestFocus();
            Toast.makeText(LoginActivity1.this, "Email diperlukan...", Toast.LENGTH_LONG).show();
        }else if(!isValidEmail(email)){
            loginEmail.requestFocus();
            Toast.makeText(LoginActivity1.this, "Email tidak valid...", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(password)){
            loginPassword.requestFocus();
            Toast.makeText(LoginActivity1.this, "Password diperlukan...", Toast.LENGTH_LONG).show();
        }
        else if(loginPassword.length() <6){
            loginPassword.requestFocus();
            Toast.makeText(LoginActivity1.this, "Password minimal 6 digit", Toast.LENGTH_LONG).show();
        }
        //If email and password fields are not empty
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Cek data...");
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Check user exist or not from database
                            checkUserExist();
                        }else{
                            mProgress.dismiss();
                            Toast.makeText(LoginActivity1.this, "Login gagal, data tidak valid", Toast.LENGTH_LONG).show();
                        }
                }
            });
        }
    }

    private void checkUserExist() {
        String userId = mAuth.getCurrentUser().getUid();

        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userId)){
                    mProgress.dismiss();
                    //Clear top means user cannot go back to prev activity
                    Intent mainIntent = new Intent(LoginActivity1.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    Toast.makeText(LoginActivity1.this, "Login berhasil ...", Toast.LENGTH_LONG).show();
                }else{
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity1.this, "Email belum terdaftar...", Toast.LENGTH_LONG).show();
                    //Clear top means user cannot go back to prev activity
                    Intent mainIntent = new Intent(LoginActivity1.this, AccountActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}