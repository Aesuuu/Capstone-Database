package com.example.capstone_project_redo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.nav.HomePage;
import com.example.capstone_project_redo.nav.MyAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    StorageReference uItemStorageRef;
    Uri imageUri;
    ProgressDialog progressDialog;
    String imageProofUrl;

    EditText editfirstname, editlastname, editMarket, editmobile, editStall;
    Button Updateprofile, selectImageBtn, clearImageBtn;
    ImageView addProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Profile Information ID
        editfirstname = findViewById(R.id.e_firstname);
        editlastname = findViewById(R.id.e_lastname);
        editMarket = findViewById(R.id.e_marketAdd);
        editmobile = findViewById(R.id.e_mobile);
        editStall = findViewById(R.id.et_stallDesc);
        addProfileImage = findViewById(R.id.iv_addProfileImage);

        //Button ID
        selectImageBtn = findViewById(R.id.btn_selectImage);
        clearImageBtn = findViewById(R.id.btn_clearProfileImage);
        Updateprofile = findViewById(R.id.btn_UpdateProfile);

        progressDialog = new ProgressDialog(this);


        ActivityResultLauncher<String> mGetImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            imageUri = result;
                            addProfileImage.setImageURI(imageUri);
                        }
                    }
                });


        selectImageBtn.setOnClickListener(v -> mGetImage.launch("image/*"));
        clearImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProfileImage.setImageResource(0);
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(this, "" + user.getUid(), Toast.LENGTH_SHORT).show();

        databaseReference = database.getReference("users").child("vendor").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String firstName = (String) snapshot.child("FirstName").getValue();
                String lastName = (String) snapshot.child("LastName").getValue();
                String mobile = (String) snapshot.child("MobileNumber").getValue();
                String marketAdd = (String) snapshot.child("MarketAddress").getValue();
                String stallDesc = (String) snapshot.child("StallDescription").getValue();
                String url = (String) snapshot.child("ImageProfile").getValue();

                editfirstname.setText(firstName);
                editlastname.setText(lastName);
                editmobile.setText(mobile);
                editMarket.setText(marketAdd);
                editStall.setText(stallDesc);

                Glide.with(getApplicationContext()).load(url).into(addProfileImage);

                Updateprofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final String firstname_edit = editfirstname.getText().toString();
                        final String lastname_edit = editlastname.getText().toString();
                        final String mobile_edit = editmobile.getText().toString();
                        final String market_edit = editMarket.getText().toString();
                        final String stall_edit = editStall.getText().toString();


                        if (addProfileImage.getDrawable() == null) {
                            Toast.makeText(EditProfileActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (firstname_edit.isEmpty() || lastname_edit.isEmpty() || mobile_edit.isEmpty() || market_edit.isEmpty() || stall_edit.isEmpty()) {
                                Toast.makeText(EditProfileActivity.this, "Please Fill all fields", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                databaseReference.child("FirstName").setValue(firstname_edit);
                                databaseReference.child("LastName").setValue(lastname_edit);
                                databaseReference.child("MobileNumber").setValue(mobile_edit);
                                databaseReference.child("MarketAddress").setValue(market_edit);
                                databaseReference.child("StallDescription").setValue(stall_edit);

                                uploadImage();

                                Toast.makeText(EditProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                ProfileTransition();
                                finish();
                            }
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            private void ProfileTransition() {
                Intent intent = new Intent(EditProfileActivity.this, MyAccount.class);
                startActivity(intent);
            }
        });

    }
    private void uploadImage() {

        String profileKey = database.getReference().push().getKey();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            if (imageUri != null) {
                uItemStorageRef = FirebaseStorage.getInstance().getReference("users/" + uid +"/"+"Profile/"+profileKey);
                uItemStorageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uItemStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageProofUrl = uri.toString();
                                databaseReference.child("ImageProfile").setValue(imageProofUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                });
            }
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditProfileActivity.this, MyAccount.class));
        finish();
    }
}