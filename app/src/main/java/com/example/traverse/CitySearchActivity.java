package com.example.traverse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CitySearchActivity extends AppCompatActivity {
    FirebaseFirestore db;

    RecyclerView recyclerView;
    ArrayList<Result> resultArrayList;
    ProgressDialog progressDialog;
    EditText searchCity;

    private static final String TAG = "FirestoreSearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        recyclerView = findViewById(R.id.city_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CitySearchActivity.this));
        //recyclerView.setNestedScrollingEnabled(false);
        this.repopulateRecyclerView();

        searchCity = findViewById(R.id.searchCity);
        this.changeOnSearch();



    }

    private void changeOnSearch() {
        searchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG,"Search box changed to : " + s.toString());

            }
        });
    }

    private void repopulateRecyclerView() {
        db.collection("locations").get()
            .addOnSuccessListener(querySnapshot -> {
                LocationAdapter adapter = new LocationAdapter(CitySearchActivity.this, querySnapshot.getDocuments());
                recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
            });
    }

}