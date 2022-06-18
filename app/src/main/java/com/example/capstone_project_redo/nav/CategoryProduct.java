package com.example.capstone_project_redo.nav;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.adapter.CategoryVendorAdapter;
import com.example.capstone_project_redo.category.Misc;
import com.example.capstone_project_redo.category.CraftedGoods;
import com.example.capstone_project_redo.category.Food;
import com.example.capstone_project_redo.category.Basic;
import com.example.capstone_project_redo.databinding.ActivityCategoryBinding;
import com.example.capstone_project_redo.adapter.CategoryAdapter;
import com.example.capstone_project_redo.model.CategoryModel;
import com.example.capstone_project_redo.model.VendorsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class CategoryProduct extends DrawerBaseActivity implements CategoryAdapter.OnCategoryListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;
    ProgressDialog loadingProgress;
    RecyclerView categoryList;
    CategoryAdapter categoryAdapter;
    String currentUser;

    Button vendors;

    ActivityCategoryBinding activityCategoryBinding;

    private ArrayList<CategoryModel> mCategory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCategoryBinding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(activityCategoryBinding.getRoot());
        allocateActivityTitle("Categories");

        uAuth = FirebaseAuth.getInstance();
        currentUser = uAuth.getCurrentUser().getUid();

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading, Please Wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        vendors = findViewById(R.id.btn_catVendor);
        vendors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryProduct.this, CategoryVendor.class));
            }
        });

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        categoryAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
    }

    private void loadData() {
        categoryList = findViewById(R.id.lv_categoryProduct);
        categoryList.setHasFixedSize(true);
        categoryList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CategoryModel> options =
                new FirebaseRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("categoryImages").child("mainCategory").orderByChild("name"), CategoryModel.class)
                        .build();


        categoryAdapter = new CategoryAdapter(this, options);
        categoryList.setAdapter(categoryAdapter);

        databaseReference = database.getReference("products");
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
        getMenuInflater().inflate(R.menu.search_only, menu);
        MenuItem item = menu.findItem(R.id.searchOnly);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void txtSearch(String str) {

        FirebaseRecyclerOptions<CategoryModel> options =
                new FirebaseRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("categoryImages").child("mainCategory").orderByChild("name")
                                .startAt(str).endAt(str+"~"), CategoryModel.class)
                        .build();

        categoryAdapter = new CategoryAdapter(this, options);
        categoryAdapter.startListening();
        categoryList.setAdapter(categoryAdapter);
    }
    @Override
    public void onCategoryClick(int position) {
        Log.d(TAG, "onCategoryClick: clicked");
        switch (position) {
            case 0:
                String basic = "BasicNecessities";
                doOnce(basic);
                Intent basicIntent = new Intent(this, Basic.class);
                startActivity(basicIntent);
                finish();
                break;
            case 1:
                String crafts = "CraftedGoods";
                doOnce(crafts);
                Intent craftIntent = new Intent(this, CraftedGoods.class);
                startActivity(craftIntent);
                finish();
                break;
            case 2:
                String food = "Food";
                doOnce(food);
                Intent foodIntent = new Intent(this, Food.class);
                startActivity(foodIntent);
                finish();
                break;
            case 3:
                String misc = "Miscellaneous";
                doOnce(misc);
                Intent miscIntent = new Intent(this, Misc.class);
                startActivity(miscIntent);
                finish();
                break;
        }
    }

    public void doOnce(String category) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //startSomethingOnce();
        switch (day) {
            case 1: {
                String dayTxt = "sunday";
                totalToday(dayTxt, category);
                break;
            }
            case 2: {
                String dayTxt = "monday";
                totalToday(dayTxt, category);
                break;
            }
            case 3: {
                String dayTxt = "tuesday";
                totalToday(dayTxt, category);
                break;
            }
            case 4: {
                String dayTxt = "wednesday";
                totalToday(dayTxt, category);
                break;
            }
            case 5: {
                String dayTxt = "thursday";
                totalToday(dayTxt, category);
                break;
            }
            case 6: {
                String dayTxt = "friday";
                totalToday(dayTxt, category);
                break;
            }
            case 7: {
                String dayTxt = "saturday";
                totalToday(dayTxt, category);
                break;
            }
        }
    }

    public void totalToday(String dayTxt, String category) {
        DatabaseReference dataRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
        dataRef.child("statistics").child("categories").child(dayTxt).child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("total")) {
                    dataRef.child("statistics").child("categories").child(dayTxt).child(category).child("total").setValue(1);
                }
                else {
                    long total = (long) Objects.requireNonNull(snapshot.child("total").getValue());
                    dataRef.child("statistics").child("categories").child(dayTxt).child(category).child("total").setValue(total + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    startActivity(new Intent(CategoryProduct.this, Dashboard.class));
                    finish();
                }
                else if (snapshot.child("consumer").hasChild(typeOfUser)) {
                    startActivity(new Intent(CategoryProduct.this, HomePage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}