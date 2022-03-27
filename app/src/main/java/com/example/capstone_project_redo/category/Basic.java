package com.example.capstone_project_redo.category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.adapter.CategoryInsideAdapter;
import com.example.capstone_project_redo.databinding.CategoryInsideBinding;
import com.example.capstone_project_redo.model.CategoryInsideModel;
import com.example.capstone_project_redo.nav.CategoryProduct;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Basic extends DrawerBaseActivity implements CategoryInsideAdapter.OnProductListener{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    ProgressDialog loadingProgress;

    RecyclerView insideList;
    CategoryInsideAdapter categoryInsideAdapter;
    CategoryInsideBinding insideBinding;

    private ArrayList<CategoryInsideModel> mCraft = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insideBinding = CategoryInsideBinding.inflate(getLayoutInflater());
        setContentView(insideBinding.getRoot());
        allocateActivityTitle("Basic Necessities Section");

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading, Please Wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        categoryInsideAdapter.startListening();
        categoryInsideAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        categoryInsideAdapter.stopListening();
    }

    private void loadData() {
        insideList = findViewById(R.id.lv_insideCategory);
        insideList.setHasFixedSize(true);
        insideList.setLayoutManager(new GridLayoutManager(this, 2));

        FirebaseRecyclerOptions<CategoryInsideModel> options;
        options = new FirebaseRecyclerOptions.Builder<CategoryInsideModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").child("Basic Necessities"), CategoryInsideModel.class)
                .build();

        categoryInsideAdapter = new CategoryInsideAdapter(this, options);
        insideList.setAdapter(categoryInsideAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (loadingProgress.isShowing()){
                    loadingProgress.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (loadingProgress.isShowing()){
                    loadingProgress.dismiss();
                }
            }
        });
    }

    @Override
    public void onCategoryClick(int position) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Basic.this, CategoryProduct.class));
        finish();
    }

}