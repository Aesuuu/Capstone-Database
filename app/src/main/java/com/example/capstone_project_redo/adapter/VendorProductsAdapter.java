package com.example.capstone_project_redo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.category.ProductData;
import com.example.capstone_project_redo.model.CategoryInsideModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class VendorProductsAdapter extends FirebaseRecyclerAdapter<CategoryInsideModel, VendorProductsAdapter.foodViewHolder> {

    private ArrayList<CategoryInsideModel> mFood = new ArrayList<>();
    private OnProductListener mOnProductListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public VendorProductsAdapter(@NonNull OnProductListener mOnFoodListener, FirebaseRecyclerOptions<CategoryInsideModel> options) {
        super(options);
        this.notifyDataSetChanged();
        this.mOnProductListener = mOnFoodListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull foodViewHolder holder, int position, @NonNull CategoryInsideModel model) {
        holder.name.setText(model.getName());
        holder.category.setText(model.getCategory());
        holder.price.setText(model.getPrice()+" "+model.getPriceExtension());

        Glide.with(holder.imageUrl.getContext())
                .load(model.getImageUrl())
                .centerCrop()
                .into(holder.imageUrl);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productID = model.getProductId();
                String productCategory = model.getCategory();

                Intent intent =  new Intent(holder.itemView.getContext(), ProductData.class);
                intent.putExtra("id", productID);
                intent.putExtra("category", productCategory);
                view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public foodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_vendor_products, parent, false);
        return new foodViewHolder(view, mOnProductListener);
    }


    public class foodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageUrl;
        TextView name, category, price;
        OnProductListener onProductListener;

        public foodViewHolder(@NonNull View itemView, OnProductListener onProductListener) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.tv_vProductName);
            category = (TextView)itemView.findViewById(R.id.tv_vProductCategory);
            price = (TextView)itemView.findViewById(R.id.tv_vProductPrice);
            imageUrl = (ImageView)itemView.findViewById(R.id.iv_vProductImage);
            this.onProductListener = onProductListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onProductListener.onCategoryClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnProductListener {
        void onCategoryClick(int position);
    }
}
