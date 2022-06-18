package com.example.capstone_project_redo.nav;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstone_project_redo.CreateAccountPart1;
import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.adapter.AboutAdapter;
import com.example.capstone_project_redo.databinding.ActivityAboutBinding;
import com.example.capstone_project_redo.model.AboutModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AboutActivity extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    DatabaseReference dataRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;
    String userId;

    ProgressDialog loadingProgress;
    RecyclerView faqList;
    AboutAdapter aboutAdapter;

    CardView applyPrompt;
    Button apply;

    ActivityAboutBinding activityAboutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAboutBinding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(activityAboutBinding.getRoot());
        allocateActivityTitle("About the App & FAQ");

        uAuth = FirebaseAuth.getInstance();
        userId = uAuth.getCurrentUser().getUid();

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading Questions, Please Wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        applyPrompt = findViewById(R.id.cardLearnMore);
        applyPrompt.setVisibility(View.GONE);
        FirebaseUser user = uAuth.getCurrentUser();
        assert user != null;
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("consumer").hasChild(user.getUid())) {
                    applyPrompt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        apply = findViewById(R.id.btn_applyNow);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataRef.child("pendingRequest").child("unread").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(userId)) {
                            Toast.makeText(AboutActivity.this, "You already submitted a request.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(AboutActivity.this, "Please wait 2-3 working days to process request.", Toast.LENGTH_LONG).show();
                        }
                        else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Are you a vendor?");
                            builder.setMessage("Press 'Yes' to proceed to application form.");

                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(AboutActivity.this, CreateAccountPart1.class));
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        aboutAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        aboutAdapter.stopListening();
    }

    private void loadData() {
        faqList = findViewById(R.id.lv_faqList);
        faqList.setHasFixedSize(true);
        faqList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<AboutModel> options =
                new FirebaseRecyclerOptions.Builder<AboutModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("faq"), AboutModel.class)
                        .build();


        aboutAdapter = new AboutAdapter(options);
        faqList.setAdapter(aboutAdapter);

        databaseReference = database.getReference("products");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                }
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
                    startActivity(new Intent(AboutActivity.this, Dashboard.class));
                    finish();
                }
                else if (snapshot.child("consumer").hasChild(typeOfUser)) {
                    startActivity(new Intent(AboutActivity.this, HomePage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}