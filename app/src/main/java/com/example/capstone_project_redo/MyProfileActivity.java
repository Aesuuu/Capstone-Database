package com.example.capstone_project_redo;

import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.databinding.ActivityMyProfileBinding;
import com.example.capstone_project_redo.nav.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyProfileActivity extends DrawerBaseActivity {

    ActivityMyProfileBinding activityMyProfileBinding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    StorageReference profileStorageRef;
    TextView profileUser, profileFullName, profileAge, profileAddress, profileStallDesc, profileMobile, profileEmail;
    ImageView profilepic;
    String stringGlide;

    ProgressDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMyProfileBinding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(activityMyProfileBinding.getRoot());
        allocateActivityTitle("Profile");

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Please wait while we fetch your data");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        Button UpdateProfile;
        UpdateProfile = findViewById(R.id.btn_EditProfile);

        UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this, EditProfileActivity.class));
            }
        });
        // Firebase Data
        profileUser = findViewById(R.id.tv_profileUser);
        profileFullName = findViewById(R.id.tv_profileFullName);
        profileAge = findViewById(R.id.tv_profileAge);
        profileAddress = findViewById(R.id.tv_profileAddress);
        profileStallDesc = findViewById(R.id.tv_profileAddDesc);
        profileMobile = findViewById(R.id.tv_profileMobile);
        profileEmail = findViewById(R.id.tv_profileEmail);
        profilepic = findViewById(R.id.iv_acctImage);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        databaseReference = database.getReference("users").child(currentUser.getUid());
        String profileKey = database.getReference("imageProof").push().getKey();

        profileStorageRef = FirebaseStorage.getInstance().getReference("users/" + uid +"imageProof/"+profileKey);

        databaseReference = database.getReference("users").child(currentUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String username = (String) snapshot.child("Username").getValue();
                String firstName = (String) snapshot.child("FirstName").getValue();
                String lastName = (String) snapshot.child("LastName").getValue();
                String fullName = firstName + " " + lastName;
                String age = (String) snapshot.child("Age").getValue();
                String municipality = (String) snapshot.child("Municipality").getValue();
                String province = (String) snapshot.child("Province").getValue();
                String stall = (String) snapshot.child("StallDescription").getValue();
                String marketAddress = municipality + ", " + province;
                String mobile = (String) snapshot.child("MobileNumber").getValue();
                String email = (String) snapshot.child("EmailAddress").getValue();
                String url = (String) snapshot.child("ImageProfile").getValue();

                profileUser.setText(username);
                profileFullName.setText(fullName);
                profileAge.setText(age);
                profileAddress.setText(marketAddress);
                profileStallDesc.setText(stall);
                profileMobile.setText(mobile);
                profileEmail.setText(email);
                stringGlide = url;
                Glide.with(MyProfileActivity.this).load(url).centerInside().into(profilepic);

                if (loadingProgress.isShowing()){
                    loadingProgress.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyProfileActivity.this, HomePage.class));
        finish();
    }

}