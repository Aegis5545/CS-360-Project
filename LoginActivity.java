package com.zybooks.project2cs_360;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonCreateAccount, buttonGuestLogin, buttonSmsPermissions;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views and database helper
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        buttonGuestLogin = findViewById(R.id.guestLogin);
        buttonSmsPermissions = findViewById(R.id.buttonSmsPermissions);
        databaseHelper = new DatabaseHelper(this);

        // Set click listeners
        buttonLogin.setOnClickListener(v -> loginUser());
        buttonCreateAccount.setOnClickListener(v -> registerUser());
        buttonGuestLogin.setOnClickListener(v -> openDataDisplayActivity());
        buttonSmsPermissions.setOnClickListener(v -> openSmsPermissionsActivity());
    }

    private void loginUser() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (databaseHelper.validateUser(username, password)) {
            // Login successful, open DataDisplayActivity
            openDataDisplayActivity();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (databaseHelper.registerUser(username, password)) {
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDataDisplayActivity() {
        Intent intent = new Intent(this, DataDisplayActivity.class);
        startActivity(intent);
    }

    private void openSmsPermissionsActivity() {
        Intent intent = new Intent(this, SmsPermissionsActivity.class);
        startActivity(intent);
    }
}
