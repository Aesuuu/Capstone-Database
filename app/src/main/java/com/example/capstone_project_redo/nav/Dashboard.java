package com.example.capstone_project_redo.nav;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstone_project_redo.ConsumerCarousel;
import com.example.capstone_project_redo.DrawerBaseActivity;
import com.example.capstone_project_redo.LoginActivity;
import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.VendorCarousel;
import com.example.capstone_project_redo.databinding.ActivityDashboardBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Dashboard extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;
    String currentUser, greet, msg;
    String nameTxt, url, status, dayTxt;



    ActivityDashboardBinding dashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(dashboardBinding.getRoot());
        allocateActivityTitle("Dashboard");

        showCarousel();

        uAuth = FirebaseAuth.getInstance();
        currentUser = Objects.requireNonNull(uAuth.getCurrentUser()).getUid();

        BarChart categoryBarChart = findViewById(R.id.categoryBarChart);
        userStatistic(categoryBarChart);

        BarChart productBarChart = findViewById(R.id.productBarChart);
        productStatistics(productBarChart);


        BarChart reviewsBarChart = findViewById(R.id.reviewsBarChart);
        reviewStatistic(reviewsBarChart);

        reviewNotification();
        appFormResponse();
    }

    public void userStatistic(BarChart barChart) {
        daysOfWeek();

        databaseReference.child("statistics").child("categories").child(dayTxt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (!snapshot.child("BasicNecessities").child("total").exists()) {
                        databaseReference.child("statistics").child("categories").child(dayTxt).child("BasicNecessities").child("total").setValue(0);
                    }
                    if (!snapshot.child("CraftedGoods").child("total").exists()) {
                        databaseReference.child("statistics").child("categories").child(dayTxt).child("CraftedGoods").child("total").setValue(0);
                    }
                    if (!snapshot.child("Food").child("total").exists()) {
                        databaseReference.child("statistics").child("categories").child(dayTxt).child("Food").child("total").setValue(0);
                    }
                    if (!snapshot.child("Miscellaneous").child("total").exists()) {
                        databaseReference.child("statistics").child("categories").child(dayTxt).child("Miscellaneous").child("total").setValue(0);
                    }
                    long mBasic = (long) (snapshot.child("BasicNecessities").child("total").getValue());
                    long mCraft = (long) (snapshot.child("CraftedGoods").child("total").getValue());
                    long mFood = (long) (snapshot.child("Food").child("total").getValue());
                    long mMisc = (long) (snapshot.child("Miscellaneous").child("total").getValue());

                    ArrayList<BarEntry> visitors = new ArrayList<>();
                    visitors.add(new BarEntry(1, mBasic));
                    visitors.add(new BarEntry(2, mCraft));
                    visitors.add(new BarEntry(3, mFood));
                    visitors.add(new BarEntry(4, mMisc));

                    BarDataSet barDataSet = new BarDataSet(visitors, dayTxt);
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);
                    barChart.animateY(1000);

                } catch (Exception ignored) {
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void productStatistics(BarChart barChart) {
        daysOfWeek();

        TextView productNames = findViewById(R.id.textView40);

        DatabaseReference dataRef = database.getReference().child("statistics").child("products");
        Query topProducts = dataRef.child(dayTxt).child(currentUser).orderByChild("total").limitToLast(5);
        topProducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> products = new ArrayList<>();
                int count = 1;

                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    String Key = childSnapshot.getKey();

                    String pastString = (String) productNames.getText();
                    String name = (String) (snapshot.child(Key).child("name").getValue());
                    productNames.setText(pastString+count+". "+name+"\n");

                    long product = (long) (snapshot.child(Key).child("total").getValue());
                    products.add(new BarEntry(count, product));
                    count = count + 1;
                }

                BarDataSet barDataSet = new BarDataSet(products, dayTxt);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barChart.animateY(1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
                try {

                    long mBasic = (long) (snapshot.child("BasicNecessities").child("total").getValue());
                    long mCraft = (long) (snapshot.child("CraftedGoods").child("total").getValue());
                    long mFood = (long) (snapshot.child("Food").child("total").getValue());
                    long mMisc = (long) (snapshot.child("Miscellaneous").child("total").getValue());



                    visitors.add(new BarEntry(2, mCraft));
                    visitors.add(new BarEntry(3, mFood));
                    visitors.add(new BarEntry(4, mMisc));



                } catch (Exception ignored) {
                }

         */
    }

    public void reviewStatistic(BarChart barChart) {
        daysOfWeek();

        databaseReference.child("statistics").child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    long monday = (long) Objects.requireNonNull(snapshot.child("monday").child("total").getValue());
                    long tuesday = (long) Objects.requireNonNull(snapshot.child("tuesday").child("total").getValue());
                    long wednesday = (long) Objects.requireNonNull(snapshot.child("wednesday").child("total").getValue());
                    long thursday = (long) Objects.requireNonNull(snapshot.child("thursday").child("total").getValue());
                    long friday = (long) Objects.requireNonNull(snapshot.child("friday").child("total").getValue());
                    long saturday = (long) Objects.requireNonNull(snapshot.child("saturday").child("total").getValue());
                    long sunday = (long) Objects.requireNonNull(snapshot.child("sunday").child("total").getValue());

                    ArrayList<BarEntry> reviews = new ArrayList<>();
                    reviews.add(new BarEntry(1, monday));
                    reviews.add(new BarEntry(2, tuesday));
                    reviews.add(new BarEntry(3, wednesday));
                    reviews.add(new BarEntry(4, thursday));
                    reviews.add(new BarEntry(5, friday));
                    reviews.add(new BarEntry(6, saturday));
                    reviews.add(new BarEntry(7, sunday));

                    final String[] labels = new String[] {"Sample", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

                    BarDataSet barDataSet = new BarDataSet(reviews, dayTxt);
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);
                    barChart.animateY(1000);

                } catch (Exception ignored) {
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void daysOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //startSomethingOnce();
        switch (day) {
            case 1: {
                dayTxt= "sunday";
                break;
            }
            case 2: {
                dayTxt= "monday";
                break;
            }
            case 3: {
                dayTxt= "tuesday";
                break;
            }
            case 4: {
                dayTxt= "wednesday";
                break;
            }
            case 5: {
                dayTxt= "thursday";
                break;
            }
            case 6: {
                dayTxt= "friday";
                break;
            }
            case 7: {
                dayTxt= "saturday";
                break;
            }
        }
    }
/*
    public void resetWeek() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AdminDashboard.this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        if (week != lastTimeStarted) {

        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("last_time_started", today);
        editor.apply();
    }


 */
    public void appFormResponse() {
        databaseReference.child("users").child("vendor").child(currentUser).child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (Objects.requireNonNull(snapshot.getValue()).toString().equals("approved")) {
                    greet = "CONGRATS!";
                    msg = "Your application form has been approved! You may now use features that are exclusive for vendor accounts.";
                    callDialogNotif(greet, msg);
                }
                else if (Objects.requireNonNull(snapshot.getValue()).toString().equals("rejected")) {
                    greet = "Your application form has been rejected.";
                    msg = "Please wait for management to contact you about the cause for rejection.";
                    callDialogNotif(greet, msg);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void reviewNotification() {
        databaseReference.child("vendorRatings").child(currentUser).child("reviews").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                status = (String)snapshot.child("read").getValue();
                if (status != null) {
                    if (status.equals("false")) {

                        nameTxt = (String)snapshot.child("FirstName").getValue() + " " + snapshot.child("LastName").getValue();
                        url = (String)snapshot.child("ImageProfile").getValue();
                        callReviewNotif();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.iv_bell) {
            databaseReference.child("vendorRatings").child(currentUser).child("reviews").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (status != null) {
                        if (snapshot.hasChildren() && status.equals("false")) {
                            callReviewNotif();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return true;
        }
        return true;
    }

    public void callReviewNotif() {

        final DialogPlus dialogPlus = DialogPlus.newDialog(Dashboard.this)
                .setContentHolder(new ViewHolder(R.layout.viewholder_new_notif))
                .setGravity(Gravity.TOP).setCancelable(false)
                .create();
        dialogPlus.show();

        View data = dialogPlus.getHolderView();

        TextView name = data.findViewById(R.id.tv_nNotifName);
        name.setText(nameTxt + " reviewed your business!");
        ImageView image = data.findViewById(R.id.iv_nNotifImage);
        Glide.with(Dashboard.this).load(url).centerInside().into(image);

        TextView hide = data.findViewById(R.id.tv_hide);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPlus.dismiss();
            }
        });
        Button showAll = data.findViewById(R.id.btn_nShowAll);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, Reviews.class));
            }
        });
    }

    public void callDialogNotif(String greeting, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(greeting);
        builder.setMessage(message);

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("users").child("vendor").child(currentUser).child("message").removeValue();
            }
        });
        builder.show();
    }

    public void showCarousel() {
        uAuth = FirebaseAuth.getInstance();
        String currentConsumer = uAuth.getCurrentUser().getUid();
        databaseReference.child("users").child("vendor").child(currentConsumer).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("carousel").exists()) {
                    startActivity(new Intent(Dashboard.this, VendorCarousel.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "It's painfully obvious but something went wrong!", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(new Intent(Dashboard.this, LoginActivity.class)));
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