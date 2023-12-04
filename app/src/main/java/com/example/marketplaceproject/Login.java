package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private DatabaseHelper db;
    private boolean toggleVisible = false;
    private FirebaseAuth auth;
    private ProgressBar progressBar;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(Login.this, Dashboard.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);
        db.getWritableDatabase();

        auth = FirebaseAuth.getInstance();

        TextView createAcc = findViewById(R.id.textview_createAccount);
        Button loginBtn = findViewById(R.id.button_login);
        ImageView visible = findViewById(R.id.imageView_visible);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        email = findViewById(R.id.edittext_email);
        pass = findViewById(R.id.edittext_password);

        // Toggle password visibility
        visible.setOnClickListener(v -> {
            if (toggleVisible) {
                // Hide Password
                visible.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_visibility_off, null));
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleVisible = false;
            } else {
                // Show Password
                visible.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_visible, null));
                pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleVisible = true;
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email_addr = email.getText().toString();
                String password = pass.getText().toString();
                if(email_addr.isEmpty()){
                    Toast.makeText(Login.this, "Enter in an Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(Login.this, "Enter in an Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(checkCredentials(email_addr,password)) {

                    auth.signInWithEmailAndPassword(email_addr, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Authentication Success.",
                                                Toast.LENGTH_SHORT).show();
                                        //progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(Login.this, Dashboard.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        createAcc.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), AccountCreationActivity.class);
            startActivity(intent);
        });

    }

    public Boolean checkCredentials(String email, String pass) {

        if (db.userExists(email)) {
            Cursor user = db.getUser(email);
            user.moveToNext();
            String checkPass = user.getString(user.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS));

            if (pass.equals(checkPass)) {
                Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, "Password Incorrect.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email not registered.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}