package com.cmu.surrussent.chatapp.app.Chat.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Activities.Activtity.AnotherActivity;
import com.cmu.surrussent.chatapp.app.Activities.Activtity.ProfilesActivity;
import com.cmu.surrussent.chatapp.app.Chat.MessageActivity;
import com.cmu.surrussent.chatapp.app.Chat.Model.Commend;
import com.cmu.surrussent.chatapp.app.Chat.Model.Love;
import com.cmu.surrussent.chatapp.app.Chat.Model.Lover;
import com.cmu.surrussent.chatapp.app.Chat.Model.Post;
import com.cmu.surrussent.chatapp.app.Chat.Model.User;
import com.cmu.surrussent.chatapp.app.Chat.Popup.PostsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    public static final int POST_TYPE_POST = 0;
    public static final int POST_TYPE_STATUS = 1;

    private Context mContext;
    private List<Post> mData ;
    List<Commend> mCommend;
    RecyclerView commendRecyclerView ;
    RecyclerView commendRecyclerView2 ;
    CommendAdapter commendAdapter ;

    private FirebaseUser fuser;
    private DatabaseReference reference;
    private DatabaseReference databasereference;
    private DatabaseReference ref;

    Dialog myDialog;
    Dialog myDialog2;
    Dialog mDialog;
    EditText commend_post;

    StorageReference photoRef;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == POST_TYPE_POST) {
            View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_post, parent, false);
            return new PostAdapter.MyViewHolder(row, 0);
        } else {
            View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_status, parent, false);
            return new PostAdapter.MyViewHolder(row, 1);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getPostStatus() == POST_TYPE_POST){
            return POST_TYPE_POST;
        } else {
            return POST_TYPE_STATUS;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!mData.get(position).getPicture().equals("")) {
            photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(mData.get(position).getPicture());
        }

        if (mData.get(position).getPostStatus() == POST_TYPE_POST) {
//            holder.tvTitle.setText(mData.get(position).getTitle());
            holder.tvStatus.setText(mData.get(position).getDescription());
            holder.tvUser.setText(mData.get(position).getUserName());
            holder.tvDate.setText(mData.get(position).getPostDate());
            Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
            Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);

            holder.imgPostProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AnotherActivity.class);
                    intent.putExtra("userid", mData.get(position).getUserId());
                    mContext.startActivity(intent);
                }
            });

            holder.img_love.setTag(0);

            final Integer resource = (Integer) holder.img_love.getTag();

            fuser = FirebaseAuth.getInstance().getCurrentUser();

            reference = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves").child(fuser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Love loved = dataSnapshot.getValue(Love.class);

                    if (!dataSnapshot.exists()) {
                        if (resource == 0) {
                            Love love = new Love(fuser.getUid(),
                                    fuser.getDisplayName(),
                                    mData.get(position).getPostKey(),
                                    0);

                            addLove(love, fuser.getUid(), mData.get(position).getPostKey());
                        }
                    } else {
                        if (loved.getcheck_click() == 0) {
                            holder.img_love.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            holder.img_love.setTag(0);
                        } else {
                            holder.img_love.setImageResource(R.drawable.ic_favorite_black_24dp);
                            holder.img_love.setTag(1);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            reference = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int love = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Love loved = snapshot.getValue(Love.class);
                        if (loved.getcheck_click() == 1) {
                            love++;
                        }
                    }
                    holder.txt_count_love.setText("(" + love + ")");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.img_love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Integer resource = (Integer) holder.img_love.getTag();

                    if (resource == 0) {
                        holder.img_love.setImageResource(R.drawable.ic_favorite_black_24dp);
                        holder.img_love.setTag(1);
                        ref = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves").child(fuser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("check_click", 1);
                        ref.updateChildren(hashMap);

                        Lover lover = new Lover(fuser.getUid(),
                                fuser.getDisplayName(),
                                mData.get(position).getPostKey());

                        addLover(lover, fuser.getUid(), mData.get(position).getPostKey());
                    }
                    if (resource == 1) {
                        holder.img_love.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        holder.img_love.setTag(0);
                        ref = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves").child(fuser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("check_click", 0);
                        ref.updateChildren(hashMap);

                        ref = FirebaseDatabase.getInstance().getReference("Lovers").child(fuser.getUid()).child(mData.get(position).getPostKey());
                        HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("postKey", "");
                        ref.updateChildren(hashMap2);
                    }
                }
            });

            myDialog = new Dialog(mContext,R.style.PauseDialogAnimation);
            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myDialog.setContentView(R.layout.post_popup);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            myDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);

            commendRecyclerView = myDialog.findViewById(R.id.commendRV);
            commendRecyclerView.setHasFixedSize(true);
            commendRecyclerView.setLayoutManager(linearLayoutManager);
            //linearLayoutManager.setReverseLayout(true);
            //linearLayoutManager.setStackFromEnd(true);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Intent intent = new Intent(mContext, PostsActivity.class);
//                    intent.putExtra("userid", mData.get(position).getUserId());
//                    intent.putExtra("picture", mData.get(position).getPicture());
//                    intent.putExtra("postkey", mData.get(position).getPostKey());
//                    intent.putExtra("userview", mData.get(position).getUserView());
//                    intent.putExtra("userphoto", mData.get(position).getUserPhoto());
//                    intent.putExtra("postdate", mData.get(position).getPostDate());
//                    intent.putExtra("username", mData.get(position).getUserName());
//                    intent.putExtra("description", mData.get(position).getDescription());
//                    mContext.startActivity(intent);

                    ImageView img_post = myDialog.findViewById(R.id.img_post);
                    ImageView img_post_img = myDialog.findViewById(R.id.img_post_img);
                    ImageView img_del = myDialog.findViewById(R.id.ico_del);
                    final ImageView img_commend = myDialog.findViewById(R.id.img_commend);
                    TextView txt_date = myDialog.findViewById(R.id.txt_date);
                    TextView txt_des = myDialog.findViewById(R.id.txt_des);
                    final TextView txt_count_commend = myDialog.findViewById(R.id.txt_count_commend);
                    TextView txt_user = myDialog.findViewById(R.id.txt_user);
                    Button add_commend = myDialog.findViewById(R.id.add_commend);
                    commend_post = myDialog.findViewById(R.id.commend_post_id);

                    commend_post.setInputType(InputType.TYPE_NULL);

                    commend_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commend_post.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            commend_post.requestFocus();
                            InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.showSoftInput(commend_post, InputMethodManager.SHOW_FORCED);
                        }
                    });

                    mDialog = new Dialog(mContext);
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mDialog.setContentView(R.layout.popup_img);
                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mDialog.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT,Toolbar.LayoutParams.WRAP_CONTENT);
                    mDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

                    final ImageView img_popup = mDialog.findViewById(R.id.img_popup);
//                    ImageView download_img = mDialog.findViewById(R.id.download_img);

                    img_post_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Glide.with(mContext).load(mData.get(position).getPicture()).into(img_popup);
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
                                                mData.get(position).getPostKey());

                                        addCommend(commend, mData.get(position).getPostKey());
                                        commend_post.setText("");

                                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputManager.hideSoftInputFromWindow(myDialog.getCurrentFocus().getWindowToken(),
                                                InputMethodManager.HIDE_NOT_ALWAYS);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });

                            } else {
                                Toast.makeText(mContext, "Please write commend to post!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    if (mData.get(position).getUserId().equals(fuser.getUid())) {
                        img_del.setVisibility(View.VISIBLE);
                        img_del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog("Delete Post?", "Do you want to delete this post it will be permanent delete?", "cancelMethod1", "okMethod1", 0, mData.get(position).getPostKey());
                            }
                        });
                    } else {
                        img_del.setVisibility(View.INVISIBLE);
                    }

//                    txt_title.setText(mData.get(position).getTitle());
                    txt_date.setText("Post Date: " + mData.get(position).getPostDate() + " | View: " + mData.get(position).getUserView());
                    txt_user.setText(mData.get(position).getUserName());
                    txt_des.setText(mData.get(position).getDescription());
                    Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(img_post);
                    Glide.with(mContext).load(mData.get(position).getPicture()).into(img_post_img);

                    img_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, AnotherActivity.class);
                            intent.putExtra("userid", mData.get(position).getUserId());
                            intent.putExtra("picture", mData.get(position).getPicture());
                            intent.putExtra("postkey", mData.get(position).getPostKey());
                            intent.putExtra("userview", mData.get(position).getUserView());
                            intent.putExtra("userphoto", mData.get(position).getUserPhoto());
                            intent.putExtra("postdate", mData.get(position).getPostDate());
                            intent.putExtra("username", mData.get(position).getUserName());
                            intent.putExtra("description", mData.get(position).getDescription());
                            mContext.startActivity(intent);
                        }
                    });

                    reference = FirebaseDatabase.getInstance().getReference("Commends").child(mData.get(position).getPostKey());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int commend = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Commend commended = snapshot.getValue(Commend.class);
                                if (commended.getPostKeyId().equals(mData.get(position).getPostKey())) {
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
                            if (isValidContextForGlide(mContext)) {
                                Glide.with(mContext).load(user.getImageURL()).into(img_commend);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    reference = FirebaseDatabase.getInstance().getReference("Posts").child(mData.get(position).getPostKey());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("userView", mData.get(position).getUserView() + 1);
                    reference.updateChildren(hashMap);

                    mCommend = new ArrayList<>();

                    databasereference = FirebaseDatabase.getInstance().getReference("Commends").child(mData.get(position).getPostKey());

                    databasereference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mCommend.clear();
                            for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                                Commend commend = postsnap.getValue(Commend.class);
                                mCommend.add(commend);

                            }

                            commendAdapter = new CommendAdapter(mContext, mCommend);
                            commendRecyclerView.setAdapter(commendAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    myDialog.show();
                }
            });
        } else {
            holder.tvSta.setText(mData.get(position).getDescription());
            holder.tvUserStatus.setText(mData.get(position).getUserName());
            holder.tvDateStatus.setText(mData.get(position).getPostDate());
            Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgStatusProfile);

            holder.imgStatusProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AnotherActivity.class);
                    intent.putExtra("userid", mData.get(position).getUserId());
                    mContext.startActivity(intent);
                }
            });

            holder.img_love.setTag(0);

            final Integer resource = (Integer) holder.img_love.getTag();

            fuser = FirebaseAuth.getInstance().getCurrentUser();

            reference = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves").child(fuser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Love loved = dataSnapshot.getValue(Love.class);

                    if (!dataSnapshot.exists()) {
                        if (resource == 0) {
                            Love love = new Love(fuser.getUid(),
                                    fuser.getDisplayName(),
                                    mData.get(position).getPostKey(),
                                    0);

                            addLove(love, fuser.getUid(), mData.get(position).getPostKey());
                        }
                    } else {
                        if (loved.getcheck_click() == 0) {
                            holder.img_love.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            holder.img_love.setTag(0);
                        } else {
                            holder.img_love.setImageResource(R.drawable.ic_favorite_black_24dp);
                            holder.img_love.setTag(1);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            reference = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int love = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Love loved = snapshot.getValue(Love.class);
                        if (loved.getcheck_click() == 1) {
                            love++;
                        }
                    }
                    holder.txt_count_love.setText("(" + love + ")");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.img_love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Integer resource = (Integer) holder.img_love.getTag();

                    if (resource == 0) {
                        holder.img_love.setImageResource(R.drawable.ic_favorite_black_24dp);
                        holder.img_love.setTag(1);
                        ref = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves").child(fuser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("check_click", 1);
                        ref.updateChildren(hashMap);

                        Lover lover = new Lover(fuser.getUid(),
                                fuser.getDisplayName(),
                                mData.get(position).getPostKey());

                        addLover(lover, fuser.getUid(), mData.get(position).getPostKey());
                    }
                    if (resource == 1) {
                        holder.img_love.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        holder.img_love.setTag(0);
                        ref = FirebaseDatabase.getInstance().getReference("Lover_list").child(mData.get(position).getPostKey()).child("Loves").child(fuser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("check_click", 0);
                        ref.updateChildren(hashMap);

                        ref = FirebaseDatabase.getInstance().getReference("Lovers").child(fuser.getUid()).child(mData.get(position).getPostKey());
                        HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("postKey", "");
                        ref.updateChildren(hashMap2);
                    }
                }
            });

            myDialog2 = new Dialog(mContext,R.style.PauseDialogAnimation);
            myDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myDialog2.setContentView(R.layout.status_popup);
            myDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog2.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
            myDialog2.getWindow().getAttributes().gravity = Gravity.TOP;

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);

            commendRecyclerView2 = myDialog2.findViewById(R.id.commendCV);
            commendRecyclerView2.setHasFixedSize(true);
            commendRecyclerView2.setLayoutManager(linearLayoutManager);
            //linearLayoutManager.setReverseLayout(true);
            //linearLayoutManager.setStackFromEnd(true);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView img_status = myDialog2.findViewById(R.id.img_status);
                    ImageView img_del = myDialog2.findViewById(R.id.ico_del);
                    final ImageView img_commend = myDialog2.findViewById(R.id.img_commend);
                    TextView txt_date = myDialog2.findViewById(R.id.txt_date);
                    TextView txt_des = myDialog2.findViewById(R.id.txt_des);
                    final TextView txt_count_commend = myDialog2.findViewById(R.id.txt_count_commend);
                    TextView txt_user = myDialog2.findViewById(R.id.txt_user);
                    Button add_commend = myDialog2.findViewById(R.id.add_commend);
                    commend_post = myDialog2.findViewById(R.id.commend_post_id);

                    commend_post.setInputType(InputType.TYPE_NULL);

                    commend_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commend_post.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            commend_post.requestFocus();
                            InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.showSoftInput(commend_post, InputMethodManager.SHOW_FORCED);
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
                                                mData.get(position).getPostKey());

                                        addCommend(commend, mData.get(position).getPostKey());
                                        commend_post.setText("");

                                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputManager.hideSoftInputFromWindow(myDialog2.getCurrentFocus().getWindowToken(),
                                                InputMethodManager.HIDE_NOT_ALWAYS);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });

                            } else {
                                Toast.makeText(mContext, "Please write commend to post!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    if (mData.get(position).getUserId().equals(fuser.getUid())) {
                        img_del.setVisibility(View.VISIBLE);
                        img_del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog("Delete Post?", "Do you want to delete this post it will be permanent delete?", "cancelMethod1", "okMethod1", 1, mData.get(position).getPostKey());
                            }
                        });
                    } else {
                        img_del.setVisibility(View.INVISIBLE);
                    }

//                    txt_title.setText(mData.get(position).getTitle());
                    txt_date.setText("Post Date: " + mData.get(position).getPostDate() + " | View: " + mData.get(position).getUserView());
                    txt_user.setText(mData.get(position).getUserName());
                    txt_des.setText(mData.get(position).getDescription());
                    Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(img_status);

                    img_status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, AnotherActivity.class);
                            intent.putExtra("userid", mData.get(position).getUserId());
                            mContext.startActivity(intent);
                        }
                    });

                    reference = FirebaseDatabase.getInstance().getReference("Commends").child(mData.get(position).getPostKey());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int commend = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Commend commended = snapshot.getValue(Commend.class);
                                if (commended.getPostKeyId().equals(mData.get(position).getPostKey())) {
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
                            Glide.with(mContext).load(user.getImageURL()).into(img_commend);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    reference = FirebaseDatabase.getInstance().getReference("Posts").child(mData.get(position).getPostKey());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("userView", mData.get(position).getUserView() + 1);
                    reference.updateChildren(hashMap);

                    mCommend = new ArrayList<>();

                    databasereference = FirebaseDatabase.getInstance().getReference("Commends").child(mData.get(position).getPostKey());

                    databasereference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mCommend.clear();
                            for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                                Commend commend = postsnap.getValue(Commend.class);
                                mCommend.add(commend);

                            }

                            commendAdapter = new CommendAdapter(mContext, mCommend);
                            commendRecyclerView2.setAdapter(commendAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    myDialog2.show();
                }
            });
        }

    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_popup;
        TextView tvStatus;
        TextView tvUser;
        TextView tvDate;
        ImageView imgPost;
        ImageView imgPostProfile;
        ImageView img_love;
        TextView txt_count_love;

        TextView tvSta;
        ImageView imgStatusProfile;
        TextView tvUserStatus;
        TextView tvDateStatus;

        public MyViewHolder(View itemView, int status) {
            super(itemView);

            if (status == 0) {
                tvStatus = itemView.findViewById(R.id.post);
                tvUser = itemView.findViewById(R.id.username_status);
                tvDate = itemView.findViewById(R.id.username_date);
                imgPost = itemView.findViewById(R.id.row_post_img);
                imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
                img_love = itemView.findViewById(R.id.ico_love_post);
                txt_count_love = itemView.findViewById(R.id.post_count_love);
            } else {
                tvSta = itemView.findViewById(R.id.status);
                tvUserStatus = itemView.findViewById(R.id.username_status);
                tvDateStatus = itemView.findViewById(R.id.username_date);
                imgStatusProfile = itemView.findViewById(R.id.row_status_profile_img);
                img_love = itemView.findViewById(R.id.ico_love);
                txt_count_love = itemView.findViewById(R.id.post_count_love);
                img_popup = itemView.findViewById(R.id.img_popup);
            }
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

                if (status == 0) {
                    myDialog.dismiss();
                } else {
                    myDialog2.dismiss();
                }
                Toast.makeText(mContext, "Post delete successfully!", Toast.LENGTH_SHORT).show();
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
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(mContext);
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

    private void addCommend(Commend commend, String postkeyid) {

        if (!commend_post.getText().toString().isEmpty()) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Commends").child(postkeyid).push();

            myRef.setValue(commend).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(mContext, "Commend Successful!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void addLove(Love love, String userId, String postkeyid) {

        if (!userId.isEmpty()) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Lover_list").child(postkeyid).child("Loves").child(userId);

            myRef.setValue(love).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }

    }

    private void addLover(Lover lover, String userId, String postkeyid) {

        if (!userId.isEmpty()) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Lovers").child(userId).child(postkeyid);

            myRef.setValue(lover).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }

    }

    private void sendCommend(String userId, String userImg, String dateId, String commendId, String postKeyId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_commend_id", userId);
        hashMap.put("user_img", userImg);
        hashMap.put("date", dateId);
        hashMap.put("commend_text", commendId);
        hashMap.put("post_key", postKeyId);

        reference.child("Commends").child(postKeyId).push().setValue(hashMap);

        Toast.makeText(mContext, "Commend Successful!", Toast.LENGTH_SHORT).show();
    }
}
