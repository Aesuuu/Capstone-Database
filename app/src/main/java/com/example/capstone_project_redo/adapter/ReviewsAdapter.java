package com.example.capstone_project_redo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.model.ReviewsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ReviewsAdapter extends FirebaseRecyclerAdapter<ReviewsModel, ReviewsAdapter.viewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ReviewsAdapter(@NonNull FirebaseRecyclerOptions<ReviewsModel> options) {
        super(options);
        this.notifyDataSetChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull ReviewsModel model) {
        holder.name.setText(model.getFirstName() + " " + model.getLastName());
        holder.total.setText(model.getRating());
        holder.desc.setText(model.getComment());

        Glide.with(holder.profile.getContext())
                .load(model.getImageProfile())
                .centerCrop()
                .into(holder.profile);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.desc.getVisibility() == View.GONE) {
                    holder.desc.setVisibility(View.VISIBLE);
                }
                else {
                    holder.desc.setVisibility(View.GONE);
                }
            }
        });
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_reviews, parent, false);
        return new viewHolder(view);
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView name, total, desc;
        ConstraintLayout layout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout_rVisible);

            profile = itemView.findViewById(R.id.iv_rPicture);
            name = itemView.findViewById(R.id.tv_rName);
            total = itemView.findViewById(R.id.tv_rTotal);
            desc = itemView.findViewById(R.id.tv_rDesc);
            desc.setVisibility(View.GONE);
        }
    }
}
