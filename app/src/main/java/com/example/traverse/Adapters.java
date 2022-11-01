package com.example.traverse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

class LocationAdapter  extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCity = itemView.findViewById(R.id.city_result);
        }
    }

    private List<DocumentSnapshot> locationDocuments;

    public LocationAdapter(Context context, List<DocumentSnapshot> locationDocumentSnapshots) {
        this.context = context;
        this.locationDocuments = locationDocumentSnapshots;
    }

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent,false);
        return new LocationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        Location location = locationDocuments.get(position).toObject(Location.class);
        holder.txtCity.setText(location.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CityDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle bundle = new Bundle();
                bundle.putSerializable("location", location);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationDocuments.size();
    }
}


class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Result> resultArrayList;

    public ResultsAdapter(Context context, ArrayList<Result> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Result result = resultArrayList.get(position);

        holder.city.setText(result.getCity());

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView city;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            city = itemView.findViewById(R.id.city_result);
        }
    }
}
