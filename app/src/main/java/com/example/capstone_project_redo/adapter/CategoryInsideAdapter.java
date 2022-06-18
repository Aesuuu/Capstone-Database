package com.example.capstone_project_redo.adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
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
import com.example.capstone_project_redo.model.CategoryInsideModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CategoryInsideAdapter extends FirebaseRecyclerAdapter<CategoryInsideModel, CategoryInsideAdapter.foodViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth = FirebaseAuth.getInstance();
    String currentUser = uAuth.getCurrentUser().getUid();

    private ArrayList<CategoryInsideModel> mFood = new ArrayList<>();
    private OnProductListener mOnProductListener;

    public CategoryInsideAdapter(@NonNull FirebaseRecyclerOptions<CategoryInsideModel> options, ArrayList<CategoryInsideModel> mFood, OnProductListener mOnProductListener) {
        super(options);
        this.mFood = mFood;
        this.mOnProductListener = mOnProductListener;
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryInsideAdapter(@NonNull OnProductListener mOnFoodListener, FirebaseRecyclerOptions<CategoryInsideModel> options) {
        super(options);
        this.notifyDataSetChanged();
        this.mOnProductListener = mOnFoodListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull foodViewHolder holder, int position, @NonNull CategoryInsideModel model) {
        holder.name.setText(model.getName());
        holder.seller.setText(model.getSeller());
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
                String userID = model.getId();

                database.getReference().child("users").child("vendor")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(userID)) {
                                    doOnce(model.getId(), model.getName(), model.getProductId());
                                    Intent intent =  new Intent(holder.itemView.getContext(), ProductData.class);
                                    intent.putExtra("id", productID);
                                    intent.putExtra("category", productCategory);
                                    view.getContext().startActivity(intent);
                                }
                                else {
                                    database.getReference().child("categories").child(productCategory).child(getRef(position).getKey()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                database.getReference().child("categories").child(model.getCategory()).child(getRef(position).getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(model.getId())) {

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    @NonNull
    @Override
    public foodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_inside_category, parent, false);
        return new foodViewHolder(view, mOnProductListener);
    }


    public class foodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageUrl;
        TextView name, seller, price;
        OnProductListener onProductListener;

        public foodViewHolder(@NonNull View itemView, OnProductListener onProductListener) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.nametext);
            seller = (TextView)itemView.findViewById(R.id.sellertext);
            price = (TextView)itemView.findViewById(R.id.pricetext);
            imageUrl = (ImageView)itemView.findViewById(R.id.img1);
            this.onProductListener = onProductListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onProductListener.onCategoryClick(getAbsoluteAdapterPosition(), itemView);
        }
    }

    public interface OnProductListener {

        void onCategoryClick(int absoluteAdapterPosition, View itemView);
    }


    public void doOnce(String vendorSelected, String name, String id) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //startSomethingOnce();
        switch (day) {
            case 1: {
                String dayTxt = "sunday";
                totalToday(dayTxt, vendorSelected, name, id);
                break;
            }
            case 2: {
                String dayTxt = "monday";
                totalToday(dayTxt, vendorSelected, name, id);
                break;
            }
            case 3: {
                String dayTxt = "tuesday";
                totalToday(dayTxt, vendorSelected, name, id);
                break;
            }
            case 4: {
                String dayTxt = "wednesday";
                totalToday(dayTxt, vendorSelected, name, id);
                break;
            }
            case 5: {
                String dayTxt = "thursday";
                totalToday(dayTxt, vendorSelected, name, id);
                break;
            }
            case 6: {
                String dayTxt = "friday";
                totalToday(dayTxt, vendorSelected, name, id);
                break;
            }
            case 7: {
                String dayTxt = "saturday";
                totalToday(dayTxt, vendorSelected, name, id);
                break;
            }
        }
    }

    public void totalToday(String dayTxt, String vendorSelected, String name, String id) {
        DatabaseReference dataRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
        dataRef.child("statistics").child("products").child(dayTxt).child(vendorSelected).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("total")) {
                    dataRef.child("statistics").child("products").child(dayTxt).child(vendorSelected).child(id).child("total").setValue(1);
                    dataRef.child("statistics").child("products").child(dayTxt).child(vendorSelected).child(id).child("name").setValue(name);
                    dataRef.child("statistics").child("products").child(dayTxt).child(vendorSelected).child(id).child("productId").setValue(id);
                }
                else {
                    long total = (long) Objects.requireNonNull(snapshot.child("total").getValue());
                    dataRef.child("statistics").child("products").child(dayTxt).child(vendorSelected).child(id).child("total").setValue(total + 1);
                    dataRef.child("statistics").child("products").child(dayTxt).child(vendorSelected).child(id).child("name").setValue(name);
                    dataRef.child("statistics").child("products").child(dayTxt).child(vendorSelected).child(id).child("productId").setValue(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
