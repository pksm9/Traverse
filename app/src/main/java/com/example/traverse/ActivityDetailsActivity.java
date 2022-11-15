package com.example.traverse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

class ActivityDetailsActivity extends AppCompatActivity {

    TextView activityName, rate;
    EditText addComment;
    RatingBar ratingBar;
    ProgressDialog progressDialog;
    Button button;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private String uid;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatails);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading Data....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        activityName = findViewById(R.id.activity);

        this.loadDetails();

        ratingBar = findViewById(R.id.rating);
        rate = findViewById(R.id.rateOnCount);
        addComment = findViewById(R.id.addComment);
        button = findViewById(R.id.commentBtn);

        uid = firebaseAuth.getCurrentUser().getUid();

        this.addFeedback();
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
                                                                Toast.makeText(ActivityDetailsActivity.this, "Comment submitted !", Toast.LENGTH_SHORT).show();
                                                                ratingBar.setRating(0);
                                                                addComment.setText("");
                                                            }else {
                                                                Toast.makeText(ActivityDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(ActivityDetailsActivity.this, "Please add a comment or a Rating !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ActivityDetailsActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Activity activity = snap.toObject(Activity.class);
                        activityName.setText(activity.getName());

                        RecyclerView visitPlaces = findViewById(R.id.locationList);
                        visitPlaces.setHasFixedSize(true);
                        visitPlaces.setLayoutManager(new LinearLayoutManager(ActivityDetailsActivity.this));
                        LocationReferenceAdapter visitPlacesAdapter = new LocationReferenceAdapter(ActivityDetailsActivity.this, activity.getLocations(), R.layout.list_item, R.id.textView);
                        visitPlaces.setAdapter(visitPlacesAdapter);

                        RecyclerView cityCommentList = findViewById(R.id.commentList);
                        cityCommentList.setHasFixedSize(true);
                        cityCommentList.setLayoutManager(new LinearLayoutManager(ActivityDetailsActivity.this));
                        //ReviewSnapshotAdapter reviewSnapshotAdapter = new ReviewSnapshotAdapter(LocationDetailsActivity.this, city.getReviews(), R.layout.each_comment, R.id.textView);
                        //cityCommentList.setAdapter(reviewSnapshotAdapter);

                        progressDialog.dismiss();
                    }
                });


    }

}