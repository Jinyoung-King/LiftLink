package com.jiny.liftlink.auth.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiny.liftlink.R;
import com.jiny.liftlink.auth.data.User;

import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    private static final String LOG_TAG = "SignUpActivity";

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextName, editTextPhone, editTextAddress;
    private Button btnSignUp;
    private ImageView imgProfile;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // 사용자 입력 필드 초기화
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        imgProfile = findViewById(R.id.imgProfile);
        btnSignUp = findViewById(R.id.btnSignUp);

        // 프로필 이미지 선택
        imgProfile.setOnClickListener(v -> openImagePicker());

        btnSignUp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            signUpUser(email, password, name, phone, address);
        });
    }

    private void openImagePicker() {
        // 이미지 선택 인텐트 실행
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.getData();
            imgProfile.setImageURI(profileImageUri);  // 선택된 이미지를 ImageView에 표시
        }
    }

    private void signUpUser(String email, String password, String name, String phone, String address) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Log.d(LOG_TAG, "Task: " + task);
                    if (task.isSuccessful()) {
                        // 회원가입 성공 후 Firestore에 사용자 정보 저장
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(LOG_TAG, "Firebase User: " + user);
                        if (user != null) {
                            String userId = user.getUid();
                            if (profileImageUri != null) {
                                uploadProfileImage(userId, name, phone, address);
                            } else {
                                saveUserInfoToFirestore(userId, name, phone, address, null);
                            }
                        }

                        // 이메일 인증 요청 -> 일단 셍략
//                        user.sendEmailVerification()
//                                .addOnCompleteListener(task1 -> {
//                                    if (task1.isSuccessful()) {
//                                        Toast.makeText(SignUpActivity.this, "이메일 인증을 확인하세요.", Toast.LENGTH_SHORT).show();
//                                    }
//                                });

                        // 로그인 화면으로 이동
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

                        finish();
                    } else {
                        // 실패
                        Toast.makeText(SignUpActivity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadProfileImage(String userId, String name, String phone, String address) {
        // Firebase Storage에 업로드
        StorageReference storageRef = storage.getReference().child("profile_images").child(UUID.randomUUID().toString());
        UploadTask uploadTask = storageRef.putFile(profileImageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // 프로필 이미지 URL을 Firestore에 저장
                saveUserInfoToFirestore(userId, name, phone, address, uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(SignUpActivity.this, "프로필 이미지 업로드 실패", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserInfoToFirestore(String userId, String name, String phone, String address, String profileImageUrl) {
        User user = new User(name, editTextEmail.getText().toString().trim(), phone, address, profileImageUrl);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(SignUpActivity.this, "사용자 정보 저장 완료", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "사용자 정보 저장 실패", Toast.LENGTH_SHORT).show());
    }
}

