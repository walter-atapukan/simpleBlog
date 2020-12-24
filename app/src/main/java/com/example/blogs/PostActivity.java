package com.example.blogs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUREST = 1;
    Uri imageUri,downloadUrl;
    final String randomKey = UUID.randomUUID().toString();
    //firebase storage
    private StorageReference mStorage, imagesRef;
    //firebase database
    private DatabaseReference dbBlog, dbUsers, LastDb;

    private ImageButton selectImage;
    private EditText postTitle, postDesc;
    private Button btnSave;
    private TextView tvDate;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //set title bar
        setTitle("Tambah berita");

        //find id of components
        selectImage = (ImageButton)findViewById(R.id.imageSelect);
        postTitle = (EditText)findViewById(R.id.titleField);
        postDesc = (EditText)findViewById(R.id.descField);
        btnSave = (Button)findViewById(R.id.btnSubmit);

        //firebase referense storage
        mStorage = FirebaseStorage.getInstance().getReference();
        imagesRef = mStorage.child("Blog_Images");

        //firebase database
        dbBlog = FirebaseDatabase.getInstance().getReference().child("Blogs");
        LastDb = FirebaseDatabase.getInstance().getReference().child("Last_post");
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        dbBlog.keepSynced(true);
        LastDb.keepSynced(true);
        dbUsers.keepSynced(true);
        //progress
        mProgress = new ProgressDialog(this);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUREST);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostingData();
            }
        });

    }

    private void PostingData() {
        //set progress dialong
        mProgress.setMessage("Menambah data");

        //get value from components
        String titleVal = postTitle.getText().toString().trim();
        String descVal = postDesc.getText().toString().trim();

        //simple validation
        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && imageUri != null){
            mProgress.show();
            StorageReference filepath = mStorage.child("Blog_Images").child(randomKey+".jpg");

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onSuccess(Uri uri) {

                            downloadUrl = uri;

                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date date = new Date();

                            DatabaseReference newPostId = dbBlog.push();

                            newPostId.child("title").setValue(titleVal);
                            newPostId.child("description").setValue(descVal);
                            newPostId.child("image").setValue(downloadUrl.toString());
                            newPostId.child("id").setValue(newPostId.getKey());
                            newPostId.child("date").setValue(dateFormat.format(date));
                            newPostId.child("waktu").setValue(date.getTime());
                            newPostId.child("penulis").setValue("Admin");
                            newPostId.child("search").setValue(titleVal.toLowerCase());

                            LastDb.child("id").setValue(newPostId.getKey());
//                            LastDb.child("title").setValue(titleVal);

                            //hide progress
                            mProgress.dismiss();
                            //Show toast
                            Toast.makeText(PostActivity.this, "Berita Berhasil Ditambah", Toast.LENGTH_LONG).show();
                            //Clear top means user cannot go back to prev activity
                            Intent loginIntent = new Intent(PostActivity.this, MainActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginIntent);
                        }
                    });

                }
            });

        }else{
            Toast.makeText(this, "Harap mengisi semua data...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUREST && resultCode == RESULT_OK){

            imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(2,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                selectImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}//end