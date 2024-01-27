package com.priyanshu.whatsappclone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.priyanshu.whatsappclone.Fragments.Adapter.UsersAdapter;
import com.priyanshu.whatsappclone.Models.Users;
import com.priyanshu.whatsappclone.R;
import com.priyanshu.whatsappclone.databinding.FragmentChatsBinding;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {


    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;

    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        UsersAdapter adapter = new UsersAdapter(list, getContext());
        binding.chatRecyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());

                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(users);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}