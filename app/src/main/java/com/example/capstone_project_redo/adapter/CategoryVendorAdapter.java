package com.example.capstone_project_redo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.category.ProductData;
import com.example.capstone_project_redo.category.VendorData;
import com.example.capstone_project_redo.model.CategoryInsideModel;
import com.example.capstone_project_redo.model.CategoryModel;
import com.example.capstone_project_redo.model.VendorsModel;
import com.example.capstone_project_redo.nav.CategoryVendor;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;


public class CategoryVendorAdapter extends FirebaseRecyclerAdapter<VendorsModel, CategoryVendorAdapter.viewHolder> {

    private ArrayList<CategoryInsideModel> mVendor = new ArrayList<>();
    private OnVendorListener mOnVendorListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryVendorAdapter(@NonNull OnVendorListener mOnVendorListener, FirebaseRecyclerOptions<VendorsModel> options) {
        super(options);
        this.notifyDataSetChanged();
        this.mOnVendorListener = mOnVendorListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull VendorsModel model) {
        holder.username.setText(model.getUsername());
        holder.name.setText(model.getFirstName() + " " + model.getLastName());
        holder.mobile.setText(model.getMobileNumber());

        Glide.with(holder.imageProfile.getContext())
                .load(model.getImageProfile())
                .centerCrop()
                .into(holder.imageProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String image = model.getImageProfile();
                String username = model.getUsername();
                String name = model.getFirstName() + " " + model.getLastName();
                String mobile = model.getMobileNumber();
                String vendorID = model.getId();
                String vendorAddress = model.getMarketAddress();
                String vendorStall = model.getStallDescription();
                String overallScore = model.getOverallScore();
                String raters = model.getRaters();

                Intent intent =  new Intent(holder.itemView.getContext(), VendorData.class);

                intent.putExtra("image", image);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("mobile", mobile);
                intent.putExtra("id", vendorID);
                intent.putExtra("address", vendorAddress);
                intent.putExtra("stall", vendorStall);
                intent.putExtra("overallScore", overallScore);
                intent.putExtra("raters", raters);
                view.getContext().startActivity(intent);

            }
        });
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category_vendors, parent, false);
        return new viewHolder(view, mOnVendorListener);
    }


    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageProfile;
        TextView username, name, mobile;
        OnVendorListener onVendorListener;

        public viewHolder(@NonNull View itemView, OnVendorListener onVendorListener) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.tv_vendorUsername);
            name = (TextView) itemView.findViewById(R.id.tv_vendorName);
            mobile = (TextView) itemView.findViewById(R.id.tv_vendorNumber);
            imageProfile = (ImageView) itemView.findViewById(R.id.iv_vendorPic);

            this.onVendorListener = onVendorListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onVendorListener.onClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnVendorListener {
        void onClick(int position);
    }
}
