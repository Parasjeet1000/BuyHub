package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class AccountCreationActivity extends AppCompatActivity {

    private EditText fName, lName, email, pass, confirm_pass;
    private ImageView visible;
    private Button login;
    private DatabaseHelper db;
    private FirebaseAuth auth;
    private FirebaseUser user;



    private boolean toggleVisible = false;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(AccountCreationActivity.this, Dashboard.class);
            startActivity(intent);
        }
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        db = new DatabaseHelper(this);
        db.getWritableDatabase();

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        // Find views
        fName = findViewById(R.id.edittext_firstName);
        lName = findViewById(R.id.edittext_lastName);
        email = findViewById(R.id.edittext_email);
        pass = findViewById(R.id.edittext_password);
        confirm_pass = findViewById(R.id.edittext_confirmPass);
        login = findViewById(R.id.button_create);
        visible = findViewById(R.id.imageView_visible);


        // Toggle Visibility
        visible.setOnClickListener(v -> {
            if (toggleVisible) {
                // Hide Password
                visible.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_visibility_off, null));
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirm_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleVisible = false;
            } else {
                // Show Password
                visible.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_visible, null));
                pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirm_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleVisible = true;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_name = fName.getText().toString();
                String last_name = lName.getText().toString();
                String email_addr = email.getText().toString();
                String password = pass.getText().toString();
                String confirm_password = confirm_pass.getText().toString();

                auth.createUserWithEmailAndPassword(email_addr, password)
                        .addOnCompleteListener (new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountCreationActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    user = auth.getCurrentUser();
                                    String uid = user.getUid();
                                    registerUser(uid);
                                    Intent intent = new Intent(AccountCreationActivity.this, Login.class);
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(AccountCreationActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });


    }




    public void registerUser(String uid) {
        String first_name = fName.getText().toString();
        String last_name = lName.getText().toString();
        String email_addr = email.getText().toString();
        String password = pass.getText().toString();
        String confirm_password = confirm_pass.getText().toString();

        if (first_name.isEmpty() || last_name.isEmpty() || email_addr.isEmpty() ||
                password.isEmpty() || confirm_password.isEmpty()) {
            Toast.makeText(this, "Please fill out the highlighted fields.", Toast.LENGTH_SHORT).show();


            // Highlight empty fields
            if (first_name.isEmpty()) {
                fName.setBackgroundColor(getResources().getColor(R.color.error, null));
            } else {
                fName.setBackgroundColor(getResources().getColor(R.color.light_gray, null));
            } if (last_name.isEmpty()){
                lName.setBackgroundColor(getResources().getColor(R.color.error, null));
            } else {
                lName.setBackgroundColor(getResources().getColor(R.color.light_gray, null));
            } if (email_addr.isEmpty()){
                this.email.setBackgroundColor(getResources().getColor(R.color.error, null));
            } else {
                this.email.setBackgroundColor(getResources().getColor(R.color.light_gray, null));
            } if (password.isEmpty()){
                this.pass.setBackgroundColor(getResources().getColor(R.color.error, null));
            } else {
                this.pass.setBackgroundColor(getResources().getColor(R.color.light_gray, null));
            } if (confirm_password.isEmpty()){
                this.confirm_pass.setBackgroundColor(getResources().getColor(R.color.error, null));
            } else {
                this.confirm_pass.setBackgroundColor(getResources().getColor(R.color.light_gray, null));
            }
        } else if (db.userExists(email_addr)) {
            Toast.makeText(this, "Email already in use.", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirm_password)) {
            Toast.makeText(this, "Passwords are different", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_FIRST_NAME, first_name);
            values.put(DatabaseHelper.COLUMN_LAST_NAME, last_name);
            values.put(DatabaseHelper.COLUMN_EMAIL, email_addr);
            values.put(DatabaseHelper.COLUMN_PASS, password);
            values.put(DatabaseHelper.UID, uid);

            long rowID = db.insert(values);

            if (rowID == -1) {
                Toast.makeText(this, "Error, try again", Toast.LENGTH_SHORT).show();
            } else {









                finish();

            }
        }

    }

}