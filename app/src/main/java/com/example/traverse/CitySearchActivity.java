package com.example.traverse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CitySearchActivity extends AppCompatActivity {
    FirebaseFirestore db;

    RecyclerView recyclerView;
    ArrayList<Result> resultArrayList;
    ResultsAdapter resultsAdapter;
    ProgressDialog progressDialog;
    EditText searchCity;

    private static final String TAG = "FirestoreSearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        db = FirebaseFirestore.getInstance();

        /*ProgressDialog progressDialog = new ProgressDialog(this);
        //ProgressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();*/

        recyclerView = findViewById(R.id.city_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CitySearchActivity.this));
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
                LocationAdapter adapter = new LocationAdapter(querySnapshot.getDocuments());
                recyclerView.setAdapter(adapter);
                /*if (progressDialog.isShowing())
                    progressDialog.dismiss();*/
            });
    }

   public void EventChangeListener() {
        db.collection("locations")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        /*if (progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        Log.e("Error : ", error.getMessage());
                        return;
                    }
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            resultArrayList .add(dc.getDocument().toObject(Result.class));
                        }
                        resultsAdapter.notifyDataSetChanged();

                    }
                }
            });
    }
}