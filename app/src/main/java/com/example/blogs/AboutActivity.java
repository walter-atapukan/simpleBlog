package com.example.blogs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {
    EditText editParagraph, editVersi;
    Button ubahInformasi;

    private DatabaseReference dbSetting;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("Tentang aplikasi");

        editParagraph = (EditText)findViewById(R.id.editParagraph);
        editVersi = (EditText)findViewById(R.id.editVersi);
        ubahInformasi = (Button)findViewById(R.id.ubahInformasi);

        dbSetting = FirebaseDatabase.getInstance().getReference().child("Pengaturan");

        //progress
        mProgress = new ProgressDialog(this);

        dbSetting.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                String deskripsi = snapshot.child("deskripsi").getValue().toString();
                String versi = snapshot.child("versi").getValue().toString();

                    editParagraph.setText(deskripsi);
                    editVersi.setText(versi);
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ubahInformasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set progress dialong
                mProgress.setMessage("Mengupdate deskripsi aplikasi...");
                mProgress.show();
                String paragraph = editParagraph.getText().toString().trim();
                String versi = editVersi.getText().toString().trim();

                dbSetting.child("deskripsi").setValue(paragraph);
                dbSetting.child("versi").setValue(versi);

                //hide progress
                mProgress.dismiss();
                //Show toast
                Toast.makeText(AboutActivity.this, "Deskripsi aplikasi Berhasil Diubah", Toast.LENGTH_LONG).show();
                //Clear top means user cannot go back to prev activity
                Intent loginIntent = new Intent(AboutActivity.this, MainActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }
        });

    }
}