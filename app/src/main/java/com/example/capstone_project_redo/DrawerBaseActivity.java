package com.example.capstone_project_redo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.category.VendorData;
import com.example.capstone_project_redo.nav.AboutActivity;
import com.example.capstone_project_redo.nav.CategoryProduct;
import com.example.capstone_project_redo.nav.Dashboard;
import com.example.capstone_project_redo.nav.HomePage;
import com.example.capstone_project_redo.nav.MyAccount;
import com.example.capstone_project_redo.nav.MyProductsActivity;
import com.example.capstone_project_redo.nav.Reviews;
import com.example.capstone_project_redo.nav.SRPActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");

    ProgressDialog loadingProgress;
    FirebaseAuth uAuth = FirebaseAuth.getInstance();

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView imageProfile;
    TextView name;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);


        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage("Loading, please wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null && user.isEmailVerified()) {

            databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    imageProfile = findViewById(R.id.iv_navPicture);
                    name = findViewById(R.id.tv_navName);

                    if (snapshot.child("consumer").hasChild(user.getUid())) {
                        hideItem();

                        String profile = (String) snapshot.child("consumer").child(user.getUid()).child("ImageProfile").getValue();
                        String firstName = (String) snapshot.child("consumer").child(user.getUid()).child("FirstName").getValue();
                        String lastName = (String) snapshot.child("consumer").child(user.getUid()).child("LastName").getValue();

                        name.setText(firstName + " " + lastName);
                        try {
                            Glide.with(DrawerBaseActivity.this).load(profile).centerInside().into(imageProfile);
                        }catch (Exception ignored) {

                        }
                        loadingProgress.dismiss();
                    }
                    else if (snapshot.child("vendor").hasChild(user.getUid())) {
                        String profile = (String) snapshot.child("vendor").child(user.getUid()).child("ImageProfile").getValue();
                        String firstName = (String) snapshot.child("vendor").child(user.getUid()).child("FirstName").getValue();
                        String lastName = (String) snapshot.child("vendor").child(user.getUid()).child("LastName").getValue();

                        name.setText(firstName + " " + lastName);
                        try {
                            Glide.with(DrawerBaseActivity.this).load(profile).centerInside().into(imageProfile);
                            imageProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getApplicationContext(), MyAccount.class));
                                }
                            });
                        }catch (Exception ignored) {

                        }

                        loadingProgress.dismiss();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingProgress.dismiss();
                }
            });
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                FirebaseAuth touAuth = FirebaseAuth.getInstance();
                String typeOfUser = Objects.requireNonNull(touAuth.getCurrentUser()).getUid();
                DatabaseReference dataRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
                dataRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("vendor").hasChild(typeOfUser)) {
                            startActivity(new Intent(DrawerBaseActivity.this, Dashboard.class));
                            finish();
                        }
                        else if (snapshot.child("consumer").hasChild(typeOfUser)) {
                            startActivity(new Intent(DrawerBaseActivity.this, HomePage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_dti:
                startActivity(new Intent(this, SRPActivity.class));
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_category:
                startActivity(new Intent(this, CategoryProduct.class));
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_account:
                startActivity(new Intent(this, MyAccount.class));
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_reviews:
                startActivity(new Intent(this, Reviews.class));
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_upload:
                startActivity(new Intent(this, MyProductsActivity.class));
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Pressing 'Confirm' will log out your account.");

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(new Intent(getApplicationContext(), LoginActivity.class)));
                        finish();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Log Out Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                overridePendingTransition(0, 0);
                break;
        }

        return false;
    }
    private void hideItem()
    {
        // SHOW ONLY NAV FOR CONSUMERS
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_upload).setVisible(false);
        nav_Menu.findItem(R.id.nav_account).setVisible(false);
        nav_Menu.findItem(R.id.nav_reviews).setVisible(false);
    }

    protected void allocateActivityTitle(String titleString) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleString);
        }
    }
}