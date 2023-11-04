package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfile extends AppCompatActivity {
    private TextView user_name;
    private Button logout;
    private DatabaseHelper db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView listing;
    private TextView noData;
    private FloatingActionButton newListing;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        auth = FirebaseAuth.getInstance();
        db = new DatabaseHelper(this);
        db.getReadableDatabase();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        newListing = findViewById(R.id.newlisting);

        if(user == null){
            Intent intent = new Intent(UserProfile.this, Login.class);
            startActivity(intent);
        }

        user_name = findViewById(R.id.userid);
        logout = findViewById(R.id.logout);
        String id = db.getUserFirstNameByUid(uid);
        user_name.setText("Welcome "+ id);


        newListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, CreateListing.class);
                startActivity(intent);
            }
        });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserProfile.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            });


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.user);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.dash) {
                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.user) {
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