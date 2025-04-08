package com.jiny.liftlink.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.jiny.liftlink.R;
import com.jiny.liftlink.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private AutoCompleteTextView email;
    private static final String[] EMAIL_DOMAINS = {
            "@gmail.com", "@naver.com", "@daum.net", "@yahoo.com", "@hotmail.com"
    };
    private EditText password;
    private Button btnLogin, btnRegister;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // 🔥 이미 로그인한 경우 바로 MainActivity 이동
            startActivity(new Intent(this, MainActivity.class));
            finish();  // 로그인 화면 종료
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // XML 파일 연결


        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);  // 기존 EditText → AutoCompleteTextView 사용
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // 회원가입 버튼 클릭 리스너
        btnRegister.setOnClickListener(v -> {
            // 회원가입 화면으로 이동
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener(v -> loginUser());

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();

                // '@'를 포함한 경우 자동완성 리스트 업데이트
                if (input.contains("@")) {
                    int atIndex = input.indexOf("@");
                    String prefix = input.substring(0, atIndex); // '@' 앞부분(ID)

                    List<String> suggestions = new ArrayList<>();
                    for (String domain : EMAIL_DOMAINS) {
                        suggestions.add(prefix + domain);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(email.getContext(),
                            android.R.layout.simple_dropdown_item_1line, suggestions);
                    email.setAdapter(adapter);
                    email.showDropDown(); // 드롭다운 표시
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private void loginUser() {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "로그인 성공: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        // 로그인 성공 시 MainActivity 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // 로그인 화면 종료
                    } else {

                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.e("Login", "잘못된 이메일 또는 비밀번호");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            Log.e("Login", "등록되지 않은 사용자");
                        } else if (e instanceof FirebaseNetworkException) {
                            Log.e("Login", "네트워크 오류 발생");
                        } else {
                            Log.e("Login", "오류 발생: " + e.getMessage());
                        }

                        Toast.makeText(this, "로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

