package com.example.blogs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference dbBlog, dbUsers;
    private Query BlogQuery;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mProgress;

    private int messageCount = 0;
    private static Uri alarmSound;
    private final long[] pattern = {100, 300, 300, 300};
    private NotificationManager mNotificationManager;
    String  __APPKEY = "MAINAPP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        //progress
        mProgress = new ProgressDialog(this);

        dbBlog = FirebaseDatabase.getInstance().getReference().child("Blogs");
        BlogQuery = dbBlog.orderByChild("waktu");

        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        // Login check
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    //Move to Login Activity
                    //Clear top means user cannot go back to prev activity
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity1.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mBlogList = (RecyclerView)findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        dbBlog.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!__APPKEY.equals("MAINAPP")){
                        notif();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void notif() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);

        mBuilder.setContentTitle("My Blog");
        mBuilder.setContentText("Ada berita baru...jangan ketinggalan");
        mBuilder.setTicker("New message alert");
        mBuilder.setSmallIcon(R.drawable.ic_myapp);

        mBuilder.setNumber(++messageCount);
        mBuilder.setSound(alarmSound);
        mBuilder.setVibrate(pattern);

        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        i.putExtra("notificationId", 111);
        i.putExtra("message", "http://walteratapukan.xyz");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DetailActivity.class);

        stackBuilder.addNextIntent(i);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager.notify(111, mBuilder.build());
    }
    private void fireSearch(String search){
//        "\uf8ff");
       //Query fireQuery = dbBlog.orderByChild("search").startAt(search + "\uf8ff");
        Query fireQuery = dbBlog.orderByChild("search").startAt(search).endAt(search+"\uf88ff");

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                fireQuery) {
            @Override
            protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                final String postKey = getRef(i).getKey();

                blogViewHolder.setTitle(blog.getTitle());
                blogViewHolder.setDescription(blog.getDescription());
                blogViewHolder.setDate(blog.getDate());
                blogViewHolder.setImage(getApplicationContext(),blog.getImage());
                //Onclick card
                blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                        intent.putExtra("blog_id",postKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExist();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                BlogQuery) {
            @Override
            protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                final String postKey = getRef(i).getKey();

                blogViewHolder.setTitle(blog.getTitle());
                blogViewHolder.setDescription(blog.getDescription());
                blogViewHolder.setDate(blog.getDate());
                blogViewHolder.setImage(getApplicationContext(),blog.getImage());
                //Onclick card
                blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                        intent.putExtra("blog_id",postKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }
    //Check user exist or not
    private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            dbUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChild(userId)){
                        mProgress.dismiss();
                        Toast.makeText(MainActivity.this, "Login berhasil ...", Toast.LENGTH_LONG).show();
                        //Clear top means user cannot go back to prev activity
                        Intent mainIntent = new Intent(MainActivity.this, AccountActivity.class);
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

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){
            TextView postTitle = (TextView)mView.findViewById(R.id.post_title);
            postTitle.setText(title);
        }

        public void setDescription(String desc){
            TextView postDesc = (TextView)mView.findViewById(R.id.post_text);
            postDesc.setText(desc);
        }
        public void setDate(String date){
            TextView textView3 = (TextView)mView.findViewById(R.id.textView3);
            textView3.setText(date);
        }

        public void setImage(Context ctx, String image){
            ImageView imageView = (ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fireSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fireSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //select actoin to button add
        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId() == R.id.action_tentang){
            startActivity(new Intent(MainActivity.this,AboutActivity.class));
        }
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this,AccountActivity.class);
            intent.putExtra("email",mAuth.getCurrentUser().getEmail());
            startActivity(intent);
        }
        if(item.getItemId() == R.id.action_logout){
            mAuth.signOut();
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity1.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}