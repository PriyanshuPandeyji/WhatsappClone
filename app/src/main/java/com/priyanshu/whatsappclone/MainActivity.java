package com.priyanshu.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.priyanshu.whatsappclone.Fragments.Adapter.FragmentAdapter;
import com.priyanshu.whatsappclone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayoutOne.setupWithViewPager(binding.viewPager);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater  inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.logout) {
            auth.signOut();
            navigateToSignInActivity();
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);

        } else if (itemId == R.id.seting) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        }
        else if (itemId == R.id.groupChat) {
            Intent intent = new Intent(MainActivity.this, GroupChat.class);
            startActivity(intent);
        }
        return true;
    }

    private void navigateToSignInActivity() {
        Intent intent = new Intent(MainActivity.this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}