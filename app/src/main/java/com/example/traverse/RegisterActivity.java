package com.example.traverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText addEmail, addPassword, confirmPassword;
    Button btnCreate;
    TextView loginPage;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addEmail = findViewById(R.id.addEmail);
        addPassword = findViewById(R.id.addPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnCreate = findViewById(R.id.btnCreate);
        loginPage = findViewById(R.id.loginPage);

        firebaseAuth = FirebaseAuth.getInstance();

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskAuth();
            }
        });
    }

    private void TaskAuth() {
        String add_user_name = addEmail.getText().toString().trim();
        String add_password = addPassword.getText().toString().trim();
        String confirm_password = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(add_user_name)) {
            Toast.makeText(getApplicationContext(), "Add email", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(add_password)) {
            Toast.makeText(getApplicationContext(), "Add password", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(confirm_password)) {
            Toast.makeText(getApplicationContext(), "Add password again to verify", Toast.LENGTH_LONG).show();
            return;
        }
        else if (!add_password.equals(confirm_password)) {
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            firebaseAuth.createUserWithEmailAndPassword(add_user_name, add_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}