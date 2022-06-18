package com.example.capstone_project_redo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConsumerRegister extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;
    ProgressDialog progressDialog;

    Button cancel, register;
    EditText email, password, cPassword, firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_register);

        uAuth = FirebaseAuth.getInstance();
        cancel = findViewById(R.id.btn_cCancel);
        register = findViewById(R.id.btn_cRegister);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConsumerRegister.this, LoginActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(ConsumerRegister.this);
                progressDialog.setMessage("Creating your account...");
                progressDialog.show();

                email = findViewById(R.id.et_cEmail);
                password = findViewById(R.id.et_cPassword);
                cPassword = findViewById(R.id.et_cConfirmPassword);
                firstName = findViewById(R.id.et_cFirstName);
                lastName = findViewById(R.id.et_cLastName);

                String emailTxt = email.getText().toString();
                String passwordTxt = password.getText().toString();
                String cPasswordTxt = cPassword.getText().toString();
                String firstNameTxt = firstName.getText().toString();
                String lastNameTxt = lastName.getText().toString();

                if (emailTxt.equals("") || passwordTxt.equals("") || cPasswordTxt.equals("") || firstNameTxt.equals("") || lastNameTxt.equals("")) {
                    Toast.makeText(ConsumerRegister.this, "Fill all fields.", Toast.LENGTH_SHORT).show();
                    uAuth.signOut();
                }
                else {
                    if (passwordTxt.length() < 6) {
                        Toast.makeText(ConsumerRegister.this, "Password too short", Toast.LENGTH_SHORT).show();
                        uAuth.signOut();
                    }
                    else {
                        if (!passwordTxt.equals(cPasswordTxt)) {
                            Toast.makeText(ConsumerRegister.this, "Password does not match", Toast.LENGTH_SHORT).show();
                            uAuth.signOut();
                        }
                        else {
                            register(emailTxt, passwordTxt, firstNameTxt, lastNameTxt);
                            uAuth.signOut();
                            progressDialog.dismiss();
                            startActivity(new Intent(ConsumerRegister.this, LoginActivity.class));
                            finish();
                        }
                    }
                }
            }
        });
    }
    public void register(String email, String pass, String first, String last) {

        uAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {{
                            uAuth.signInWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                        }
                                    });

                            uAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String user = uAuth.getCurrentUser().getUid();
                                            databaseReference.child("users").child("consumer").child(user).child("id").setValue(user);
                                            databaseReference.child("users").child("consumer").child(user).child("FirstName").setValue(first);
                                            databaseReference.child("users").child("consumer").child(user).child("LastName").setValue(last);
                                            databaseReference.child("users").child("consumer").child(user).child("EmailAddress").setValue(email);
                                            databaseReference.child("users").child("consumer").child(user).child("Password").setValue(pass);
                                            databaseReference.child("users").child("consumer").child(user).child("ImageProfile").setValue("https://firebasestorage.googleapis.com/v0/b/loginregister-f1e0d.appspot.com/o/categoryImages%2Fuser_sample.png?alt=media&token=1e2439b9-ac42-4b67-aa95-73ce677c7624");

                                            Toast.makeText(ConsumerRegister.this, "Verification mail has been sent to your email.", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(ConsumerRegister.this, "Verify your Email to complete registration", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }}
                        else {
                            uAuth.signOut();
                            Toast.makeText(ConsumerRegister.this, "Invalid Email or Email has already been taken", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}