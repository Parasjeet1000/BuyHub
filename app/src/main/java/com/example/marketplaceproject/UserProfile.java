package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private RecyclerView recyclerView;
    private TextView noData;
    private FloatingActionButton newListing;
    private userListingAdapter mAdapter;
    private SQLiteDatabase mDatabase;
    private ImageView refresh;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null){
        Intent intent = new Intent(UserProfile.this, Login.class);
        startActivity(intent);
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        auth = FirebaseAuth.getInstance();
        db = new DatabaseHelper(UserProfile.this);
        mDatabase=db.getWritableDatabase();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        newListing = findViewById(R.id.newlisting);

        recyclerView = findViewById(R.id.myListings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mAdapter.swapCursor(getAllItems(uid));
        mAdapter = new userListingAdapter(UserProfile.this, getAllItems(uid));
        recyclerView.setAdapter(mAdapter);

        if(user == null){
            Intent intent = new Intent(UserProfile.this, Login.class);
            startActivity(intent);
        }

        user_name = findViewById(R.id.userid);
        refresh=findViewById(R.id.refresh);
        logout = findViewById(R.id.logout);
        String id = db.getUserFirstNameByUid(uid);
        user_name.setText("Welcome "+ id);



        refresh.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mAdapter.swapCursor(getAllItems(uid));
    }
});
        newListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uid == user.getUid()){
                Intent intent = new Intent(UserProfile.this, CreateListing.class);
                startActivity(intent);}
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



        // BOTTOM NAV CODE
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

    private Cursor getAllItems(String queryString) {

        String selection;
        String[] selectionArgs;

        if (queryString.equalsIgnoreCase("null")) {
            selection = null;
            selectionArgs = null;
        } else {
            selection = ListingContract.ListingEntry.UID2 + " IS ?";
            selectionArgs = new String[]{queryString};
        }


        return mDatabase.query(
                    ListingContract.ListingEntry.TABLE_NAME2,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


    }
}