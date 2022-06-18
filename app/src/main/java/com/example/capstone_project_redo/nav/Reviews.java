package com.example.capstone_project_redo.nav;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.adapter.DtiListAdapter;
import com.example.capstone_project_redo.adapter.ReviewsAdapter;
import com.example.capstone_project_redo.databinding.ActivityAboutBinding;
import com.example.capstone_project_redo.databinding.ActivityReviewsBinding;
import com.example.capstone_project_redo.model.ReviewsModel;
import com.example.capstone_project_redo.model.ReviewsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Reviews extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    ProgressDialog loadingProgress;
    FirebaseAuth uAuth;
    String currentUser;

    String type = "FirstName";

    RecyclerView reviewsList;
    ReviewsAdapter reviewsAdapter;

    ActivityReviewsBinding reviewsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviewsBinding = ActivityReviewsBinding.inflate(getLayoutInflater());
        setContentView(reviewsBinding.getRoot());
        allocateActivityTitle("User Reviews");

        uAuth = FirebaseAuth.getInstance();
        currentUser = uAuth.getCurrentUser().getUid();

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading, Please Wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        readNotif();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reviewsAdapter.startListening();
        reviewsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reviewsAdapter.stopListening();
    }

    private void loadData() {
        reviewsList = findViewById(R.id.rv_reviewsList);
        reviewsList.setHasFixedSize(true);
        reviewsList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ReviewsModel> options;
        options = new FirebaseRecyclerOptions.Builder<ReviewsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("vendorRatings").child(currentUser).child("reviews").orderByChild(type), ReviewsModel.class)
                .build();

        reviewsAdapter = new ReviewsAdapter(options);
        reviewsList.setAdapter(reviewsAdapter);

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
        getMenuInflater().inflate(R.menu.search_reviews, menu);
        MenuItem item = menu.findItem(R.id.srSearch);

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
            case R.id.srFName:
                item.setChecked(true);
                type = "FirstName";
                return true;
            case R.id.srLName:
                item.setChecked(true);
                type = "LastName";
                return true;
            case R.id.srRating:
                item.setChecked(true);
                type = "rating";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void txtSearch(String str, String type) {

        FirebaseRecyclerOptions<ReviewsModel> options;
        options = new FirebaseRecyclerOptions.Builder<ReviewsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("vendorRatings").child(currentUser).child("reviews")
                        .orderByChild(type).startAt(str).endAt(str+"~"), ReviewsModel.class)
                .build();

        reviewsAdapter = new ReviewsAdapter(options);
        reviewsAdapter.startListening();
        reviewsList.setAdapter(reviewsAdapter);

    }

    public void readNotif() {
        databaseReference.child("vendorRatings").child(currentUser).child("reviews").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String status = (String)snapshot.child("read").getValue();
                String id = (String)snapshot.child("id").getValue();
                if (status!=null){
                    if (status.equals("false")) {
                        databaseReference.child("vendorRatings").child(currentUser).child("reviews").child(id).child("read").setValue("true");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
                    startActivity(new Intent(Reviews.this, Dashboard.class));
                    finish();
                }
                else if (snapshot.child("consumer").hasChild(typeOfUser)) {
                    startActivity(new Intent(Reviews.this, HomePage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}