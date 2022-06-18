package com.example.capstone_project_redo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone_project_redo.nav.Dashboard;
import com.example.capstone_project_redo.nav.HomePage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth uAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    DatabaseReference dataRef = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");

    TextView forgotPassword;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        forgotPassword = findViewById(R.id.tv_forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LinForgotPass.class));
            }
        });

        TextView createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ConsumerRegister.class));
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User was still logged in.");
            builder.setMessage("Press 'Yes' to continue to your account.");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String typeOfUser = Objects.requireNonNull(uAuth.getCurrentUser()).getUid();
                    dataRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (user.isEmailVerified()) {
                                if (snapshot.child("vendor").hasChild(typeOfUser)) {
                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                    finish();
                                }
                                else if (snapshot.child("consumer").hasChild(typeOfUser)) {
                                    startActivity(new Intent(LoginActivity.this, HomePage.class));
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    uAuth.signOut();
                }
            });
            builder.show();
        }else uAuth.signOut();

        final EditText email = findViewById(R.id.login_email);
        final EditText password = findViewById(R.id.login_password);
        final Button loginBtn = findViewById(R.id.btn_regApply);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Logging you in...");
                progressDialog.show();

                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();

                if (emailTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Input both Email and Password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    uAuth.signInWithEmailAndPassword(emailTxt,passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String currentUser = uAuth.getCurrentUser().getUid();
                                        if (uAuth.getCurrentUser().isEmailVerified()) {
                                            databaseReference = database.getReference("users");
                                            databaseReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    //String consumer = database.getReference().child("users").child("consumer").getKey();

                                                    if (snapshot.child("consumer").hasChild(currentUser)){

                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(LoginActivity.this, HomePage.class));

                                                    } else if (snapshot.child("vendor").hasChild(currentUser)) {

                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                                    }
                                                    else {
                                                        progressDialog.dismiss();
                                                        //Toast.makeText(LoginActivity.this, "Please wait 2-3 working days for Admin to enable your account", Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(LoginActivity.this, "Error logging in your account.", Toast.LENGTH_SHORT).show();
                                                        //Toast.makeText(LoginActivity.this, snapshot.child("consumer").child(currentUser).child("typeOfUser").toString(), Toast.LENGTH_SHORT).show();
                                                        uAuth.signOut();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    uAuth.signOut();
                                                }
                                            });
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email Address", Toast.LENGTH_SHORT).show();
                                            uAuth.signOut();
                                        }
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                                        uAuth.signOut();
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        finish();
    }
}