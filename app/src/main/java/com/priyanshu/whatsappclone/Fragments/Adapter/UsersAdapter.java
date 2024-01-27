package com.priyanshu.whatsappclone.Fragments.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.priyanshu.whatsappclone.ChatDetailActivity;
import com.priyanshu.whatsappclone.Models.Users;
import com.priyanshu.whatsappclone.ProfilePhotoActivity;
import com.priyanshu.whatsappclone.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    ArrayList<Users> list;
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.shample_show_user,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = list.get(position);
        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.profile_user_svgrepo_com).into(holder.profileImage);
        holder.userNameList.setText(users.getUserName());


        // this code is for showing last message in mainActivity
        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(FirebaseAuth.getInstance().getUid() + users.getUserId()) // first is sender id get from firebaseAuth and second is receiver id geting from users model
                            .orderByChild("timestamp") // this is query for database convert into descending order. Last message is show on top. this works on integer because timestamp is integer value given
                                .limitToLast(1) // this is limit for last message
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                               if (snapshot.hasChildren()){
                                                   for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                                       holder.lastMessage.setText(snapshot1.child("message").getValue().toString());

                                                   }
                                               }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("profilePic", users.getProfilepic());
                intent.putExtra("userName", users.getUserName());

                context.startActivity(intent);
            }

        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfilePhotoActivity.class);
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("profilePic", users.getProfilepic());
                context.startActivity(intent);
            }
        }) ;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameList, lastMessage;
        ImageView profileImage;
         public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            userNameList = itemView.findViewById(R.id.userNameList);
            lastMessage  = itemView.findViewById(R.id.lastMessage);


        }
    }
}
