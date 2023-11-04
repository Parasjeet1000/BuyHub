package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {


    private Button button;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //button = findViewById(R.id.button2);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();



        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.dash);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.user) {
                    FirebaseUser currentUser = auth.getCurrentUser();
                    if(currentUser == null){
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                    startActivity(new Intent(getApplicationContext(), UserProfile.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.dash) {
                    return true;
                } else if (itemId == R.id.msg) {
                    startActivity(new Intent(getApplicationContext(), Messages.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

    }
}