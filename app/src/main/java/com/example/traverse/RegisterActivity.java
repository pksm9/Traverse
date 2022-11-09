package com.example.traverse;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText addEmail, addUserName, addPassword, confirmPassword;
    Button btnCreate;
    TextView loginPage;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addEmail = findViewById(R.id.addEmail);
        addUserName = findViewById(R.id.userName);
        addPassword = findViewById(R.id.addPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnCreate = findViewById(R.id.btnCreate);
        loginPage = findViewById(R.id.loginPage);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


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
        String email = addEmail.getText().toString().trim();
        String userName = addUserName.getText().toString().trim();
        String password = addPassword.getText().toString().trim();
        String confirm_password = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Add an Email", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getApplicationContext(), "Add a User Name", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Add password", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(confirm_password)) {
            Toast.makeText(getApplicationContext(), "Add password again to verify", Toast.LENGTH_LONG).show();
            return;
        }
        else if (!password.equals(confirm_password)) {
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                        saveToFireStore(userName);

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

    private void saveToFireStore(String userName) {
        uid = firebaseAuth.getCurrentUser().getUid();
        if (!userName.isEmpty()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("user", userName);

            db.collection("users").document(uid).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Failed to make user", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}