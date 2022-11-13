package com.example.traverse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

public class CitySearchActivity extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;
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
        this.populateRecyclerView();

        searchCity = findViewById(R.id.searchCity);
    }

    private void populateRecyclerView() {
//        searchCity.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                return false;
//            }
//        });

        searchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String searchKey = searchCity.getText().toString();
                db.collection("cities").get()
                        .addOnSuccessListener(querySnapshot -> {
                            CitySnapshotAdapter adapter = new CitySnapshotAdapter(CitySearchActivity.this, querySnapshot.getDocuments(), R.layout.search_item, R.id.textView);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}