package com.example.traverse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CityDetailsActivity extends AppCompatActivity {

    private static final String TAG = "Tag";
    TextView location, province;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        db = FirebaseFirestore.getInstance();

        location = findViewById(R.id.location);
        province = findViewById(R.id.province);

        DocumentReference documentReference = db.collection("locations").document("wgedi");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                location.setText(value.getString("district"));
                province.setText(value.getString("province"));
            }
        });



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