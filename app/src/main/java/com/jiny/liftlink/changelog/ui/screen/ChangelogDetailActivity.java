package com.jiny.liftlink.changelog.ui.screen;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiny.liftlink.R;
import com.jiny.liftlink.changelog.data.model.Changelog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChangelogDetailActivity extends AppCompatActivity {

    private TextView textTitle, textDescription, textAuthor, textTag, textDate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog_detail);

        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textAuthor = findViewById(R.id.textAuthor);
        textTag = findViewById(R.id.textTag);
        textDate = findViewById(R.id.textDate);

        db = FirebaseFirestore.getInstance();

        String changelogId = getIntent().getStringExtra("changelog_id");
        if (changelogId != null) {
            loadChangelogDetail(changelogId);
        } else {
            Toast.makeText(this, "changelog ID가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadChangelogDetail(String id) {
        db.collection("changelogs").document(id).get()
                .addOnSuccessListener(this::bindData)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void bindData(DocumentSnapshot doc) {
        Changelog changelog = doc.toObject(Changelog.class);
        if (changelog == null) {
            Toast.makeText(this, "changelog 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textTitle.setText(changelog.getTitle());
        textDescription.setText(changelog.getDescription());
        textAuthor.setText("작성자: " + changelog.getAuthor());
        textTag.setText("#" + changelog.getTag());

        String formattedDate = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                .format(new Date(changelog.getTimestamp()));
        textDate.setText(formattedDate);
    }
}
