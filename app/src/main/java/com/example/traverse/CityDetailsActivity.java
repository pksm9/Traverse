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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CityDetailsActivity extends AppCompatActivity {
    TextView txtCity, txtProvince, cityRate, placeMap;
    EditText cityComment;
    ImageView cityImage;
    RatingBar ratingBar;
    ProgressDialog progressDialog;
    Button button;

    TextView placeAltitude, placeTemp, placeHumidity, currentWeather;
    TextView altitude, avgTemp, humidity, weather;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private String uid;
    private String user;
    private String destination;

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
        txtProvince = findViewById(R.id.subTextView);
        cityImage = findViewById(R.id.cityImage);
        placeMap = findViewById(R.id.cityMap);

        placeAltitude = findViewById(R.id.placeAltitude);
        placeTemp = findViewById(R.id.placeTemp);
        placeHumidity = findViewById(R.id.placeHumidity);
        currentWeather = findViewById(R.id.currentWeather);

        altitude = findViewById(R.id.altitude);
        avgTemp = findViewById(R.id.avgTemp);
        humidity = findViewById(R.id.humidity);
        weather = findViewById(R.id.weather);


        this.loadDetails();
        this.displayMap();
        this.getWeatherDetails();

        ratingBar = findViewById(R.id.cityRating);
        cityRate = findViewById(R.id.cityRateOnCount);
        cityComment = findViewById(R.id.addCityComment);
        button = findViewById(R.id.cityCommentBtn);

        uid = firebaseAuth.getCurrentUser().getUid();

        this.addFeedback();
    }

    private void getWeatherDetails() {

        db.document(getIntent().getStringExtra("documentPath")).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot locationSnap) {
                        Location location = locationSnap.toObject(Location.class);
                        String place = location.getName();
                       Log.d("place", place);
                        final String BASE_URL ="https://api.openweathermap.org/data/2.5/weather?q="+place+"&appid="+"bf5e6047a46ad2469dced210d31f972e"+"&units=metric";


                        if (place.isEmpty()) {
                            Toast.makeText(CityDetailsActivity.this, "Error Loading details...", Toast.LENGTH_SHORT).show();
                        }
                        else {
                        }
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Log.d("response", response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                                    JSONObject weatherObject = jsonArray.getJSONObject(0);
                                    String weather = weatherObject.getString("description");

                                    JSONObject main = jsonObject.getJSONObject("main");
                                    String temp = main.getString("temp");
                                    String humidity = main.getString("humidity");

                                    placeTemp.setText(temp+" °C, ");
                                    placeHumidity.setText(humidity+"%");
                                    currentWeather.setText(weather);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                int statusCode = error.networkResponse.statusCode;
                                //Log.e("code", String.valueOf(statusCode));
                                if (statusCode==404){
                                    avgTemp.setText("Weather data not available for this city");
                                    humidity.setText("");
                                    weather.setText("");

                                }
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                });
    }

    private void displayMap() {
        placeMap.setOnClickListener(new View.OnClickListener() {
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
                String time = String.valueOf(FieldValue.serverTimestamp());

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
                                            commentsMap.put("time", time);
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
                        placeAltitude.setText(city.getAltitude());

                        String image = city.getImage();
                        Glide.with(CityDetailsActivity.this).load(image).into(cityImage);

                        RecyclerView visitPlaces = findViewById(R.id.locationList);
                        visitPlaces.setHasFixedSize(true);
                        visitPlaces.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        LocationReferenceAdapter visitPlacesAdapter = new LocationReferenceAdapter(CityDetailsActivity.this, city.getLocations(), R.layout.list_item);
                        visitPlaces.setAdapter(visitPlacesAdapter);

                        RecyclerView cityActivityList = findViewById(R.id.cityActivityList);
                        cityActivityList.setHasFixedSize(true);
                        cityActivityList.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        ActivityReferenceAdapter cityActivityListAdapter = new ActivityReferenceAdapter(CityDetailsActivity.this, city.getActivities(), R.layout.list_item);
                        cityActivityList.setAdapter(cityActivityListAdapter);

                        RecyclerView cityHotelList = findViewById(R.id.cityHotelList);
                        cityHotelList.setHasFixedSize(true);
                        cityHotelList.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        HotelReferenceAdapter cityHotelListAdapter = new HotelReferenceAdapter(CityDetailsActivity.this, city.getHotels(), R.layout.list_item);
                        cityHotelList.setAdapter(cityHotelListAdapter);

                        RecyclerView imageList = findViewById(R.id.imageList);
                        imageList.setHasFixedSize(true);
                        imageList.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        ImageUrlAdapter imageListAdapter = new ImageUrlAdapter(CityDetailsActivity.this, city.getImages(), R.layout.image_item);
                        imageList.setAdapter(imageListAdapter);

                        progressDialog.dismiss();
                    }
                });

        populateReviews();
    }

    public void populateReviews() {
        db.document(getIntent().getStringExtra("documentPath")).collection("reviews").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snap) {
                        RecyclerView cityCommentList = findViewById(R.id.commentList);
                        cityCommentList.setHasFixedSize(true);
                        cityCommentList.setLayoutManager(new LinearLayoutManager(CityDetailsActivity.this));
                        ReviewSnapshotAdapter reviewSnapshotAdapter = new ReviewSnapshotAdapter(CityDetailsActivity.this, snap.getDocuments(), R.layout.each_comment);
                        cityCommentList.setAdapter(reviewSnapshotAdapter);
                    }
                });
    }

}