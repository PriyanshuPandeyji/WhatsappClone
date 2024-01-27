package com.priyanshu.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.priyanshu.whatsappclone.databinding.ActivityProfilePhotoBinding;
import com.squareup.picasso.Picasso;

public class ProfilePhotoActivity extends AppCompatActivity{

    ActivityProfilePhotoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String profilePic = getIntent().getStringExtra("profilePic");
        Picasso.get().load(profilePic)
                        .placeholder(R.drawable.profile_user_svgrepo_com)
                            .into(binding.profileImage);

    }
}