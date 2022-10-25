package com.example.traverse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CitySearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Result> resultArrayList;
    ResultsAdapter resultsAdapter;
    ProgressDialog progressDialog;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        /*ProgressDialog progressDialog = new ProgressDialog(this);
        //ProgressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();*/

        recyclerView = findViewById(R.id.city_list);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        resultArrayList = new ArrayList<Result>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        resultsAdapter = new ResultsAdapter(CitySearchActivity.this,resultArrayList);

        recyclerView.setAdapter(resultsAdapter);

        EventChangeListener();
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
                           /* if (progressDialog.isShowing()){
                                progressDialog.dismiss();}*/
                        }
                    }
                });
    }
}