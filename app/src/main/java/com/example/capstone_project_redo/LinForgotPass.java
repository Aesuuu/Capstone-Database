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
import android.widget.Toast;

import com.example.capstone_project_redo.databinding.ActivityLinForgotPassBinding;
import com.example.capstone_project_redo.nav.HomePage;
import com.example.capstone_project_redo.nav.MyAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LinForgotPass extends DrawerBaseActivity {

    FirebaseAuth uAuth;

    ActivityLinForgotPassBinding forgotPassBinding;
    ProgressDialog progressDialog;

    EditText etUserEmail;
    Button sendReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lin_forgot_pass);

        uAuth = FirebaseAuth.getInstance();

        etUserEmail = findViewById(R.id.et_fpEmail);

        sendReset = findViewById(R.id.btn_fpSend);
        sendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(LinForgotPass.this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                String userEmail = etUserEmail.getText().toString();
                if (userEmail.equals("")) {
                    Toast.makeText(LinForgotPass.this, "Enter your email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    uAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LinForgotPass.this, "Check your email to reset your password.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                            else {
                                Toast.makeText(LinForgotPass.this, "Try again! Something wrong happened.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(new Intent(LinForgotPass.this, LoginActivity.class)));
        finish();
    }
}