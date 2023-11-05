package com.example.marketplaceproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CreateListing extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String videoPath ="";
    private String adcondition="";
    private EditText title, description, category, postalcode, price;
    private RadioGroup condition;
    private RadioButton brandnew, likenew, good, fair;
    private ImageView picture, video;
    private VideoView videoView;
    private Button post;
    private DatabaseHelper db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);


        LocalDateTime currentDate  = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // or "dd-MM-yyyy"
        String formattedDate = currentDate.format(formatter);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();

        db= new DatabaseHelper(CreateListing.this);

        picture = findViewById(R.id.imageselect);
        video=findViewById(R.id.videoselect);
        title = findViewById(R.id.Listingtitle);
        description = findViewById(R.id.Listingdescription);
        category = findViewById(R.id.Listingcategory);
        postalcode=findViewById(R.id.Listingpostal);
        price = findViewById(R.id.Listingprice);
        condition = findViewById(R.id.condition);
        post= findViewById(R.id.post);



        videoView=findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);



        ActivityResultLauncher fileChooser =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        new ActivityResultCallback<Uri>() {
                            @Override
                            public void onActivityResult(Uri result) {
                                videoView.setVideoURI(result);
                                videoView.start();
                                videoPath=result.toString();
                            }
                        });

        condition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.brandnew) {adcondition="New";
                    Toast.makeText(CreateListing.this, formattedDate, Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.likenew) {adcondition="Like New";// Option 2 is selected
                } else if (checkedId == R.id.good) {adcondition="Good";// Option 3 is selected
                } else if (checkedId == R.id.fair) {adcondition="Fair";// Option 3 is selected
                }
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adtitle = title.getText().toString();
                int adprice = Integer.parseInt((price.getText().toString()));
                String addescription = description.getText().toString();
                String adcategory = category.getText().toString();
                String adpostalcode = postalcode.getText().toString();

                if(adtitle.isEmpty() || addescription.isEmpty() || adcategory.isEmpty() || adprice<0 || adpostalcode.isEmpty() || adcondition.isEmpty()){
                    Toast.makeText(CreateListing.this, "incorrect information", Toast.LENGTH_SHORT).show();
                    return;
                }
                BitmapDrawable bitmapDrawable = (BitmapDrawable) picture.getDrawable();
                Bitmap imageBitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageByteArray = stream.toByteArray();

                Boolean checkinsertdata = db.addNewListing(adtitle,adprice,uid,adcategory,addescription,adcondition,adpostalcode,formattedDate,imageByteArray, videoPath);
                if(checkinsertdata) {
                    Toast.makeText(CreateListing.this, "Note has been saved :)", Toast.LENGTH_SHORT).show();
                    title.setText("");
                    price.setText("");
                    description.setText("");
                    postalcode.setText("");
                    category.setText("");

                    if(uid == user.getUid()){
                    Intent intent = new Intent(CreateListing.this, Dashboard.class);
                    startActivity(intent);}
                }
                else{
                    Toast.makeText(CreateListing.this, "Note failed to save", Toast.LENGTH_SHORT).show();
                }





            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                fileChooser.launch("video/*");
                //Uri videoUri = Uri.parse(videoPath);

            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //int PICK_IMAGE_REQUEST =1;
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            // Handle the image selected from the gallery
            Uri selectedImageUri = data.getData();
            picture.setImageURI(selectedImageUri);
        }

    }
}