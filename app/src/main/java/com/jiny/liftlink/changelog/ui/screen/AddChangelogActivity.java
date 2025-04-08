package com.jiny.liftlink.changelog.ui.screen;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jiny.liftlink.R;
import com.jiny.liftlink.changelog.data.model.Changelog;

import java.util.HashMap;
import java.util.Map;

public class AddChangelogActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription, edtAuthor, edtTag;
    private Button btnSave;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_changelog);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtTag = findViewById(R.id.edtTag);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(view -> saveChangelog());
    }

    private void saveChangelog() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String tag = edtTag.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(author) || TextUtils.isEmpty(tag)) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> changelog = new HashMap<>();
        changelog.put("title", title);
        changelog.put("description", description);
        changelog.put("author", author);
        changelog.put("tag", tag);
        changelog.put("timestamp", System.currentTimeMillis());

        db.collection("changelogs")
                .add(changelog)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "등록 완료!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "등록 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
