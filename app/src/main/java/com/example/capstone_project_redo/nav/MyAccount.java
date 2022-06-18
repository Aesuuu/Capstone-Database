package com.example.capstone_project_redo.nav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.capstone_project_redo.ChangePassword;
import com.example.capstone_project_redo.ConsumerCarousel;
import com.example.capstone_project_redo.CreateAccountPart1;
import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.EditProfileActivity;
import com.example.capstone_project_redo.MapsActivity;
import com.example.capstone_project_redo.MyProfileActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.VendorCarousel;
import com.example.capstone_project_redo.databinding.ActivityMyAccountBinding;
import com.example.capstone_project_redo.databinding.ActivitySrpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyAccount extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;

    ImageView image;
    TextView name, email, username, number, address, stall;
    Button edit, reset, map, tutorial;

    ActivityMyAccountBinding accountBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBinding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        setContentView(accountBinding.getRoot());
        allocateActivityTitle("Account");

        uAuth = FirebaseAuth.getInstance();
        String currentUser = Objects.requireNonNull(uAuth.getCurrentUser()).getUid();
        image = findViewById(R.id.iv_acctImage);
        name = findViewById(R.id.tv_acctName);
        email = findViewById(R.id.tv_acctEmail);
        username = findViewById(R.id.tv_acctUsername);
        number = findViewById(R.id.tv_acctMobile);
        address = findViewById(R.id.tv_acctMarket);
        stall = findViewById(R.id.tv_acctStall);

        edit = findViewById(R.id.btn_acctEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccount.this, EditProfileActivity.class));
            }
        });

        reset = findViewById(R.id.btn_acctChange);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccount.this, ChangePassword.class));
            }
        });

        map = findViewById(R.id.btn_gMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccount.this, MapsActivity.class));
            }
        });

        databaseReference.child("users").child("vendor").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageTxt = (String)snapshot.child("ImageProfile").getValue();
                String nameTxt = (String)snapshot.child("FirstName").getValue() + " " + snapshot.child("LastName").getValue();
                String emailTxt = (String)snapshot.child("EmailAddress").getValue();
                String usernameTxt = (String)snapshot.child("Username").getValue();
                String numberTxt = (String)snapshot.child("MobileNumber").getValue();
                String addressTxt = (String)snapshot.child("MarketAddress").getValue();
                String stallTxt = (String)snapshot.child("StallDescription").getValue();

                Glide.with(MyAccount.this).load(imageTxt).centerInside().into(image);
                name.setText(nameTxt);
                email.setText(emailTxt);
                username.setText(usernameTxt);
                number.setText(numberTxt);
                address.setText(addressTxt);
                stall.setText(stallTxt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tutorial = findViewById(R.id.btn_vShowTutorial);
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this, VendorCarousel.class);
                startActivity(intent);
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
                    startActivity(new Intent(MyAccount.this, Dashboard.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}