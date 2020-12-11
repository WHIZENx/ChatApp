package com.cmu.surrussent.chatapp.app.Chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Activities.Activtity.AnotherActivity;
import com.cmu.surrussent.chatapp.app.Activities.Constant;
import com.cmu.surrussent.chatapp.app.Activities.SherePref;
import com.cmu.surrussent.chatapp.app.Chat.Adapter.MessageAdapter;
import com.cmu.surrussent.chatapp.app.Chat.Fragments.APIService;
import com.cmu.surrussent.chatapp.app.Chat.Model.Chat;
import com.cmu.surrussent.chatapp.app.Chat.Model.Type;
import com.cmu.surrussent.chatapp.app.Chat.Model.User;
import com.cmu.surrussent.chatapp.app.Chat.Model.Viewer;
import com.cmu.surrussent.chatapp.app.Chat.Notifications.Client;
import com.cmu.surrussent.chatapp.app.Chat.Notifications.Data;
import com.cmu.surrussent.chatapp.app.Chat.Notifications.MyResponse;
import com.cmu.surrussent.chatapp.app.Chat.Notifications.Sender;
import com.cmu.surrussent.chatapp.app.Chat.Notifications.Token;
import com.google.android.gms.flags.Flag;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;
    DatabaseReference ref;

    CircleImageView img_on;
    CircleImageView img_off;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String userid;

    SherePref sherePref;

    APIService apiService;

    boolean notify = false;

    Constant constant;
    SharedPreferences app_preferences;
    int appTheme_home;
    int themeColor;
    int appColor;

    AnimatorSet mAnimationSet;

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
        setContentView(R.layout.activity_chat_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        img_off = findViewById(R.id.img_off);
        img_on = findViewById(R.id.img_on);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Viewer").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Viewer uservier = dataSnapshot.getValue(Viewer.class);
                if (!dataSnapshot.exists()) {
                    Viewer viewer = new Viewer(fuser.getUid(),
                            userid);

                    addView(viewer, fuser.getUid());
                } else {
                    reference = FirebaseDatabase.getInstance().getReference("Viewer").child(userid);

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Viewer getviewer = dataSnapshot.getValue(Viewer.class);

                            mAnimationSet = new AnimatorSet();

                            try {
                                if (uservier.getviewId().equals(userid) && getviewer.getviewId().equals(fuser.getUid())) {

                                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(img_on, "alpha", 1f, .3f);
                                    fadeOut.setDuration(1000);
                                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(img_on, "alpha", .3f, 1f);
                                    fadeIn.setDuration(1000);

                                    mAnimationSet.play(fadeIn).after(fadeOut);

                                    mAnimationSet.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (uservier.getviewId().equals(userid) && getviewer.getviewId().equals(fuser.getUid())) {
                                                mAnimationSet.start();
                                            }
                                        }
                                    });
                                    mAnimationSet.start();
                                } else {
                                    mAnimationSet.removeAllListeners();
                                    mAnimationSet.cancel();
                                    mAnimationSet.end();
                                }
                            } catch (Exception e) {
                                Viewer viewer = new Viewer(userid,
                                        "null");

                                addView(viewer, userid);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        final RelativeLayout rev = findViewById(R.id.type_hide);
//
//        text_send.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                reference = FirebaseDatabase.getInstance().getReference("Type").child(fuser.getUid()).child(userid);
//
//                reference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        final Type typer = dataSnapshot.getValue(Type.class);
//                        String msg = text_send.getText().toString();
//                        if (!dataSnapshot.exists()) {
//                            Type type = new Type(fuser.getUid(),
//                                    fuser.getDisplayName(),
//                                    userid,
//                                    msg);
//
//                            addType(type, fuser.getUid(), userid);
//                        } else {
//
//                            ref = FirebaseDatabase.getInstance().getReference("Type").child(fuser.getUid()).child(userid);
//                            HashMap<String, Object> hashMap2 = new HashMap<>();
//                            hashMap2.put("msg", msg);
//                            ref.updateChildren(hashMap2);
//
//                        }
//
//                        if (!msg.equals("") && typer.getviewId().equals(userid)) {
//                            rev.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                rev.setVisibility(View.GONE);
//            }
//        });



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    String timezoneID = TimeZone.getDefault().getID();
                    TimeZone timeZone = TimeZone.getTimeZone(timezoneID);
                    Calendar c = Calendar.getInstance(timeZone);

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy\n      HH:mm");
                    String formattedDate = df.format(c.getTime());

                    sendMessage(fuser.getUid(), userid, msg, formattedDate);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
                ref = FirebaseDatabase.getInstance().getReference("Type").child(fuser.getUid()).child(userid);
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("msg", "");
                ref.updateChildren(hashMap2);
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                profile_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AnotherActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
                        intent.putExtra("userid", userid);
                        getApplicationContext().startActivity(intent);
                    }
                });

                if (user.getStatus().equals("online")) {
                    img_on.setVisibility(View.VISIBLE);
                    img_off.setVisibility(View.GONE);
                } else {
                    img_on.setVisibility(View.GONE);
                    img_off.setVisibility(View.VISIBLE);
                }

                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message, String date){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("date", date);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);


        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, message, username,
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addType(Type type, String userId, String viewId) {

        if (!userId.isEmpty()) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Type").child(userId).child(viewId);

            myRef.setValue(type).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }

    }

    private void addView(Viewer viewer, String userId) {

        if (!userId.isEmpty()) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Viewer").child(userId);

            myRef.setValue(viewer).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }

    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    private void viwer(String viewId){
        reference = FirebaseDatabase.getInstance().getReference("Viewer").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("viewId", viewId);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        viwer(userid);
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        viwer("null");
        currentUser("none");
        try {
            mAnimationSet.removeAllListeners();
            mAnimationSet.cancel();
            mAnimationSet.end();
        } catch (Exception e) {
        }
    }
}
