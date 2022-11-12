package com.example.traverse;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class CityDetailsActivity extends AppCompatActivity {
    TextView txtCity, txtProvince, cityRate, cityMap;
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

        txtCity = findViewById(R.id.city);
        txtProvince = findViewById(R.id.province);
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
                                    Toast.makeText(CityDetailsActivity.this, "Error Loading details...", Toast.LENGTH_SHORT).show();
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
                                                                Toast.makeText(CityDetailsActivity.this, "Comment submitted !", Toast.LENGTH_SHORT).show();
                                                                ratingBar.setRating(0);
                                                                cityComment.setText("");
                                                            }else {
                                                                Toast.makeText(CityDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(CityDetailsActivity.this, "Please add a comment or a Rating !", Toast.LENGTH_SHORT).show();
                                        }
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
            }
        });
    }

    private void loadDetails() {
        db.document(getIntent().getStringExtra("documentPath")).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snap) {
                        City city = snap.toObject(City.class);
                        txtCity.setText(city.getName());
                        txtProvince.setText(city.getProvince());

                        String image = city.getImage();
                        Glide.with(CityDetailsActivity.this).load(image).into(cityImage);

                        RecyclerView visitPlaces = findViewById(R.id.placeList);
                        visitPlaces.setHasFixedSize(true);
                        visitPlaces.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        LocationReferenceAdapter visitPlacesAdapter = new LocationReferenceAdapter(CityDetailsActivity.this, city.getLocations(), R.layout.search_item, R.id.textView);
                        visitPlaces.setAdapter(visitPlacesAdapter);

                        RecyclerView cityActivityList = findViewById(R.id.cityActivityList);
                        cityActivityList.setHasFixedSize(true);
                        cityActivityList.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        ActivityReferenceAdapter cityActivityListAdapter = new ActivityReferenceAdapter(CityDetailsActivity.this, city.getActivities(), R.layout.search_item, R.id.textView);
                        cityActivityList.setAdapter(cityActivityListAdapter);

                        RecyclerView cityHotelList = findViewById(R.id.cityHotelList);
                        cityHotelList.setHasFixedSize(true);
                        cityHotelList.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        HotelReferenceAdapter cityHotelListAdapter = new HotelReferenceAdapter(CityDetailsActivity.this, city.getHotels(), R.layout.search_item, R.id.textView);
                        cityHotelList.setAdapter(cityHotelListAdapter);

//                        RecyclerView cityCommentList = findViewById(R.id.cityCommentList);
//                        cityCommentList.setHasFixedSize(true);
//                        cityCommentList.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));

                        progressDialog.dismiss();
                    }
                });


    }

    public class FetchData extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
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

                                }
                            }
                        });

                final String BASE_URL ="https://api.openweathermap.org/data/2.5/forecast?q="+ destination +"&appid="+"bf5e6047a46ad2469dced210d31f972e";
                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) { return null; }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line1;

                while ((line1 = reader.readLine()) != null) { buffer.append(line1 + "\n"); }
                if (buffer.length() == 0) { return null; }
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("Json", "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) { urlConnection.disconnect(); }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Json", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

}