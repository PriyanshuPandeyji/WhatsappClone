package com.priyanshu.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.priyanshu.whatsappclone.Fragments.Adapter.ChatAdapter;
import com.priyanshu.whatsappclone.Models.MessageModel;
import com.priyanshu.whatsappclone.databinding.ActivityChatDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.profile_user_svgrepo_com).into(binding.profileImage);

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(ChatDetailActivity.this, ProfilePhotoActivity.class);
                intent.putExtra("profilePic", profilePic);
                startActivity(intent);
            }
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        chatAdapter = new ChatAdapter(this, messageModels, recieveId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        binding.chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Check if the chat is at the bottom
                    isChatAtBottom = !recyclerView.canScrollVertically(1);
                }
            }
        });

        // Scroll to the bottom after initializing the adapter
        scrollToBottom();


        final String senderRoom = senderId + recieveId;
        final String receiverRoom = recieveId + senderId;


        database.getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.etMessage.getText().toString().isEmpty()){

                }
                else{
                    String message = binding.etMessage.getText().toString();
                    final MessageModel model = new MessageModel(senderId, message);
                    model.setTimestamp(new Date().getTime());
                    binding.etMessage.setText("");

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .push()
                            .setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .push()
                                            .setValue(model)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // After sending the message, update the adapter
                                                    // After sending the message, update both adapters
                                                    updateBothAdapters(senderRoom, receiverRoom);
                                                }
                                            });
                                }
                            });
                }
            }
        });

    }

    private void updateAdapter(String senderRoom) {
        database.getReference()
                .child("chats")
                .child(senderRoom)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<MessageModel> updatedMessages = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());
                            updatedMessages.add(model);
                        }

                        chatAdapter.updateMessages(updatedMessages);

                        // Scroll to the bottom after updating the adapter
                        scrollToBottom();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if needed
                    }
                });
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    // Call this method for both sender and receiver
    private boolean isChatAtBottom = true;

    private void updateBothAdapters(String senderRoom, String receiverRoom) {
        updateAdapter(senderRoom);
        updateAdapter(receiverRoom);

        // Add a delay to wait for the adapter to update before scrolling
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isChatAtBottom) {
                    scrollToBottom();
                }
            }
        }, 200); // You can adjust the delay as needed
    }


}