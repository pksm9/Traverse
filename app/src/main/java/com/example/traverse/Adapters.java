package com.example.traverse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

abstract class CustomAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context context;
    protected List<T> items;
    @IdRes protected int textViewId;
    @LayoutRes protected int layoutId;

    public CustomAdapter(Context context, List<T> items, @LayoutRes int layoutId) {
        this.context = context;
        this.items = items;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    };

    @Override
    abstract public void onBindViewHolder(@NonNull ViewHolder holder, int position);
}

class CitySnapshotAdapter extends CustomAdapter<DocumentSnapshot> {
    public CitySnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        City city = snap.toObject(City.class);

        TextView txtName = holder.itemView.findViewById(R.id.textView);
        txtName.setText(city.getName());

        TextView txtProvince = holder.itemView.findViewById(R.id.subTextView);
        txtProvince.setText(city.getProvince());

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
    public LocationSnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Location location = snap.toObject(Location.class);

        TextView txtName = holder.itemView.findViewById(R.id.textView);
        txtName.setText(location.getName());

        TextView txtProvince = holder.itemView.findViewById(R.id.subTextView);
        txtProvince.setText(location.getProvince());

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
    public ActivitySnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Activity activity = snap.toObject(Activity.class);

        TextView txtName = holder.itemView.findViewById(R.id.textView);
        txtName.setText(activity.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documentPath", snap.getReference().getPath());
                context.startActivity(intent);
            }
        });
    }
}

class HotelSnapshotAdapter extends CustomAdapter<DocumentSnapshot> {
    public HotelSnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Hotel hotel = snap.toObject(Hotel.class);

        TextView txtName = holder.itemView.findViewById(R.id.textView);
        txtName.setText(hotel.getName());

        TextView txtCity = holder.itemView.findViewById(R.id.subTextView);
        txtCity.setText(hotel.getCity());

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
    public LocationReferenceAdapter(Context context, List<DocumentReference> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentReference ref = items.get(position);
        ref.get()
            .addOnSuccessListener(snap -> {
                City city = snap.toObject(City.class);

                TextView txtName = holder.itemView.findViewById(R.id.textView);
                txtName.setText(city.getName());

                TextView txtProvince = holder.itemView.findViewById(R.id.subTextView);
                txtProvince.setText(city.getProvince());
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
    public ActivityReferenceAdapter(Context context, List<DocumentReference> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentReference ref = items.get(position);
        ref.get()
            .addOnSuccessListener(snap -> {
                Activity activity = snap.toObject(Activity.class);

                TextView txtName = holder.itemView.findViewById(R.id.textView);
                txtName.setText(activity.getName());
            });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(context, ActivityDetailsActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               intent.putExtra("documentPath", ref.getPath());
               context.startActivity(intent);
            }
        });
    }
}

class HotelReferenceAdapter extends CustomAdapter<DocumentReference> {
    public HotelReferenceAdapter(Context context, List<DocumentReference> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentReference ref = items.get(position);
        ref.get()
            .addOnSuccessListener(snap -> {
                Hotel hotel = snap.toObject(Hotel.class);

                TextView txtName = holder.itemView.findViewById(R.id.textView);
                txtName.setText(hotel.getName());

                TextView txtProvince = holder.itemView.findViewById(R.id.subTextView);
                txtProvince.setText(hotel.getCity());
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
    public ReviewSnapshotAdapter(Context context, List<DocumentSnapshot> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snap = items.get(position);
        Review review = snap.toObject(Review.class);

        TextView userName = holder.itemView.findViewById(R.id.userName);
        userName.setText(review.getUser());

        TextView txtComment = holder.itemView.findViewById(R.id.textView);
        txtComment.setText(review.getComment());

        TextView txtRate = holder.itemView.findViewById(R.id.rate);
        txtRate.setText(String.valueOf(review.getRating()));
    }
}

class ImageUrlAdapter extends CustomAdapter<String> {
    public ImageUrlAdapter(Context context, List<String> items, @LayoutRes int layoutId) {
        super(context, items, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = items.get(position);

        ImageView img = holder.itemView.findViewById(R.id.img);
        Glide.with(context).load(url).into(img);
    }
}

