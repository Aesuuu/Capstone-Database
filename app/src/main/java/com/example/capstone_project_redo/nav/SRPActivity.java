package com.example.capstone_project_redo.nav;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.adapter.DtiListAdapter;
import com.example.capstone_project_redo.databinding.ActivitySrpBinding;
import com.example.capstone_project_redo.model.DtiListModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SRPActivity extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    ProgressDialog loadingProgress;
    RecyclerView dtiList;
    DtiListAdapter dtiListAdapter;

    String type = "product";

    ActivitySrpBinding srpBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        srpBinding = ActivitySrpBinding.inflate(getLayoutInflater());
        setContentView(srpBinding.getRoot());
        allocateActivityTitle("DTI's list of SRPs");

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading, Please Wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dtiListAdapter.startListening();
        dtiListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dtiListAdapter.stopListening();
    }

    private void loadData() {

        dtiList = findViewById(R.id.rv_dtiList);
        dtiList.setHasFixedSize(true);
        dtiList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<DtiListModel> options;
        options = new FirebaseRecyclerOptions.Builder<DtiListModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("dtiList").orderByChild("category"), DtiListModel.class)
                .build();

        dtiListAdapter = new DtiListAdapter(options);
        dtiList.setAdapter(dtiListAdapter);

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
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search);

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
            case R.id.sCategory:
                item.setChecked(true);
                type = "category";
                return true;
            case R.id.sProduct:
                item.setChecked(true);
                type = "product";
                return true;
            case R.id.sBrand:
                item.setChecked(true);
                type = "brand";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void txtSearch(String str, String type) {

        FirebaseRecyclerOptions<DtiListModel> options =
                new FirebaseRecyclerOptions.Builder<DtiListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("dtiList")
                                .orderByChild(type).startAt(str).endAt(str+"~"), DtiListModel.class).build();
        dtiListAdapter = new DtiListAdapter(options);
        dtiListAdapter.startListening();
        dtiList.setAdapter(dtiListAdapter);

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
                    startActivity(new Intent(SRPActivity.this, Dashboard.class));
                    finish();
                }
                else if (snapshot.child("consumer").hasChild(typeOfUser)) {
                    startActivity(new Intent(SRPActivity.this, HomePage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}