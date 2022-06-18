package com.example.capstone_project_redo.nav;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.adapter.CategoryAdapter;
import com.example.capstone_project_redo.adapter.CategoryVendorAdapter;
import com.example.capstone_project_redo.adapter.MyListAdapter;
import com.example.capstone_project_redo.category.Basic;
import com.example.capstone_project_redo.category.CraftedGoods;
import com.example.capstone_project_redo.category.Food;
import com.example.capstone_project_redo.category.Misc;
import com.example.capstone_project_redo.databinding.ActivityCategoryBinding;
import com.example.capstone_project_redo.databinding.ActivityCategoryVendorBinding;
import com.example.capstone_project_redo.model.CategoryModel;
import com.example.capstone_project_redo.model.MyListModel;
import com.example.capstone_project_redo.model.VendorsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class CategoryVendor extends DrawerBaseActivity implements CategoryVendorAdapter.OnVendorListener{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    ProgressDialog loadingProgress;
    RecyclerView vendorList;
    CategoryVendorAdapter vendorAdapter;

    String type = "Username";
    Button products;

    ActivityCategoryVendorBinding vendorBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vendorBinding = ActivityCategoryVendorBinding.inflate(getLayoutInflater());
        setContentView(vendorBinding.getRoot());
        allocateActivityTitle("Categories");

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading, Please Wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        products = findViewById(R.id.btn_catProduct);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryVendor.this, CategoryProduct.class));
            }
        });

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vendorAdapter.startListening();
        vendorAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vendorAdapter.stopListening();
    }

    private void loadData() {

        vendorList = findViewById(R.id.lv_categoryVendor);
        vendorList.setHasFixedSize(true);
        vendorList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<VendorsModel> options;
        options = new FirebaseRecyclerOptions.Builder<VendorsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child("vendor").orderByChild(type), VendorsModel.class)
                .build();

        vendorAdapter = new CategoryVendorAdapter(this, options);
        vendorList.setAdapter(vendorAdapter);

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
        getMenuInflater().inflate(R.menu.search_vendors, menu);
        MenuItem item = menu.findItem(R.id.vSearch);

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
            case R.id.vFName:
                item.setChecked(true);
                type = "FirstName";
                return true;
            case R.id.vLName:
                item.setChecked(true);
                type = "LastName";
                return true;
            case R.id.vUserName:
                item.setChecked(true);
                type = "Username";
                return true;
            case R.id.vNumber:
                item.setChecked(true);
                type = "MobileNumber";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void txtSearch(String str, String type) {

        FirebaseRecyclerOptions<VendorsModel> options =
                new FirebaseRecyclerOptions.Builder<VendorsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child("vendor")
                                .orderByChild(type).startAt(str).endAt(str+"~"), VendorsModel.class)
                        .build();
        vendorAdapter = new CategoryVendorAdapter(this, options);
        vendorAdapter.startListening();
        vendorList.setAdapter(vendorAdapter);

    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onBackPressed() {
        FirebaseAuth touAuth = FirebaseAuth.getInstance();
        String typeOfUser = Objects.requireNonNull(touAuth.getCurrentUser()).getUid();
        DatabaseReference dataRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
        dataRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("vendor").hasChild(typeOfUser)) {
                    startActivity(new Intent(CategoryVendor.this, Dashboard.class));
                    finish();
                }
                else if (snapshot.child("consumer").hasChild(typeOfUser)) {
                    startActivity(new Intent(CategoryVendor.this, HomePage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}