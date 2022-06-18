package com.example.capstone_project_redo;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstone_project_redo.databinding.ActivityCreateAccountPart1Binding;
import com.example.capstone_project_redo.databinding.ActivitySrpBinding;
import com.example.capstone_project_redo.nav.AboutActivity;
import com.example.capstone_project_redo.nav.HomePage;
import com.example.capstone_project_redo.nav.MyAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountPart1 extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    StorageReference uItemStorageRef;
    FirebaseAuth uAuth;
    Uri imageUri;
    ProgressDialog loadingProgress;
    String imageProofUrl;

    EditText username, mobile, address, stall;

    Button select, clear, submit;
    ImageView addValidationImage;

    ActivityCreateAccountPart1Binding createAccountPart1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAccountPart1 = ActivityCreateAccountPart1Binding.inflate(getLayoutInflater());
        setContentView(createAccountPart1.getRoot());
        allocateActivityTitle("Vendor Application Form");

        uAuth = FirebaseAuth.getInstance();
        select = findViewById(R.id.btn_registerSelectImg);
        clear = findViewById(R.id.btn_registerClearImg);
        submit = findViewById(R.id.btn_regApply);
        addValidationImage = findViewById(R.id.iv_validationImage);

        username = findViewById(R.id.et_regUsername);
        mobile = findViewById(R.id.et_regMobile);
        address = findViewById(R.id.et_regMarket);
        stall = findViewById(R.id.et_regStall);

        select.setOnClickListener(v -> mGetImage.launch("image/*"));
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addValidationImage.setImageResource(0);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingProgress = new ProgressDialog(CreateAccountPart1.this);
                loadingProgress.setMessage("Please wait while we fetch your data");
                loadingProgress.setCancelable(false);
                loadingProgress.show();

                String usernameTxt = username.getText().toString();
                String mobileTxt = mobile.getText().toString();
                String addressTxt = address.getText().toString();
                String stallTxt = stall.getText().toString();

                if (usernameTxt.equals("") || mobileTxt.equals("") || addressTxt.equals("") || stallTxt.equals("")) {
                    Toast.makeText(CreateAccountPart1.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    loadingProgress.dismiss();
                } else {
                    if (addValidationImage.getDrawable() == null) {
                        Toast.makeText(CreateAccountPart1.this, "Please select an image", Toast.LENGTH_SHORT).show();
                        loadingProgress.dismiss();
                    }
                    else {
                        String currentUser = uAuth.getCurrentUser().getUid();
                        uploadImage();

                        databaseReference.child("users").child("consumer").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String email = (String)snapshot.child("EmailAddress").getValue();
                                String password = (String)snapshot.child("Password").getValue();
                                String firstName = (String)snapshot.child("FirstName").getValue();
                                String lastName = (String)snapshot.child("LastName").getValue();
                                String url = (String)snapshot.child("ImageProfile").getValue();

                                Map<String,Object> map = new HashMap<>();
                                map.put("id", currentUser);
                                map.put("EmailAddress", email);
                                map.put("Password", password);
                                map.put("FirstName", firstName);
                                map.put("LastName", lastName);
                                map.put("ImageProfile", url);
                                map.put("Username", usernameTxt);
                                map.put("MobileNumber", mobileTxt);
                                map.put("MarketAddress", addressTxt);
                                map.put("StallDescription", stallTxt);

                                databaseReference.child("pendingRequest").child("unread").child(currentUser).updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                doOnce();
                                                Toast.makeText(CreateAccountPart1.this, "Vendor application form has been sent to management", Toast.LENGTH_SHORT).show();
                                                loadingProgress.dismiss();
                                                startActivity(new Intent(CreateAccountPart1.this, AboutActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CreateAccountPart1.this, "Failed to send vendor application form. Try again after 24 hours.", Toast.LENGTH_SHORT).show();
                                                loadingProgress.dismiss();
                                                startActivity(new Intent(CreateAccountPart1.this, AboutActivity.class));
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                loadingProgress.dismiss();
                            }
                        });
                    }
                }
            }
        });
    }

    // This enables users to pick an image,then receive its uri
    ActivityResultLauncher<String> mGetImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        imageUri = result;
                        addValidationImage.setImageURI(imageUri);
                    }
                }
            });

    private void uploadImage() {

        String validationKey = database.getReference().push().getKey();

        FirebaseUser currentUser = uAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            uItemStorageRef = FirebaseStorage.getInstance().getReference("users/" + uid +"/"+"imageProof/" + validationKey);

            uItemStorageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uItemStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageProofUrl = uri.toString();

                            databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
                            databaseReference.child("pendingRequest").child("unread").child(uid).child("ImageProof").setValue(imageProofUrl);
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
        dataRef.child("statistics").child("requests").child("pending").child(dayTxt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("total")) {
                    dataRef.child("statistics").child("requests").child("pending").child(dayTxt).child("total").setValue(1);
                }
                else {
                    long total = (long) Objects.requireNonNull(snapshot.child("total").getValue());
                    dataRef.child("statistics").child("requests").child("pending").child(dayTxt).child("total").setValue(total + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateAccountPart1.this, AboutActivity.class));
        finish();
    }
}