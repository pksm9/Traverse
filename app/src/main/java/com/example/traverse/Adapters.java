package com.example.traverse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

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
        DocumentSnapshot locationSnap = locationDocuments.get(position);
        Location location = locationSnap.toObject(Location.class);
        holder.txtCity.setText(location.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CityDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", locationSnap.getReference().getPath());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationDocuments.size();
    }
}

