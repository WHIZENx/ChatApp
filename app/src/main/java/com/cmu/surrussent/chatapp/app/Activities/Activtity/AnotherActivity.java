package com.cmu.surrussent.chatapp.app.Activities.Activtity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Activities.Adapter.MyPagerAdapter;
import com.cmu.surrussent.chatapp.app.Activities.Constant;
import com.cmu.surrussent.chatapp.app.Activities.Fragments.AnotherLovesFragment;
import com.cmu.surrussent.chatapp.app.Activities.Fragments.AnthorPostsFragment;
import com.cmu.surrussent.chatapp.app.Activities.Fragments.LoversFragment;
import com.cmu.surrussent.chatapp.app.Activities.Fragments.PostsFragment;
import com.cmu.surrussent.chatapp.app.Activities.SherePref;
import com.cmu.surrussent.chatapp.app.Chat.Model.Lover;
import com.cmu.surrussent.chatapp.app.Chat.Model.Post;
import com.cmu.surrussent.chatapp.app.Chat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnotherActivity extends AppCompatActivity {

    FirebaseUser fuser;
    DatabaseReference reference;

    CircleImageView profile_image;
    TextView username;

    Intent intent;

    String userid;

    SherePref sherePref;

    Constant constant;
    SharedPreferences app_preferences;
    int appTheme_home;
    int themeColor;
    int appColor;

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sherePref = new SherePref(this);
        if (sherePref.loadNightModeState() == true) {
            setTheme(R.style.darktheme_home);
        } else setTheme(R.style.AppTheme_Home);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme_home = app_preferences.getInt("theme_home", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0) {
            setTheme(Constant.theme_home);
        } else if (appTheme_home == 0) {
            setTheme(Constant.theme_home);
        } else {
            setTheme(appTheme_home);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AnthorPostsFragment()).commit();
        }

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TextView txt_count_love = findViewById(R.id.txt_lover);

        reference = FirebaseDatabase.getInstance().getReference("Lovers").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int love = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Lover lover = snapshot.getValue(Lover.class);
                    if (!lover.getPostKey().equals("")) {
                        love++;
                    }
                }
                txt_count_love.setText("" + love);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TextView txt_count_post = findViewById(R.id.txt_poster);

        reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int posts = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getUserId().equals(userid)) {
                        posts++;
                    }
                }
                txt_count_post.setText("" + posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.fragment_container);

        final ImageView img01 = findViewById(R.id.post_slide);
        final ImageView img02 = findViewById(R.id.lover_slide);

        img01.setImageResource(R.drawable.ic_bookmark_black_24dp);
        img02.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        img01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(0);
            }
        });

        img02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(1);
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    img01.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    img02.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                } else if (position == 1) {
                    img01.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    img02.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        pager.setAdapter(adapter);
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
