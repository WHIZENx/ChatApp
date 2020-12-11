package com.cmu.surrussent.chatapp.app.Chat.Popup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Activities.Activtity.AnotherActivity;
import com.cmu.surrussent.chatapp.app.Activities.Constant;
import com.cmu.surrussent.chatapp.app.Activities.SherePref;
import com.cmu.surrussent.chatapp.app.Chat.Adapter.CommendAdapter;
import com.cmu.surrussent.chatapp.app.Chat.Model.Commend;
import com.cmu.surrussent.chatapp.app.Chat.Model.Post;
import com.cmu.surrussent.chatapp.app.Chat.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class PostsActivity extends AppCompatActivity {

    private FirebaseUser fuser;
    private DatabaseReference reference;
    private DatabaseReference databasereference;
    private DatabaseReference ref;

    List<Commend> mCommend;
//    RecyclerView commendRecyclerView ;
    CommendAdapter commendAdapter ;

    EditText commend_post;

    Dialog mDialog;

    Intent intent;

    String userid;
    String picture;
    String postkey;
    int userview;
    String userphoto;
    String postdate;
    String username;
    String description;

    SherePref sherePref;

    Constant constant;
    SharedPreferences app_preferences;
    int appTheme_home;
    int themeColor;
    int appColor;

    ImageView img_popup;

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
        setContentView(R.layout.activity_posts);

        ImageView img_post = findViewById(R.id.img_post);
        ImageView img_post_img = findViewById(R.id.img_post_img);
        ImageView img_del = findViewById(R.id.ico_del);
        final ImageView img_commend = findViewById(R.id.img_commend);
        TextView txt_date = findViewById(R.id.txt_date);
        TextView txt_des = findViewById(R.id.txt_des);
        final TextView txt_count_commend = findViewById(R.id.txt_count_commend);
        TextView txt_user = findViewById(R.id.txt_user);
        Button add_commend = findViewById(R.id.add_commend);
        commend_post = findViewById(R.id.commend_post_id);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

//        commendRecyclerView = findViewById(R.id.commendRV);
//        commendRecyclerView.setHasFixedSize(true);
//        commendRecyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        picture = intent.getStringExtra("picture");
        postkey = intent.getStringExtra("postkey");
        userview = intent.getIntExtra("userview", 0);
        userphoto = intent.getStringExtra("userphoto");
        postdate = intent.getStringExtra("postdate");
        username = intent.getStringExtra("username");
        description = intent.getStringExtra("description");

        mDialog = new Dialog(getApplicationContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.popup_img);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT,Toolbar.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        final ImageView img_popup = mDialog.findViewById(R.id.img_popup);

        img_post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getApplicationContext()).load(picture).into(img_popup);
                mDialog.show();
            }
        });

        img_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mDialog.dismiss();
            }
        });

        add_commend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!commend_post.getText().toString().isEmpty()) {

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            String timezoneID = TimeZone.getDefault().getID();
                            TimeZone timeZone = TimeZone.getTimeZone(timezoneID);
                            Calendar c = Calendar.getInstance(timeZone);

                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                            String formattedDate = df.format(c.getTime());

                            // create post Object
                            Commend commend = new Commend(fuser.getDisplayName(),
                                    user.getImageURL(),
                                    formattedDate,
                                    commend_post.getText().toString(),
                                    postkey);

                            addCommend(commend, postkey);
                            commend_post.setText("");

                            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Please write commend to post!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (userid.equals(fuser.getUid())) {
            img_del.setVisibility(View.VISIBLE);
            img_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog("Delete Post?", "Do you want to delete this post it will be permanent delete?", "cancelMethod1", "okMethod1", 0, postkey);
                }
            });
        } else {
            img_del.setVisibility(View.INVISIBLE);
        }

        txt_date.setText("Post Date: " + postdate + " | View: " + userview);
        txt_user.setText(username);
        txt_des.setText(description);
        Glide.with(getApplicationContext()).load(userphoto).into(img_post);
        Glide.with(getApplicationContext()).load(picture).into(img_post_img);

        img_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnotherActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Commends").child(postkey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int commend = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Commend commended = snapshot.getValue(Commend.class);
                    if (commended.getPostKeyId().equals(postkey)) {
                        commend++;
                    }
                }
                if (commend != 0) {
                    txt_count_commend.setText("Comment (" + commend + "):");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(img_commend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Posts").child(postkey);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userView", userview+1);
        reference.updateChildren(hashMap);

        mCommend = new ArrayList<>();

        databasereference = FirebaseDatabase.getInstance().getReference("Commends").child(postkey);

        databasereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCommend.clear();
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Commend commend = postsnap.getValue(Commend.class);
                    mCommend.add(commend);

                }

                commendAdapter = new CommendAdapter(getApplicationContext(), mCommend);
//                commendRecyclerView.setAdapter(commendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addCommend(Commend commend, String postkeyid) {

        if (!commend_post.getText().toString().isEmpty()) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Commends").child(postkeyid).push();

            myRef.setValue(commend).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Commend Successful!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void cancelMethod1(){
    }
    private void okMethod1(final int status, final String getPostkey){

//        if (!photoRef.equals("")) {
//            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                }
//            });
//        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                databasereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        dataSnapshot2.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                databasereference = FirebaseDatabase.getInstance().getReference("Lovers").child(fuser.getUid()).child(getPostkey);

                databasereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        dataSnapshot2.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                dataSnapshot.getRef().removeValue();

                finish();
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
    public void customDialog(String title, String message, final String cancelMethod, final String okMethod, final int status, final String getPostkey){
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
                            okMethod1(status, getPostkey);
                        }
                    }
                });


        builderSingle.show();
    }
}
