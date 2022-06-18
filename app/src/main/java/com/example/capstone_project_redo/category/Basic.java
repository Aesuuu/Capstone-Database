package com.example.capstone_project_redo.category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.adapter.CategoryInsideAdapter;
import com.example.capstone_project_redo.adapter.CategoryVendorAdapter;
import com.example.capstone_project_redo.databinding.CategoryInsideBinding;
import com.example.capstone_project_redo.model.CategoryInsideModel;
import com.example.capstone_project_redo.model.VendorsModel;
import com.example.capstone_project_redo.nav.CategoryProduct;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Basic extends DrawerBaseActivity implements CategoryInsideAdapter.OnProductListener{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    ProgressDialog loadingProgress;

    String type = "name";
    RecyclerView insideList;
    CategoryInsideAdapter categoryInsideAdapter;
    CategoryInsideBinding insideBinding;

    private ArrayList<CategoryInsideModel> mFood = new ArrayList<>();


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
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").child("Basic Necessities").orderByChild(type), CategoryInsideModel.class)
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_items, menu);
        MenuItem item = menu.findItem(R.id.pSearch);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query, type);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query, type);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.pName:
                item.setChecked(true);
                type = "name";
                return true;
            case R.id.pSeller:
                item.setChecked(true);
                type = "seller";
                return true;
            case R.id.pPrice:
                item.setChecked(true);
                type = "price";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void txtSearch(String str, String type) {

        FirebaseRecyclerOptions<CategoryInsideModel> options;
        options = new FirebaseRecyclerOptions.Builder<CategoryInsideModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").child("Basic Necessities")
                        .orderByChild(type).startAt(str).endAt(str+"~"), CategoryInsideModel.class)
                .build();

        categoryInsideAdapter = new CategoryInsideAdapter(this, options);
        categoryInsideAdapter.startListening();
        insideList.setAdapter(categoryInsideAdapter);

    }

    @Override
    public void onCategoryClick(int position, View itemView) {
        Log.d(TAG, "onCategoryClick: clicked");

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Basic.this, CategoryProduct.class));
        finish();
    }

}