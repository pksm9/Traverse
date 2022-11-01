package com.example.traverse;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class CityDetailsActivity extends AppCompatActivity {

    private static final String TAG = "Tag";
    TextView city, province;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        db = FirebaseFirestore.getInstance();

        city = findViewById(R.id.city);
        province = findViewById(R.id.province);

        Location location = (Location) getIntent().getExtras().getSerializable("location");
        Log.d("Traverse", location.getName());
//        city.setText(getIntent().get("city"));

        /*DocumentReference documentReference = db.collection("locations").document("wgedi");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                city.setText(value.getString("district"));
                province.setText(value.getString("province"));
            }
        });*/



//        db.collection("locations").document("wgedi")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            //Log.d(TAG, document.getId() + " => " + document.getData());
//
//                            location.setText(document.getString("district"));
//                            province.setText(document.getString("province"));
//
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                            //Log.w(TAG, "Error : " + task.getException().getMessage());
//                        }
//                    }
//                });


    }
}