package com.example.capstone_project_redo.nav;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.capstone_project_redo.AddItemActivity;
import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.databinding.ActivityMyProductsBinding;
import com.example.capstone_project_redo.adapter.MyListAdapter;
import com.example.capstone_project_redo.model.MyListModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.Objects;

public class MyProductsActivity extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser = user.getUid();

    String type = "";
    boolean showAll = true;

    RecyclerView myList;
    MyListAdapter myListAdapter;
    ActivityMyProductsBinding activityMyProductsBinding;

    ProgressDialog loadingProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMyProductsBinding = ActivityMyProductsBinding.inflate(getLayoutInflater());
        setContentView(activityMyProductsBinding.getRoot());
        allocateActivityTitle("My Products");

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Please wait while we fetch your data");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        FloatingActionButton fab_addItem = findViewById(R.id.fab_addItem);
        fab_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProductsActivity.this, AddItemActivity.class));
            }
        });

        if (showAll) {
            loadData();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        myListAdapter.startListening();
        myListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myListAdapter.stopListening();
    }

    private void loadData() {

        myList = findViewById(R.id.lv_myProducts);
        myList.setHasFixedSize(true);
        myList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<MyListModel> options =
                new FirebaseRecyclerOptions.Builder<MyListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").child(currentUser).orderByChild("name"), MyListModel.class)
                        .build();

        myListAdapter = new MyListAdapter(options);
        myList.setAdapter(myListAdapter);

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

    private void loadPerCategory(String category) {
        FirebaseRecyclerOptions<MyListModel> options =
                new FirebaseRecyclerOptions.Builder<MyListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").child(category).child(currentUser).orderByChild("name"), MyListModel.class)
                        .build();

        myListAdapter = new MyListAdapter(options);
        myListAdapter.startListening();
        myList.setAdapter(myListAdapter);
    }

    private void filterLoadAll() {
        FirebaseRecyclerOptions<MyListModel> options =
                new FirebaseRecyclerOptions.Builder<MyListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").child(currentUser).orderByChild("name"), MyListModel.class)
                        .build();

        myListAdapter = new MyListAdapter(options);
        myListAdapter.startListening();
        myList.setAdapter(myListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_products_filter, menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (showAll) {
                    txtSearch(query);
                }
                else filterCategory(query, type);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (showAll) {
                    txtSearch(query);
                }
                else filterCategory(query, type);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filterShowAll:
                showAll = true;
                filterLoadAll();
                return true;
            case R.id.filterFood:
                showAll = false;
                type = "Food";
                String food = "Food";
                loadPerCategory(food);
                return true;

            case R.id.filterCrafts:
                showAll = false;
                type = "Crafted Goods";
                String crafts = "Crafted Goods";
                loadPerCategory(crafts);
                return true;

            case R.id.filterBasic:
                showAll = false;
                type = "Basic Necessities";
                String basic = "Basic Necessities";
                loadPerCategory(basic);
                return true;

            case R.id.filterMisc:
                showAll = false;
                type = "Miscellaneous";
                String misc = "Miscellaneous";
                loadPerCategory(misc);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void txtSearch(String str) {
        FirebaseRecyclerOptions<MyListModel> options =
                new FirebaseRecyclerOptions.Builder<MyListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").child(currentUser).orderByChild("name")
                                .startAt(str).endAt(str+"~"), MyListModel.class)
                        .build();
        myListAdapter = new MyListAdapter(options);
        myListAdapter.startListening();
        myList.setAdapter(myListAdapter);
    }

    private void filterCategory(String str, String category) {
        FirebaseRecyclerOptions<MyListModel> options =
                new FirebaseRecyclerOptions.Builder<MyListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").child(category).child(currentUser).orderByChild("name")
                                .startAt(str).endAt(str+"~"), MyListModel.class)
                        .build();
        myListAdapter = new MyListAdapter(options);
        myListAdapter.startListening();
        myList.setAdapter(myListAdapter);
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
                    startActivity(new Intent(MyProductsActivity.this, Dashboard.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}