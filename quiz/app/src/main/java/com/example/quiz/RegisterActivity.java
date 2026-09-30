
package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText name = findViewById(R.id.etFullName);
        EditText username = findViewById(R.id.etNewUsername);
        EditText password = findViewById(R.id.etNewPassword);
        EditText confirm = findViewById(R.id.etConfirmPassword);
        Button register = findViewById(R.id.btnRegister);
        TextView goLogin = findViewById(R.id.tvGoLogin);

        register.setOnClickListener(v -> {
            String fullName = name.getText().toString().trim();
            String user = username.getText().toString().trim();
            String pass = password.getText().toString();
            String confirmPass = confirm.getText().toString();

            if (fullName.isEmpty() || user.isEmpty()
                    || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Fill in all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (user.contains(" ")) {
                Toast.makeText(this,
                        "Username cannot contain spaces",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass.length() < 6) {
                Toast.makeText(this,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = AuthHelper.register(
                    this, fullName, user, pass);

            if (success) {
                Toast.makeText(this,
                        "Registration successful! Please log in.",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(
                        this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this,
                        "Username already exists. Choose another.",
                        Toast.LENGTH_LONG).show();
            }
        });

        goLogin.setOnClickListener(v -> finish());
    }
}