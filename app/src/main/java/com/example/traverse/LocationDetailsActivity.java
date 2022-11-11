package com.example.traverse;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LocationDetailsActivity extends AppCompatActivity {
    TextView city, province, cityRate, cityMap;
    EditText cityComment;
    ImageView cityImage;
    RatingBar ratingBar;
    ProgressDialog progressDialog;
    Button button;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private String uid;
    private String user;
    private String destination;

    private static void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading Data....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        city = findViewById(R.id.city);
        province = findViewById(R.id.province);
        cityImage = findViewById(R.id.cityImage);
        cityMap = findViewById(R.id.cityMap);

        this.loadDetails();
        this.displayMap();

        ratingBar = findViewById(R.id.cityRating);
        cityRate = findViewById(R.id.cityRateOnCount);
        cityComment = findViewById(R.id.addCityComment);
        button = findViewById(R.id.cityCommentBtn);

        uid = firebaseAuth.getCurrentUser().getUid();

        this.addFeedback();
    }

    private void displayMap() {
        cityMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.document(getIntent().getStringExtra("documentPath")).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot locationSnap) {
                                Location location = locationSnap.toObject(Location.class);
                                destination = location.getName();

                                if (destination.isEmpty()) {
                                    Toast.makeText(LocationDetailsActivity.this, "Error Loading details...", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    try {
                                        Uri uri = Uri.parse("geo:0,0?q=" + destination);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        intent.setPackage("com.google.android.apps.maps");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }catch (ActivityNotFoundException e) {
                                        // if maps not installed redirect to play store
                                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void addFeedback() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                cityRate.setText(String.valueOf(rating));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = cityComment.getText().toString();
                float rating = ratingBar.getRating();

                db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){

                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        user = documentSnapshot.getString("user");
                                        Log.d("Traverse", user);

                                        if ((!comment.isEmpty()) || (!(String.valueOf(rating)).isEmpty())) {
                                            HashMap<String, Object> commentsMap = new HashMap<>();
                                            commentsMap.put("user", user);
                                            commentsMap.put("comment", comment);
                                            commentsMap.put("rating", rating);
                                            commentsMap.put("time", FieldValue.serverTimestamp());
                                            db.document(getIntent().getStringExtra("documentPath")).collection("reviews").document(uid).set(commentsMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(LocationDetailsActivity.this, "Comment submitted !", Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                Toast.makeText(LocationDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(LocationDetailsActivity.this, "Please add a comment or a Rating !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LocationDetailsActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void loadDetails() {
        db.document(getIntent().getStringExtra("documentPath")).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot locationSnap) {
                        Location location = locationSnap.toObject(Location.class);
                        city.setText(location.getName());
                        province.setText(location.getProvince());
                        String image = location.getImage();

                        Glide.with(LocationDetailsActivity.this).load(image).into(cityImage);

                        progressDialog.dismiss();
                    }
                });

        RecyclerView visitPlaces = findViewById(R.id.placeList);
        visitPlaces.setHasFixedSize(true);
        visitPlaces.setLayoutManager(new LinearLayoutManager(LocationDetailsActivity.this));
        db.collection("locations").get()
            .addOnSuccessListener(querySnapshot -> {
                LocationAdapter adapter = new LocationAdapter(LocationDetailsActivity.this, querySnapshot.getDocuments(), R.layout.search_item, R.id.textView);
                visitPlaces.setAdapter(adapter);
            });

        RecyclerView cityActivityList = findViewById(R.id.cityActivityList);
        cityActivityList.setHasFixedSize(true);
        cityActivityList.setLayoutManager(new LinearLayoutManager(LocationDetailsActivity.this));
        db.collection("locations").get()
            .addOnSuccessListener(querySnapshot -> {
                LocationAdapter adapter = new LocationAdapter(LocationDetailsActivity.this, querySnapshot.getDocuments(), R.layout.search_item, R.id.textView);
                cityActivityList.setAdapter(adapter);
            });

        RecyclerView cityHotelList = findViewById(R.id.cityHotelList);
        cityHotelList.setHasFixedSize(true);
        cityHotelList.setLayoutManager(new LinearLayoutManager(LocationDetailsActivity.this));
        db.collection("locations").get()
                .addOnSuccessListener(querySnapshot -> {
                    LocationAdapter adapter = new LocationAdapter(LocationDetailsActivity.this, querySnapshot.getDocuments(), R.layout.search_item, R.id.textView);
                    cityHotelList.setAdapter(adapter);
                });

        RecyclerView cityCommentList = findViewById(R.id.cityCommentList);
        cityCommentList.setHasFixedSize(true);
        cityCommentList.setLayoutManager(new LinearLayoutManager(LocationDetailsActivity.this));
        db.document(getIntent().getStringExtra("documentPath")).collection("reviews").get()
                .addOnSuccessListener(querySnapshot -> {
                    LocationAdapter adapter = new LocationAdapter(LocationDetailsActivity.this, querySnapshot.getDocuments(), R.layout.each_comment, R.id.rate);
                    cityCommentList.setAdapter(adapter);
                });
    }

}