package com.example.capstone_project_redo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.capstone_project_redo.nav.Dashboard;
import com.example.capstone_project_redo.nav.HomePage;
import com.example.capstone_project_redo.nav.MyAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VendorCarousel extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_carousel);

        Carousel();

        ImageView close = findViewById(R.id.btn_vCarouselClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Tutorial will be hidden.");
                builder.setMessage("This tutorial will now be hidden. Check the 'My Account' page to view the tutorial again.");

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                showCarousel();
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                finish();
                            }
                        });
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
    }

    private void Carousel() {

        ImageSlider imageSlider = findViewById(R.id.vSlider);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.v1));
        slideModels.add(new SlideModel(R.drawable.v2));
        slideModels.add(new SlideModel(R.drawable.v3));
        slideModels.add(new SlideModel(R.drawable.v4));
        slideModels.add(new SlideModel(R.drawable.v5));
        slideModels.add(new SlideModel(R.drawable.v6));
        slideModels.add(new SlideModel(R.drawable.v7));
        slideModels.add(new SlideModel(R.drawable.v8));
        slideModels.add(new SlideModel(R.drawable.v9));
        slideModels.add(new SlideModel(R.drawable.v10));
        slideModels.add(new SlideModel(R.drawable.v11));
        slideModels.add(new SlideModel(R.drawable.v12));

        imageSlider.setImageList(slideModels, true);
    }

    public void showCarousel() {
        uAuth = FirebaseAuth.getInstance();
        String currentUser = uAuth.getCurrentUser().getUid();
        databaseReference.child("users").child("vendor").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child("users").child("vendor").child(currentUser).child("carousel").setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VendorCarousel.this, "It's painfully obvious but something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}