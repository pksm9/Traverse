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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

class ViewHolder extends RecyclerView.ViewHolder {
    TextView textView,province;
    public ViewHolder(@NonNull View itemView, @IdRes int textViewId) {
        super(itemView);
        textView = itemView.findViewById(textViewId);
        province = itemView.findViewById(R.id.province);
    }
}

abstract class CustomAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context context;
    protected List<T> items;
    @IdRes protected int textViewId;
    @LayoutRes protected int layoutId;

    public CustomAdapter(Context context, List<T> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        this.context = context;
        this.items = items;
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
        return this.items.size();
    };

    @Override
    abstract public void onBindViewHolder(@NonNull ViewHolder holder, int position);
}

class CitySnapshotAdapter extends CustomAdapter<DocumentSnapshot> {
    public CitySnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        City city = snap.toObject(City.class);
        holder.textView.setText(city.getName());
        holder.province.setText(city.getProvince());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CityDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", snap.getReference().getPath());
                context.startActivity(intent);
            }
        });
    }
}

class LocationSnapshotAdapter extends CustomAdapter<DocumentSnapshot> {
    public LocationSnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Location location = snap.toObject(Location.class);
        holder.textView.setText(location.getName());
        holder.province.setText(location.getCity());

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

class ActivitySnapshotAdapter extends CustomAdapter<DocumentSnapshot> {
    public ActivitySnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Activity activity = snap.toObject(Activity.class);
        holder.textView.setText(activity.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityDeatailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", snap.getReference().getPath());
                context.startActivity(intent);
            }
        });
    }
}

class HotelSnapshotAdapter extends CustomAdapter<DocumentSnapshot> {
    public HotelSnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Hotel hotel = snap.toObject(Hotel.class);
        holder.textView.setText(hotel.getName());
        holder.province.setText(hotel.getCity());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HotelDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", snap.getReference().getPath());
                context.startActivity(intent);
            }
        });
    }
}

class LocationReferenceAdapter extends CustomAdapter<DocumentReference> {
    public LocationReferenceAdapter(Context context, List<DocumentReference> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentReference ref = items.get(position);
        ref.get()
            .addOnSuccessListener(snap -> {
                City city = snap.toObject(City.class);
                holder.textView.setText(city.getName());
                holder.province.setText(city.getProvince());
            });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", ref.getPath());
                context.startActivity(intent);
            }
        });
    }
}

class ActivityReferenceAdapter extends CustomAdapter<DocumentReference> {
    public ActivityReferenceAdapter(Context context, List<DocumentReference> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentReference ref = items.get(position);
        ref.get()
            .addOnSuccessListener(snap -> {
                Activity activity = snap.toObject(Activity.class);
                holder.textView.setText(activity.getName());
            });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ActivityDetailsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("documentPath", ref.getPath());
//                context.startActivity(intent);
            }
        });
    }
}

class HotelReferenceAdapter extends CustomAdapter<DocumentReference> {
    public HotelReferenceAdapter(Context context, List<DocumentReference> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentReference ref = items.get(position);
        ref.get()
            .addOnSuccessListener(snap -> {
                Hotel hotel = snap.toObject(Hotel.class);
                holder.textView.setText(hotel.getName());
            });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HotelDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", ref.getPath());
                context.startActivity(intent);
            }
        });
    }
}

class ReviewSnapshotAdapter extends CustomAdapter<DocumentSnapshot> {
    public ReviewSnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId, @IdRes int textViewId) {
        super(context, items, layoutId, textViewId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Review review = snap.toObject(Review.class);
        holder.textView.setText(review.getComment());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ActivityDetailsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("documentPath", ref.getPath());
//                context.startActivity(intent);
            }
        });
    }
}

