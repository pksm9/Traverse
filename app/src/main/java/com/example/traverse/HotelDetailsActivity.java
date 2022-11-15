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

public class HotelDetailsActivity extends AppCompatActivity {

    TextView hotelName, city, rate, map;
    EditText addComment;
    ImageView imageView;
    RatingBar ratingBar;
    ProgressDialog progressDialog;
    Button button;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private String uid;
    private String user;
    private String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading Data....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        hotelName = findViewById(R.id.hotel);
        city = findViewById(R.id.city);
        imageView = findViewById(R.id.image);
        map = findViewById(R.id.map);

        this.loadDetails();
        this.displayMap();

        ratingBar = findViewById(R.id.rating);
        rate = findViewById(R.id.rateOnCount);
        addComment = findViewById(R.id.addComment);
        button = findViewById(R.id.commentBtn);

        uid = firebaseAuth.getCurrentUser().getUid();

        this.addFeedback();
    }

    private void displayMap() {
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.document(getIntent().getStringExtra("documentPath")).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot locationSnap) {
                                Location location = locationSnap.toObject(Location.class);
                                destination = location.getName();

                                if (destination.isEmpty()) {
                                    Toast.makeText(HotelDetailsActivity.this, "Error Loading details...", Toast.LENGTH_SHORT).show();
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
                rate.setText(String.valueOf(rating));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = addComment.getText().toString();
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
                                                                Toast.makeText(HotelDetailsActivity.this, "Comment submitted !", Toast.LENGTH_SHORT).show();
                                                                ratingBar.setRating(0);
                                                                addComment.setText("");
                                                            }else {
                                                                Toast.makeText(HotelDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(HotelDetailsActivity.this, "Please add a comment or a Rating !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HotelDetailsActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void loadDetails() {
        db.document(getIntent().getStringExtra("documentPath")).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snap) {
                        Hotel hotel= snap.toObject(Hotel.class);
                        hotelName.setText(hotel.getName());
                        city.setText(hotel.getCity());

                        String image = hotel.getImage();
                        Glide.with(HotelDetailsActivity.this).load(image).into(imageView);

                        RecyclerView visitPlaces = findViewById(R.id.locationList);
                        visitPlaces.setHasFixedSize(true);
                        visitPlaces.setLayoutManager(new LinearLayoutManager(HotelDetailsActivity.this));
                        LocationReferenceAdapter visitPlacesAdapter = new LocationReferenceAdapter(HotelDetailsActivity.this, hotel.getLocations(), R.layout.list_item, R.id.textView);
                        visitPlaces.setAdapter(visitPlacesAdapter);

                        RecyclerView cityCommentList = findViewById(R.id.commentList);
                        cityCommentList.setHasFixedSize(true);
                        cityCommentList.setLayoutManager(new LinearLayoutManager(HotelDetailsActivity.this));
                        //ReviewSnapshotAdapter reviewSnapshotAdapter = new ReviewSnapshotAdapter(LocationDetailsActivity.this, city.getReviews(), R.layout.each_comment, R.id.textView);
                        //cityCommentList.setAdapter(reviewSnapshotAdapter);

                        progressDialog.dismiss();
                    }
                });


    }
}