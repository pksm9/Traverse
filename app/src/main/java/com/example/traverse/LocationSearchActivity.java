package com.example.traverse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

public class LocationSearchActivity extends AppCompatActivity {
    FirebaseFirestore db;

    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    EditText searchCity;

    private static final String TAG = "FirestoreSearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        recyclerView = findViewById(R.id.city_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(LocationSearchActivity.this));
        //recyclerView.setNestedScrollingEnabled(false);
        this.repopulateRecyclerView();

        searchCity = findViewById(R.id.searchCity);
    }

    private void repopulateRecyclerView() {
        db.collection("locations").get()
            .addOnSuccessListener(querySnapshot -> {
                LocationSnapshotAdapter adapter = new LocationSnapshotAdapter(LocationSearchActivity.this, querySnapshot.getDocuments(), R.layout.search_item, R.id.textView);
                recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
            });
    }

}