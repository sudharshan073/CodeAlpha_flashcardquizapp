
package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthHelper.isLoggedIn(this)) {
            openDashboard();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.etUsername);
        EditText password = findViewById(R.id.etPassword);
        Button login = findViewById(R.id.btnLogin);
        TextView register = findViewById(R.id.tvGoRegister);

        login.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,
                        "Enter username and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (AuthHelper.login(this, user, pass)) {
                Toast.makeText(this, "Login successful!",
                        Toast.LENGTH_SHORT).show();
                openDashboard();
            } else {
                Toast.makeText(this,
                        "Invalid username or password. Register first if you are new.",
                        Toast.LENGTH_LONG).show();
            }
        });

        register.setOnClickListener(v ->
                startActivity(new Intent(
                        this, RegisterActivity.class)));
    }

    private void openDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}