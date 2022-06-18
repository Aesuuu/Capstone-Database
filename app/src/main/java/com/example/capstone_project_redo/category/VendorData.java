package com.example.capstone_project_redo.category;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.CreateAccountPart1;
import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.MapsActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.ReportVendor;
import com.example.capstone_project_redo.adapter.ReviewsAdapter;
import com.example.capstone_project_redo.adapter.VendorProductsAdapter;
import com.example.capstone_project_redo.databinding.CategoryInsideVendorBinding;
import com.example.capstone_project_redo.model.CategoryInsideModel;
import com.example.capstone_project_redo.model.ReviewsModel;
import com.example.capstone_project_redo.nav.AboutActivity;
import com.example.capstone_project_redo.nav.CategoryVendor;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class VendorData extends DrawerBaseActivity implements VendorProductsAdapter.OnProductListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    DatabaseReference dbRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;

    ProgressDialog loadingProgress;

    ImageView image;
    TextView username, name, mobile, address, stallName, stall, overall, raters,userProducts;
    Button map, showMore, rateVendor, reportVendor, submit;
    RatingBar ratingBar;
    float userScore = 0, overallScore = 0;
    String userScoreStr, overallScoreStr, idTxt;

    RecyclerView insideList;
    VendorProductsAdapter vendorProductsAdapter;
    RecyclerView reviewsList;
    ReviewsAdapter reviewsAdapter;

    CategoryInsideVendorBinding vendorBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vendorBinding = CategoryInsideVendorBinding.inflate(getLayoutInflater());
        setContentView(vendorBinding.getRoot());
        allocateActivityTitle("Vendor Data");

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading, Please Wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        uAuth = FirebaseAuth.getInstance();
        String currentUser = uAuth.getCurrentUser().getUid();

        // INTENT FROM ADAPTER
        Intent extra = getIntent();
        String imageTxt = extra.getStringExtra("image");
        String usernameTxt = extra.getStringExtra("username");
        String nameTxt = extra.getStringExtra("name");
        String mobileTxt = extra.getStringExtra("mobile");
        idTxt = extra.getStringExtra("id");
        String addressTxt = extra.getStringExtra("address");
        String stallTxt = extra.getStringExtra("stall");
        String overallTxt = extra.getStringExtra("overallScore");
        String ratersTxt = extra.getStringExtra("raters");

        image = findViewById(R.id.iv_vendorPic);
        username = findViewById(R.id.tv_vendorUsername);
        name = findViewById(R.id.tv_vendorName);
        mobile = findViewById(R.id.tv_vendorNumber);
        address = findViewById(R.id.tv_vendorAddress); address.setVisibility(View.GONE);
        stallName = findViewById(R.id.tv_vendorStallN); stallName.setVisibility(View.GONE);
        stall = findViewById(R.id.tv_vendorStall); stall.setVisibility(View.GONE);
        map = findViewById(R.id.btn_vendorMap); map.setVisibility(View.GONE);
        overall = findViewById(R.id.tv_totalRating);
        raters = findViewById(R.id.tv_usersRated);
        userProducts = findViewById(R.id.tv_vUserProducts);

        // SET STRING TO TEXT VIEW
        username.setText(usernameTxt);
        name.setText(nameTxt);
        mobile.setText(mobileTxt);
        address.setText(addressTxt);
        stall.setText(stallTxt);
        overall.setText(overallTxt);
        raters.setText(ratersTxt);
        Glide.with(image.getContext())
                .load(imageTxt)
                .centerCrop()
                .into(image);
        if (overallTxt != null && ratersTxt != null) {
            overall.setText(overallTxt);
            raters.setText(ratersTxt);
        }
        userProducts.setText(usernameTxt+"'s" + " Products");

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VendorData.this, MapsActivity.class));
            }
        });

        showMore = findViewById(R.id.btn_vendorDetails);
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address.getVisibility() == view.GONE && stallName.getVisibility() == view.GONE && stall.getVisibility() == view.GONE ) {
                    address.setVisibility(view.VISIBLE);
                    stallName.setVisibility(view.VISIBLE);
                    stall.setVisibility(view.VISIBLE);
                    map.setVisibility(view.VISIBLE);
                    showMore.setText("Show Less");
                }
                else {
                    address.setVisibility(view.GONE);
                    stallName.setVisibility(view.GONE);
                    stall.setVisibility(view.GONE);
                    map.setVisibility(view.GONE);
                    showMore.setText("Show More");
                }
            }
        });

        reportVendor = findViewById(R.id.btn_reportVendor);
        reportVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Report this vendor?");
                builder.setMessage("Press 'Yes' to confirm.");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent =  new Intent(VendorData.this, ReportVendor.class);
                        intent.putExtra("idTxt", idTxt);
                        intent.putExtra("username", usernameTxt);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });

        rateVendor = findViewById(R.id.btn_rateVendor);
        rateVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("vendorRatings").child(currentUser).child("rated").child(idTxt)
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (idTxt.equals(currentUser)) {
                            Toast.makeText(VendorData.this, "Don't rate yourself!", Toast.LENGTH_SHORT).show();
                        }
                        else if (!snapshot.child("reviewed").exists()) {
                            final DialogPlus dialogPlus = DialogPlus.newDialog(VendorData.this)
                                    .setContentHolder(new ViewHolder(R.layout.viewholder_rate_vendor))
                                    .setGravity(Gravity.CENTER)
                                    .create();

                            View data = dialogPlus.getHolderView();

                            ratingBar = data.findViewById(R.id.vRatingBar);
                            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                                    overallScore = ratingBar.getRating();
                                    userScore = ratingBar.getRating();

                                    if (v == 1.0) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 1.5) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 2.0) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 2.5) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 3.0) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 3.5) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 4.0) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 4.5) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 5.0) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();
                                    else if (v == 5.5) Toast.makeText(VendorData.this, String.valueOf(overallScore), Toast.LENGTH_SHORT).show();

                                }
                            });

                            dialogPlus.show();

                            submit = data.findViewById(R.id.btn_vSubmit);
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    databaseReference.child("users").child("vendor").child(idTxt).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.child("OverallScore").exists() || !snapshot.child("Raters").exists()) {
                                                overallScoreStr = String.format(Locale.US,"%.1f", overallScore);
                                                databaseReference.child("users").child("vendor").child(idTxt).child("OverallScore").setValue(String.valueOf(overallScore));
                                                databaseReference.child("users").child("vendor").child(idTxt).child("Raters").setValue(String.valueOf(1));
                                            }
                                            else {
                                                String pastScore = (String) snapshot.child("OverallScore").getValue();
                                                float pastScoreF = Float.parseFloat(pastScore);
                                                float nOverallScore = ((pastScoreF + overallScore)/2);
                                                overallScoreStr = String.format(Locale.US,"%.1f", nOverallScore);
                                                databaseReference.child("users").child("vendor").child(idTxt).child("OverallScore").setValue(overallScoreStr);

                                                String pastRaters = (String) snapshot.child("Raters").getValue();
                                                int pastRatersInt = Integer.parseInt(pastRaters);
                                                int newRaters = pastRatersInt + 1;
                                                databaseReference.child("users").child("vendor").child(idTxt).child("Raters").setValue(String.valueOf(newRaters));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
                                    databaseReference.child("vendorRatings").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            EditText comment = data.findViewById(R.id.et_vReview);
                                            String commentTxt = comment.getText().toString();
                                            userScoreStr = String.format(Locale.US,"%.1f", userScore);

                                            databaseReference.child("vendorRatings").child(currentUser).child("rated").child(idTxt).child("comment").setValue(commentTxt);
                                            databaseReference.child("vendorRatings").child(currentUser).child("rated").child(idTxt).child("rating").setValue(userScoreStr);

                                            databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("comment").setValue(commentTxt);
                                            databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("rating").setValue(userScoreStr);

                                            dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.child("consumer").hasChild(currentUser)) {
                                                        String firstName = (String)snapshot.child("consumer").child(currentUser).child("FirstName").getValue();
                                                        String lastName = (String)snapshot.child("consumer").child(currentUser).child("LastName").getValue();
                                                        String imageUrl = (String)snapshot.child("consumer").child(currentUser).child("ImageProfile").getValue();
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("id").setValue(currentUser);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("FirstName").setValue(firstName);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("LastName").setValue(lastName);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("ImageProfile").setValue(imageUrl);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("read").setValue("false");
                                                    }
                                                    else if (snapshot.child("vendor").hasChild(currentUser)) {
                                                        String firstName = (String)snapshot.child("vendor").child(currentUser).child("FirstName").getValue();
                                                        String lastName = (String)snapshot.child("vendor").child(currentUser).child("LastName").getValue();
                                                        String imageUrl = (String)snapshot.child("vendor").child(currentUser).child("ImageProfile").getValue();
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("id").setValue(currentUser);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("FirstName").setValue(firstName);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("LastName").setValue(lastName);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("ImageProfile").setValue(imageUrl);
                                                        databaseReference.child("vendorRatings").child(idTxt).child("reviews").child(currentUser).child("read").setValue("false");
                                                    }

                                                    // ADD TOTAL CLICKS HERE
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.child("vendorRatings").child(currentUser).child("rated").child(idTxt).child("reviewed").setValue(true);
                                            doOnce();
                                            Toast.makeText(VendorData.this, "Reviewed this vendor successfully!", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        }
                        else {
                            if (snapshot.child("reviewed").getValue().equals(true)) {
                                Toast.makeText(VendorData.this, "You have already rated this vendor.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        loadData(idTxt);
        loadReviews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vendorProductsAdapter.startListening();
        vendorProductsAdapter.notifyDataSetChanged();
        reviewsAdapter.startListening();
        reviewsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vendorProductsAdapter.stopListening();
        reviewsAdapter.stopListening();
    }

    private void loadData(String idTxt) {

        insideList = findViewById(R.id.lv_vendorProducts);
        insideList.setHasFixedSize(true);
        insideList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CategoryInsideModel> options;
        options = new FirebaseRecyclerOptions.Builder<CategoryInsideModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("products").child(idTxt), CategoryInsideModel.class)
                .build();

        vendorProductsAdapter = new VendorProductsAdapter(this, options);
        insideList.setAdapter(vendorProductsAdapter);

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

    private void loadReviews() {

        reviewsList = findViewById(R.id.lv_vendorReviews);
        reviewsList.setHasFixedSize(false);
        reviewsList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ReviewsModel> options;
        options = new FirebaseRecyclerOptions.Builder<ReviewsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("vendorRatings").child(idTxt).child("reviews"), ReviewsModel.class)
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

    public void doOnce() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //startSomethingOnce();
        switch (day) {
            case 1: {
                String dayTxt = "sunday";
                totalToday(dayTxt);
                break;
            }
            case 2: {
                String dayTxt = "monday";
                totalToday(dayTxt);
                break;
            }
            case 3: {
                String dayTxt = "tuesday";
                totalToday(dayTxt);
                break;
            }
            case 4: {
                String dayTxt = "wednesday";
                totalToday(dayTxt);
                break;
            }
            case 5: {
                String dayTxt = "thursday";
                totalToday(dayTxt);
                break;
            }
            case 6: {
                String dayTxt = "friday";
                totalToday(dayTxt);
                break;
            }
            case 7: {
                String dayTxt = "saturday";
                totalToday(dayTxt);
                break;
            }
        }
    }

    public void totalToday(String dayTxt) {
        DatabaseReference dataRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
        dataRef.child("statistics").child("reviews").child(dayTxt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("total")) {
                    dataRef.child("statistics").child("reviews").child(dayTxt).child("total").setValue(1);
                }
                else {
                    long total = (long) Objects.requireNonNull(snapshot.child("total").getValue());
                    dataRef.child("statistics").child("reviews").child(dayTxt).child("total").setValue(total + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCategoryClick(int position) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(VendorData.this, CategoryVendor.class));
        finish();
    }
}