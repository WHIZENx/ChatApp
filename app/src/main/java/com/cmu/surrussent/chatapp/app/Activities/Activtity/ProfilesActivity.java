package com.cmu.surrussent.chatapp.app.Activities.Activtity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Activities.Constant;
import com.cmu.surrussent.chatapp.app.Activities.Fragments.LoversFragment;
import com.cmu.surrussent.chatapp.app.Activities.Fragments.PostsFragment;
import com.cmu.surrussent.chatapp.app.Activities.SherePref;
import com.cmu.surrussent.chatapp.app.Chat.Fragments.UsersFragment;
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

public class ProfilesActivity extends AppCompatActivity {

    SherePref sherePref;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Constant constant;
    SharedPreferences app_preferences;
    int appTheme_home;
    int themeColor;
    int appColor;

    BottomNavigationView bottomNav;
    ProgressBar mainProgress;

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
        setContentView(R.layout.activity_profiles);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PostsFragment()).commit();
        }

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        mainProgress = (ProgressBar) findViewById(R.id.progress_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

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
                mainProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TextView txt_count_love = findViewById(R.id.txt_lover);

        reference = FirebaseDatabase.getInstance().getReference("Lovers").child(firebaseUser.getUid());

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
                txt_count_love.setText("Loves: " + love);
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
                    if (post.getUserId().equals(firebaseUser.getUid())) {
                        posts++;
                    }
                }
                txt_count_post.setText("Posts: " + posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_posts:
                            if (!item.isChecked()) {
                                selectedFragment = new PostsFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            }
                            break;
                        case R.id.nav_lovers:
                            if (!item.isChecked()) {
                                selectedFragment = new LoversFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            }
                            break;
                    }

                    return true;
                }
            };

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

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
