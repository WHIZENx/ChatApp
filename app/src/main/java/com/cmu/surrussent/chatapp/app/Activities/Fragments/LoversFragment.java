package com.cmu.surrussent.chatapp.app.Activities.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Chat.Adapter.PostAdapter;
import com.cmu.surrussent.chatapp.app.Chat.Model.Love;
import com.cmu.surrussent.chatapp.app.Chat.Model.Post;
import com.cmu.surrussent.chatapp.app.Chat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoversFragment extends Fragment {

    RecyclerView postRecyclerView ;
    private PostAdapter postAdapter ;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference ;
    private List<Post> postList;
    private List<Love> lovelist;

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lovers, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        postRecyclerView  = view.findViewById(R.id.loverRV);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        lover_load();

        return view;
    }

    private void lover_load() {

        lovelist = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Lovers").child(currentUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lovelist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Love love = snapshot.getValue(Love.class);
                    lovelist.add(love);
                }

                postList = new ArrayList<>();
                databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            for (Love love : lovelist) {
                                if (post.getPostKey().equals(love.getPostKey())) {
                                    postList.add(post);
                                }
                            }
                            postAdapter = new PostAdapter(getContext(), postList);
                            postRecyclerView.setAdapter(postAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
