package com.example.capstone_project_redo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone_project_redo.databinding.ActivityReportBinding;
import com.example.capstone_project_redo.databinding.CategoryInsideVendorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportVendor extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;

    TextView tvVendorReported;
    EditText etSubject, etBody;
    Button btnSubmit;
    ActivityReportBinding reportBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportBinding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(reportBinding.getRoot());
        allocateActivityTitle("Report Vendor");

        uAuth = FirebaseAuth.getInstance();
        String currentUser = uAuth.getCurrentUser().getUid();

        // INTENT FROM ADAPTER
        Intent extra = getIntent();
        String idTxt = extra.getStringExtra("idTxt");
        String usernameTxt = extra.getStringExtra("username");

        tvVendorReported = findViewById(R.id.tv_rVendor);
        tvVendorReported.setText("You are currently reporting " + usernameTxt);
        etSubject = findViewById(R.id.et_rSubject);
        etBody = findViewById(R.id.et_rBody);

        btnSubmit = findViewById(R.id.btn_rSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String subject = etSubject.getText().toString();
                String body = etBody.getText().toString();

                if (idTxt.equals(currentUser)) {
                    Toast.makeText(ReportVendor.this, "Are you seriously reporting yourself?", Toast.LENGTH_SHORT).show();
                }
                else if (subject.equals("") || body.equals("")) {
                    Toast.makeText(ReportVendor.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
                    databaseReference.child("vendorRatings").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String reportKey = database.getReference("vendorRatings").push().getKey();

                            databaseReference.child("vendorRatings").child(currentUser).child("reported").child(reportKey).child("subject").setValue(subject);
                            databaseReference.child("vendorRatings").child(currentUser).child("reported").child(reportKey).child("body").setValue(body);
                            databaseReference.child("vendorRatings").child(currentUser).child("reported").child(reportKey).child("id").setValue(idTxt);

                            databaseReference.child("vendorRatings").child(idTxt).child("reports").child(reportKey).child("subject").setValue(subject);
                            databaseReference.child("vendorRatings").child(idTxt).child("reports").child(reportKey).child("body").setValue(body);
                            databaseReference.child("vendorRatings").child(idTxt).child("reports").child(reportKey).child("reporter").setValue(uAuth.getCurrentUser().getEmail());
                            databaseReference.child("vendorRatings").child(idTxt).child("reports").child(reportKey).child("id").setValue(uAuth.getCurrentUser().getUid());
                            Toast.makeText(ReportVendor.this, "Successfully Reported Vendor", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}