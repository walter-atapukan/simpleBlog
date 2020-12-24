package com.example.blogs;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

public class DetailActivity extends AppCompatActivity {
    private static final int GALLERY_REQUREST = 1;
    Uri imageUri,downloadUrl;
    final String randomKey = UUID.randomUUID().toString();
    //to save postKey value
    private  String postKey = null;
    //db reference
    private DatabaseReference dbBlog, dbBlogID;
    Button btnDelete, btnUpdate;

    EditText detailDescription, updateTitle;
    ImageButton imageView;
    private ProgressDialog mProgress;
    //firebase storage
    private StorageReference mStorage, imagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //progress
        mProgress = new ProgressDialog(this);
        //Get string
        postKey = getIntent().getExtras().getString("blog_id");
        //get compenent with id
        detailDescription = (EditText)findViewById(R.id.detailDescription);
        updateTitle = (EditText)findViewById(R.id.update_title);
        imageView = (ImageButton)findViewById(R.id.detailImage) ;
        btnDelete = (Button)findViewById(R.id.btn_delete_blog);
        btnUpdate = (Button)findViewById(R.id.btn_update_blog);

        //firebase referense storage
        mStorage = FirebaseStorage.getInstance().getReference();
        imagesRef = mStorage.child("Blog_Images");
        //set text to scrollable
        detailDescription.setMovementMethod(new ScrollingMovementMethod());

        dbBlog = FirebaseDatabase.getInstance().getReference().child("Blogs");
        dbBlogID = dbBlog.child(postKey);

        dbBlog.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(postKey)) {
                    dbBlogID.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                            String postTitle = snapshot.child("title").getValue().toString();
                                String postDesc = snapshot.child("description").getValue().toString();
                                String postImage = snapshot.child("image").getValue().toString();
                                setTitle("Detail Berita");
                                detailDescription.setText(postDesc);
                                updateTitle.setText(postTitle);
                                Picasso.with(DetailActivity.this).load(postImage).into(imageView);
                            }   }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(DetailActivity.this, "Blog tidak ada", Toast.LENGTH_LONG).show();

                    Intent loginIntent = new Intent(DetailActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUREST);
            }
        });
        //OnClik btn Delete
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Menghapus data ...");
                mProgress.show();
                dbBlogID.removeValue();

                mProgress.dismiss();
                Toast.makeText(DetailActivity.this, "Berhasil Hapus Berita", Toast.LENGTH_LONG).show();

                Intent loginIntent = new Intent(DetailActivity.this, MainActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);



            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostingData();
            }
        });
        receiveData();


    }
    public void receiveData() {
        String message = "";
        int id = 0;
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            message = "error";
        }else{
            id = extras.getInt("notificationId");
            message = extras.getString("message");
        }
    }
    private void PostingData() {
        //set progress dialong
        mProgress.setMessage("Mengupdate Berita...");
        mProgress.show();
        //get value from components
        String titleVal = updateTitle.getText().toString().trim();
        String descVal = detailDescription.getText().toString().trim();

        //simple validation  && imageUri != null
        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal)){
            if(imageUri != null){
                StorageReference filepath = mStorage.child("Blog_Images").child(randomKey+".jpg");

                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onSuccess(Uri uri) {

                                downloadUrl = uri;

                                dbBlogID.child("description").setValue(descVal);
                                dbBlogID.child("title").setValue(titleVal);
                                dbBlogID.child("search").setValue(titleVal.toLowerCase());
                                dbBlogID.child("image").setValue(downloadUrl.toString());
                                dbBlogID.child("search").setValue(titleVal.toLowerCase());

                                //hide progress
                                mProgress.dismiss();
                                //Show toast
                                Toast.makeText(DetailActivity.this, "Berita Berhasil Diubah", Toast.LENGTH_LONG).show();
                                //Clear top means user cannot go back to prev activity
                                Intent loginIntent = new Intent(DetailActivity.this, MainActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(loginIntent);
                            }
                        });

                    }
                });
            }else{
                dbBlogID.child("description").setValue(descVal);
                dbBlogID.child("title").setValue(titleVal);
                dbBlogID.child("search").setValue(titleVal.toLowerCase());

                //hide progress
                mProgress.dismiss();
                //Show toast
                Toast.makeText(DetailActivity.this, "Berita Berhasil Diubah", Toast.LENGTH_LONG).show();
                //Clear top means user cannot go back to prev activity
                Intent loginIntent = new Intent(DetailActivity.this, MainActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }

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
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}