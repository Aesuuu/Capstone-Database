package com.example.capstone_project_redo.nav;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.capstone_project_redo.ConsumerCarousel;
import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.LoginActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.databinding.ActivityHomePageBinding;
import com.example.capstone_project_redo.databinding.ActivityMyProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class HomePage extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    Button toCategory, toAbout, toDti;

    FirebaseAuth uAuth;

    ActivityHomePageBinding activityHomePageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomePageBinding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(activityHomePageBinding.getRoot());
        allocateActivityTitle("Home");

        showCarousel();

        uAuth = FirebaseAuth.getInstance();

        toCategory = findViewById(R.id.btn_hCategory);
        toDti = findViewById(R.id.btn_hDti);
        toAbout = findViewById(R.id.btn_hAbout);

        toCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, CategoryProduct.class));
            }
        });
        toDti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, SRPActivity.class));
            }
        });
        toAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, AboutActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentUser = uAuth.getCurrentUser().getUid();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("vendor").hasChild(currentUser)) {
                    doOnce("vendor");
                }
                else if ((snapshot.child("consumer").hasChild(currentUser))) {
                    doOnce("consumer");
                }
                else Toast.makeText(HomePage.this, "Ya donkey, this method ain't working!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void doOnce(String typeOfUser) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (today != lastTimeStarted) {
            //startSomethingOnce();
            switch (day) {
                case 1: {
                    String dayTxt = "sunday";
                    totalToday(dayTxt, typeOfUser);
                    break;
                }
                case 2: {
                    String dayTxt = "monday";
                    totalToday(dayTxt, typeOfUser);
                    break;
                }
                case 3: {
                    String dayTxt = "tuesday";
                    totalToday(dayTxt, typeOfUser);
                    break;
                }
                case 4: {
                    String dayTxt = "wednesday";
                    totalToday(dayTxt, typeOfUser);
                    break;
                }
                case 5: {
                    String dayTxt = "thursday";
                    totalToday(dayTxt, typeOfUser);
                    break;
                }
                case 6: {
                    String dayTxt = "friday";
                    totalToday(dayTxt, typeOfUser);
                    break;
                }
                case 7: {
                    String dayTxt = "saturday";
                    totalToday(dayTxt, typeOfUser);
                    break;
                }
            }

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.apply();
        }
    }

    public void totalToday(String dayTxt, String typeOfUser) {
        try{
            databaseReference.child("statistics").child(dayTxt).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long visitors = (long) Objects.requireNonNull(snapshot.child(typeOfUser).child("visitors").getValue());
                    databaseReference.child("statistics").child(dayTxt).child(typeOfUser).child("visitors").setValue(visitors + 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception ignored) {
        }
    }

    public void showCarousel() {
        uAuth = FirebaseAuth.getInstance();
        String currentConsumer = uAuth.getCurrentUser().getUid();
        databaseReference.child("users").child("consumer").child(currentConsumer).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("carousel").exists()) {
                    startActivity(new Intent(HomePage.this, ConsumerCarousel.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, "It's painfully obvious but something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Pressing 'Confirm' will log out your account.");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(new Intent(HomePage.this, LoginActivity.class)));
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

}