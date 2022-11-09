package com.example.traverse;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Objects;

public class CityDetailsActivity extends AppCompatActivity {

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

        uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

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
                                    Toast.makeText(CityDetailsActivity.this, "Error Loading details...", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    try {
                                        Uri uri = Uri.parse("www.google.co.in.maps/" + destination);
                                        //<iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3958.6972463705174!2d80.54281252201248!3d7.160956878291667!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3ae371f91c8825a9%3A0x8180706aa66fa138!2sAmbuluwawa%20Temple!5e0!3m2!1sen!2slk!4v1667957239572!5m2!1sen!2slk" width="600" height="450" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
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

                db.collection("users").document(uid).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        user = documentSnapshot.getString("user");
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CityDetailsActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

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
                                        Toast.makeText(CityDetailsActivity.this, "Comment submitted !", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(CityDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(CityDetailsActivity.this, "Please add a comment or a Rating !", Toast.LENGTH_SHORT).show();
                }
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

                        Glide.with(CityDetailsActivity.this).load(image).into(cityImage);

                        progressDialog.dismiss();
                    }
                });
    }

}