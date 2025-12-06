package com.example.tmk.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tmk.database.DBHelper;
import com.example.tmk.R;
import com.example.tmk.database.UserDB;

public class LoginActivity extends AppCompatActivity {

    private UserDB userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDB = new UserDB(this);
        userDB.open();

        EditText emailEditText = findViewById(R.id.email_field);
        EditText passwordEditText = findViewById(R.id.password_field);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);
        TextView forgotPasswordButton = findViewById(R.id.forgot_password_button);

        loginButton.setOnClickListener(v -> login(emailEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim()));

        registerButton.setOnClickListener(v -> showRegistrationDialog());
        forgotPasswordButton.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void login(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = userDB.getUserByEmail(email);
        if (cursor != null) {
            try {
                int passwordColumnIndex = cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD);
                if (passwordColumnIndex != -1) {
                    String storedPassword = cursor.getString(passwordColumnIndex);
                    if (storedPassword.equals(password)) {
                        Log.d("LoginActivity", "Успешный вход: " + email);
                        startActivity(new Intent(this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        Toast.makeText(this, "Вход успешен!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Пользователь с таким e-mail не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRegistrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Регистрация");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 0);

        EditText emailInput = new EditText(this);
        emailInput.setHint("Введите e-mail");
        layout.addView(emailInput);

        EditText passwordInput = new EditText(this);
        passwordInput.setHint("Введите пароль");
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput);

        builder.setView(layout);

        builder.setPositiveButton("Зарегистрироваться", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userDB.isEmailExists(email)) {
                Toast.makeText(this, "Пользователь с таким e-mail уже существует", Toast.LENGTH_SHORT).show();
                return;
            }
            long userId = userDB.addUser(email, password);
            Toast.makeText(this, userId != -1 ? "Регистрация успешна!" : "Ошибка регистрации",
                    Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.forgot_password_dialog, null);
        builder.setView(dialogView);

        EditText emailInput = dialogView.findViewById(R.id.email_input);
        Button sendCodeButton = dialogView.findViewById(R.id.send_code_button);

        AlertDialog dialog = builder.create();

        sendCodeButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Введите e-mail", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!userDB.isEmailExists(email)) {
                Toast.makeText(this, "E-mail не найден", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            showResetPasswordDialog(email);
        });

        dialog.show();
    }
    private void showResetPasswordDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.reset_password_dialog, null);
        builder.setView(dialogView);

        EditText codeInput = dialogView.findViewById(R.id.code_input);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        Button resetPasswordButton = dialogView.findViewById(R.id.reset_password_button);

        AlertDialog dialog = builder.create();

        resetPasswordButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (!"1234".equals(code)) {
                Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "Введите новый пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            userDB.updatePassword(email, newPassword);
            dialog.dismiss();
            Toast.makeText(this, "Пароль успешно изменён", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDB.close();
    }
}
