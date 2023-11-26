package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {


    private Button button;
    private RecyclerView recyclerView;
    private SearchView search;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private listingAdapter mAdapter;
    private SQLiteDatabase mDatabase;
    private DatabaseHelper db;


    @Override
    public void onStart() {
        super.onStart();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.createDatabase();

        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){

        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        db = new DatabaseHelper(Dashboard.this);
        // db.createDatabase();
        mDatabase=db.getWritableDatabase();


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        recyclerView = findViewById(R.id.mainListings);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new listingAdapter(Dashboard.this, getAllItems("null"));
        recyclerView.setAdapter(mAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Dashboard.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        mAdapter.swapCursor(getAllItems("null"));



        //serach function
        search = findViewById(R.id.search);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.swapCursor(getAllItems(newText));
                return true;
            }
        });



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
                        startActivity(new Intent(Dashboard.this, Login.class));
                    }
                    startActivity(new Intent(Dashboard.this, UserProfile.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.dash) {
                    return true;
                } else if (itemId == R.id.msg) {
                    startActivity(new Intent(Dashboard.this, Messages.class));
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
            selection = ListingContract.ListingEntry.TITLE_COL + " LIKE ? OR " +
                    ListingContract.ListingEntry.CATEGORY_COL + " LIKE ?";
            selectionArgs = new String[]{"%" + queryString + "%", "%" + queryString + "%"};
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