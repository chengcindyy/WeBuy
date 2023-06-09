package com.csis4495_cmk.webuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editEmail;
    private Button btnReset, btnCancel;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editEmail = findViewById(R.id.edit_email);

        btnReset = findViewById(R.id.btn_send);
        btnReset.setOnClickListener(view -> {

            String email = editEmail.getText().toString();

            if (TextUtils.isEmpty(email)){
                Toast.makeText(ForgotPasswordActivity.this,
                        "Please enter your registered email.", Toast.LENGTH_SHORT).show();
                editEmail.setError("Email is required.");
                editEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(ForgotPasswordActivity.this,
                        "Please enter your valid email.", Toast.LENGTH_SHORT).show();
                editEmail.setError("Valid email is required.");
                editEmail.requestFocus();
            } else {
                ResetPassword(email);
            }

        });

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(view -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
        });
    }

    private void ResetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);

                    // Clear stack to prevent user coming back to ForgotPasswordActivity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}