package com.example.marketplaceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class OpenListing extends AppCompatActivity implements OnMapReadyCallback{

    private LatLng loc;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_listing);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Retrieve intent
        Intent data = getIntent();
        int listId = data.getIntExtra("id", -1);

        if (listId == -1) {
            Toast.makeText(this, "Product unavailable", Toast.LENGTH_SHORT).show();
        }

        DatabaseHelper db = new DatabaseHelper(this);
        db.getReadableDatabase();
        Cursor listData = db.getListData(listId);

        listData.moveToNext();
        String title = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.TITLE_COL));
        String price = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.PRICE_COL));
        String category = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.CATEGORY_COL));
        String condition = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.CONDITION_COL));
        String postal = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.POSTAL_COL));
        String date = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.DATE_COL));
        String description = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.DESCRIPTION_COL));
        byte[] image = listData.getBlob(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.IMAGE_COL));
        String video = listData.getString(listData.getColumnIndexOrThrow(ListingContract.ListingEntry.VIDEO_COL));

        // Setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_maps);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Get views
        TextView listTitle = findViewById(R.id.textview_title);
        TextView listPrice = findViewById(R.id.textview_price);
        TextView listCategory = findViewById(R.id.textview_category);
        TextView listCondition = findViewById(R.id.textview_condition);
        TextView listPostal = findViewById(R.id.textview_postal);
        TextView listDate = findViewById(R.id.textview_date);
        TextView listDescription = findViewById(R.id.textview_desc);
        ImageView listImage = findViewById(R.id.imageView);
        VideoView listVideo = findViewById(R.id.videoView);
        Button contact = findViewById(R.id.contact_seller);

        contact.setOnClickListener(v -> {
            if(user == null) {
                startActivity(new Intent(OpenListing.this, Login.class));
            }
            else{
                Toast.makeText(getApplicationContext(), "Contacted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Display bundle info in views
        listTitle.setText(title);
        listPrice.setText("$" + price);
        listCategory.setText("Category: " + category);
        listCondition.setText("Condition: " + condition);
        listPostal.setText("Postal: " + postal);
        listDate.setText(date);
        listDescription.setText(description);
        listImage.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeByteArray(image, 0, image.length)));
        if (video != null) {
            String temp = "https://drive.google.com/uc?export=download&id=1okFNZUo_4KjssRAA7QTyxdcZPT4XZV0O";
            MediaController mediaController = new MediaController(OpenListing.this);
            mediaController.setAnchorView(listCategory);
            listVideo.setVideoPath(video);
            // sets the media controller to the videoView
            listVideo.setMediaController(mediaController);
            listVideo.requestFocus();
            // starts the video
            listVideo.start();

        } else if(video == null) {
            listVideo.setVisibility(View.INVISIBLE);
        } else if (video.length() < 3) {
            listVideo.setVisibility(View.INVISIBLE);
        } else{
            listVideo.setVisibility(View.INVISIBLE);
        }

        // Get lat and long from postal
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            assert postal != null;
            List<Address> addresses = geocoder.getFromLocationName(postal, 1);
            assert addresses != null;
            Address address = addresses.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            loc = new LatLng(latitude, longitude);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        // Add the sellers general location
        googleMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("Seller Location"));

        // Move the camera to the general area
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

        googleMap.addCircle(new CircleOptions()
                .center(loc)
                .radius(2000) // Initial radius in meters
                .strokeWidth(2)
                .strokeColor(getResources().getColor(R.color.yellow))
                .fillColor(getResources().getColor(R.color.yellow)));

    }
}