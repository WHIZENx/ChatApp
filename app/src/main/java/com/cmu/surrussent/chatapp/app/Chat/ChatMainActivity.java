package com.cmu.surrussent.chatapp.app.Chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Activities.Activtity.AnotherActivity;
import com.cmu.surrussent.chatapp.app.Activities.BottomMenuHelper;
import com.cmu.surrussent.chatapp.app.Activities.Constant;
import com.cmu.surrussent.chatapp.app.Activities.LoginActivity;
import com.cmu.surrussent.chatapp.app.Activities.SettingActivity;
import com.cmu.surrussent.chatapp.app.Activities.SherePref;
import com.cmu.surrussent.chatapp.app.Chat.Fragments.ChatsFragment;
import com.cmu.surrussent.chatapp.app.Chat.Fragments.PostFragment;
import com.cmu.surrussent.chatapp.app.Chat.Fragments.UsersFragment;
import com.cmu.surrussent.chatapp.app.Chat.Model.Chat;
import com.cmu.surrussent.chatapp.app.Chat.Model.User;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMainActivity extends AppCompatActivity {

    SherePref sherePref;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private long backPressedTime;

    private int[] tabIcons = {
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_chat_black_24dp,
            R.drawable.ic_group_black_24dp,
    };

    Constant constant;
    SharedPreferences app_preferences;
    int appTheme_home;
    int themeColor;
    int appColor;

    BottomNavigationView bottomNav;
    ProgressBar mainProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        final ProgressDialog dialog;
//        dialog = new ProgressDialog(ChatMainActivity.this);
//        dialog.setTitle("Logging In...");
//        dialog.setMessage("Please wait while we check your internet.");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
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
        setContentView(R.layout.activity_home2);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PostFragment()).commit();
        }

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        mainProgress = (ProgressBar) findViewById(R.id.progress_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                try {
                    Badges.setBadge(ChatMainActivity.this, 0);
                } catch (BadgesNotSupportedException badgesNotSupportedException) {
                }
                try {
                    BottomMenuHelper.removeBadge(bottomNav, R.id.nav_chats);
                } catch (Exception e) {
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                        unread++;
                        try {
                            Badges.setBadge(ChatMainActivity.this, unread);
                        } catch (BadgesNotSupportedException error) {

                        }
                    }
                }
//
//                viewPagerAdapter.addFragment(new PostFragment(), "Home");
//
                if (unread == 0) {
                    BottomMenuHelper.removeBadge(bottomNav, R.id.nav_chats);
                } else if (unread >= 100) {
                    BottomMenuHelper.showBadge(getApplicationContext(), bottomNav, R.id.nav_chats, "99");
                } else {
                    BottomMenuHelper.showBadge(getApplicationContext(), bottomNav, R.id.nav_chats, Integer.toString(unread));
                }
//
//                viewPagerAdapter.addFragment(new UsersFragment(), "Users");
//
//                viewPager.setAdapter(viewPagerAdapter);

//                tabLayout.setupWithViewPager(viewPager);
//                tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//                tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//                tabLayout.getTabAt(2).setIcon(tabIcons[2]);

//                dialog.dismiss();

                mainProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(false);
                        int id = menuItem.getItemId();

                        if (id == R.id.nav_profile) {
                            menuItem.setChecked(true);
                            Intent intent = new Intent(getApplicationContext(), AnotherActivity.class);
                            intent.putExtra("userid", firebaseUser.getUid());
                            startActivity(intent);
                        } else if (id == R.id.nav_settings) {

                            startActivity(new Intent(ChatMainActivity.this, SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            menuItem.setChecked(true);

                        } else if (id == R.id.nav_signout) {
                            menuItem.setChecked(true);
                            customDialog("Sign Out?", "Do you want to sign out?", "cancelMethod1", "okMethod1");
                            return true;
                        }

                        drawerLayout.closeDrawers();
                        return true;
                    }
                });


        updateNavHeader();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            if (!item.isChecked()) {
                                selectedFragment = new PostFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            }
                            break;
                        case R.id.nav_chats:
                            if (!item.isChecked()) {
                                selectedFragment = new ChatsFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            }
                            break;
                        case R.id.nav_search:
                            if (!item.isChecked()) {
                                selectedFragment = new UsersFragment();
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

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit!", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    public void updateNavHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        final ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(firebaseUser.getEmail());
        navUsername.setText(firebaseUser.getDisplayName());

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(navUserPhot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void cancelMethod1(){
    }
    private void okMethod1(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ChatMainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    /**
     * Custom alert dialog that will execute method in the class
     * @param title
     * @param message
     * @param cancelMethod
     * @param okMethod
     */
    public void customDialog(String title, String message, final String cancelMethod, final String okMethod){
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(ChatMainActivity.this);
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
}
