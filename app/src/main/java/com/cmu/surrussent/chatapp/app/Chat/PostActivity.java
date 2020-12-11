package com.cmu.surrussent.chatapp.app.Chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Activities.Constant;
import com.cmu.surrussent.chatapp.app.Activities.SherePref;
import com.cmu.surrussent.chatapp.app.Chat.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    ImageView img_post;
    ImageView img_post_img;
    ImageView img_del;

    TextView title_bar;
    TextView txt_title;
    TextView txt_date;
    TextView txt_des;
    TextView txt_view;

    FirebaseUser fuser;
    DatabaseReference reference;

    SherePref sherePref;

    Intent intent;

    String userid;

    Constant constant;
    SharedPreferences app_preferences;
    int appTheme_home;
    int themeColor;
    int appColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sherePref = new SherePref(this);
        if (sherePref.loadNightModeState()==true) {
            setTheme(R.style.darktheme_home);
        }
        else setTheme(R.style.AppTheme_Home);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme_home = app_preferences.getInt("theme_home", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme_home);
        }else if (appTheme_home == 0){
            setTheme(Constant.theme_home);
        }else{
            setTheme(appTheme_home);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title_bar = findViewById(R.id.post_title_bar);
        img_post = findViewById(R.id.img_post);
        img_post_img = findViewById(R.id.img_post_img);
        txt_title = findViewById(R.id.txt_title);
        txt_date = findViewById(R.id.txt_date);
        txt_des = findViewById(R.id.txt_des);
        txt_view = findViewById(R.id.txt_view);

        intent = getIntent();
        userid = intent.getStringExtra("postKey");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Posts").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);
                title_bar.setText(post.getUserName());

                txt_title.setText(post.getTitle());
                txt_date.setText(post.getPostDate());
                txt_des.setText(post.getDescription());
                txt_view.setText("View: " + post.getUserView());
                Glide.with(getApplicationContext()).load(post.getUserPhoto()).into(img_post);
                Glide.with(getApplicationContext()).load(post.getPicture()).into(img_post_img);

                img_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (post.getUserId().equals(fuser.getUid())) {
                            customDialog("Delete Post?","Do you want to delete this post it will be permanent delete?", "cancelMethod1","okMethod1");
                        } else {
                            Toast.makeText(getApplicationContext(), "You not own this post!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cancelMethod1(){
    }
    private void okMethod1(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                Toast.makeText(getApplicationContext(), "Post delete successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Custom alert dialog that will execute method in the class
     * @param title
     * @param message
     * @param cancelMethod
     * @param okMethod
     */
    public void customDialog(String title, String message, final String cancelMethod, final String okMethod){
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
        builderSingle.setIcon(R.mipmap.ic_notification);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(cancelMethod.equals("cancelMethod1")){
                            cancelMethod1();
                        }
                    }
                });

        builderSingle.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(okMethod.equals("okMethod1")){
                            okMethod1();
                        }
                    }
                });


        builderSingle.show();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
