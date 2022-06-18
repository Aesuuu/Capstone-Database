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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConsumerCarousel extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_carousel);

        Carousel();

        ImageView close = findViewById(R.id.btn_cCarouselClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Warning");
                builder.setMessage("Tutorial will now be hidden. Please view every image before exiting.");

                builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
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

        ImageSlider imageSlider = findViewById(R.id.cSlider);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.c1));
        slideModels.add(new SlideModel(R.drawable.c2));
        slideModels.add(new SlideModel(R.drawable.c3));
        slideModels.add(new SlideModel(R.drawable.c4));
        slideModels.add(new SlideModel(R.drawable.c5));
        slideModels.add(new SlideModel(R.drawable.c6));
        slideModels.add(new SlideModel(R.drawable.c7));
        slideModels.add(new SlideModel(R.drawable.c8));
        slideModels.add(new SlideModel(R.drawable.c9));

        imageSlider.setImageList(slideModels, true);
    }

    public void showCarousel() {
        uAuth = FirebaseAuth.getInstance();
        String currentUser = uAuth.getCurrentUser().getUid();
        databaseReference.child("users").child("consumer").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child("users").child("consumer").child(currentUser).child("carousel").setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConsumerCarousel.this, "It's painfully obvious but something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}