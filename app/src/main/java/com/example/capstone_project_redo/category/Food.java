package com.example.capstone_project_redo.category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

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

public class Food extends DrawerBaseActivity implements CategoryInsideAdapter.OnProductListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    ProgressDialog loadingProgress;

    String order;

    RecyclerView insideList;
    CategoryInsideAdapter categoryInsideAdapter;
    CategoryInsideBinding insideBinding;

    private ArrayList<CategoryInsideModel> mCraft = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insideBinding = CategoryInsideBinding.inflate(getLayoutInflater());
        setContentView(insideBinding.getRoot());
        allocateActivityTitle("Food Section");

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
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").child("Food").orderByChild("name"), CategoryInsideModel.class)
                .build();

        categoryInsideAdapter = new CategoryInsideAdapter(this, options);
        insideList.setAdapter(categoryInsideAdapter);

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
    public void onCategoryClick(int position, View itemView) {

    }

    MenuItem filterMeat;
    MenuItem filterProcessed;
    MenuItem filterSeafood;
    MenuItem filterFruits;
    MenuItem filterVegetables;

    MenuItem itemChicken;
    MenuItem itemPork;
    MenuItem itemBeef;
    MenuItem itemFrozen;
    MenuItem itemCanned;
    MenuItem itemFish;
    MenuItem itemShellfish;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.filter_food, menu);

        MenuItem item = menu.findItem(R.id.searchFood);
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

        FirebaseRecyclerOptions<CategoryInsideModel> options;
        options = new FirebaseRecyclerOptions.Builder<CategoryInsideModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").child("Food")
                        .orderByChild("name").startAt(str).endAt(str+"~"), CategoryInsideModel.class)
                .build();
        categoryInsideAdapter = new CategoryInsideAdapter(this, options);
        categoryInsideAdapter.startListening();
        insideList.setAdapter(categoryInsideAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String meat = "Meat";
        String processed = "Processed Food";
        String seafood = "Seafood";
        switch (item.getItemId()) {
            case R.id.filterShowAllFood:
                order = "name";
                filterShowAll();
                return true;
            case R.id.filterMeat:
                order = "categorySub";
                filterFoodCat(meat, order);
                return true;
                case R.id.itemChicken:
                    order = "categorySub2";
                    String chicken = "Chicken";
                    filterFoodCat(chicken, order);
                    return true;
                case R.id.itemPork:
                    order = "categorySub2";
                    String pork = "Pork";
                    filterFoodCat(pork, order);
                    return true;
                case R.id.itemBeef:
                    order = "categorySub2";
                    String beef = "Beef";
                    filterFoodCat(beef, order);
                    return true;

            case R.id.filterProcessed:
                order = "categorySub";
                filterFoodCat(processed, order);
                return true;
                case R.id.itemFrozen:
                    order = "categorySub2";
                    String frozen = "Frozen";
                    filterFoodCat(frozen, order);
                    return true;
                case R.id.itemCanned:
                    order = "categorySub2";
                    String canned = "Canned";
                    filterFoodCat(canned, order);
                    return true;

            case R.id.filterSeafood:
                order = "categorySub";
                filterFoodCat(seafood, order);
                return true;
                case R.id.itemFish:
                    order = "categorySub2";
                    String fish = "Fish";
                    filterFoodCat(fish, order);
                    return true;
                case R.id.itemShellfish:
                    order = "categorySub2";
                    String shellfish = "Shellfish";
                    filterFoodCat(shellfish, order);
                    return true;
            case R.id.filterFruits:
                order = "categorySub";
                String fruit = "Fruits";
                filterFoodCat(fruit, order);
                return true;
            case R.id.filterVegetables:
                order = "categorySub";
                String vegetables = "Vegetables";
                filterFoodCat(vegetables, order);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void filterShowAll() {
        FirebaseRecyclerOptions<CategoryInsideModel> options;
        options = new FirebaseRecyclerOptions.Builder<CategoryInsideModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").child("Food").orderByChild("name"), CategoryInsideModel.class)
                .build();

        categoryInsideAdapter = new CategoryInsideAdapter(this, options);
        categoryInsideAdapter.startListening();
        insideList.setAdapter(categoryInsideAdapter);

    }

    private void filterFoodCat(String str, String orderBy) {

        FirebaseRecyclerOptions<CategoryInsideModel> options;
        options = new FirebaseRecyclerOptions.Builder<CategoryInsideModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").child("Food")
                        .orderByChild(orderBy).equalTo(str), CategoryInsideModel.class)
                .build();

        categoryInsideAdapter = new CategoryInsideAdapter(this, options);
        categoryInsideAdapter.startListening();
        insideList.setAdapter(categoryInsideAdapter);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Food.this, CategoryProduct.class));
        finish();
    }
}