package com.example.capstone_project_redo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstone_project_redo.databinding.ActivityChangePasswordBinding;
import com.example.capstone_project_redo.databinding.ActivityMyAccountBinding;
import com.example.capstone_project_redo.nav.MyAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends DrawerBaseActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://loginregister-f1e0d-default-rtdb.firebaseio.com");
    FirebaseAuth uAuth;
    ProgressDialog progressDialog;

    EditText currentPass, newPass;
    Button change;
    String currentUser, currentPasstxt, newPasstxt;

    ActivityChangePasswordBinding changePasswordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePasswordBinding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(changePasswordBinding.getRoot());
        allocateActivityTitle("Change Password");

        uAuth = FirebaseAuth.getInstance();
        currentUser = uAuth.getCurrentUser().getUid();

        currentPass = findViewById(R.id.et_currentPass);
        newPass = findViewById(R.id.et_newPass);

        change = findViewById(R.id.btn_changePass);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(ChangePassword.this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                currentPasstxt = currentPass.getText().toString();
                newPasstxt = newPass.getText().toString();
                if (currentPasstxt.equals("") || newPasstxt.equals("")) {
                    Toast.makeText(ChangePassword.this, "Enter both current and new password.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    if (newPasstxt.length() < 6) {
                        Toast.makeText(ChangePassword.this, "New Password is too short", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else {
                        databaseReference.child("users").child("vendor").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userPassword = String.valueOf(snapshot.child("Password").getValue());
                                if (currentPasstxt.equals(userPassword)) {
                                    changePassword(currentPasstxt, newPasstxt);
                                    databaseReference.child("users").child("vendor").child(currentUser).child("Password").setValue(newPasstxt);
                                    startActivity(new Intent(ChangePassword.this, MyAccount.class));
                                    Toast.makeText(ChangePassword.this, "Successfully changed your password", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else {
                                    Toast.makeText(ChangePassword.this, "Your current password does not match our records", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
        });

    }

    private void changePassword(String userPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();


        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        assert userEmail != null;
        AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail, userPassword);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChangePassword.this, "Error, password not changed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChangePassword.this, "Error auth failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChangePassword.this, MyAccount.class));
        finish();
    }
}