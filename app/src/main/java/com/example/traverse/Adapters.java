package com.example.traverse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

class ViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    public ViewHolder(@NonNull View itemView, @IdRes int textViewId) {
        super(itemView);
        textView = itemView.findViewById(textViewId);
    }
}

abstract class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    protected Context context;
    @IdRes protected int textViewId;
    @LayoutRes protected int layoutId;
    protected List<DocumentSnapshot> firebaseDocuments;

    public CustomAdapter(Context context, List<DocumentSnapshot> firebaseDocuments, @LayoutRes int layoutId, @IdRes int textViewId) {
        this.context = context;
        this.firebaseDocuments = firebaseDocuments;
        this.layoutId = layoutId;
        this.textViewId = textViewId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,false);
        return new ViewHolder(view, textViewId);
    }

    @Override
    public int getItemCount() {
        return firebaseDocuments.size();
    }

    abstract public void onBindViewHolder(@NonNull ViewHolder holder, int position);
}

class LocationAdapter extends CustomAdapter {
    public LocationAdapter(Context context, List<DocumentSnapshot> firebaseDocuments, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, firebaseDocuments, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = firebaseDocuments.get(position);
        Location location = snap.toObject(Location.class);
        holder.textView.setText(location.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", snap.getReference().getPath());
                context.startActivity(intent);
            }
        });
    }
}

