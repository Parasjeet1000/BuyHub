package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Messages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.msg);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.user) {
                    startActivity(new Intent(getApplicationContext(), UserProfile.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.msg) {
                    return true;
                } else if (itemId == R.id.dash) {
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

    }
}