package com.jiny.liftlink.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jiny.liftlink.R;
import com.jiny.liftlink.common.utils.ThemeManager;

public class ThemeSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selection);

        findViewById(R.id.btnSpringWarm).setOnClickListener(v -> changeTheme(R.style.Theme_LiftLink_SpringWarm));
        findViewById(R.id.btnSummerCool).setOnClickListener(v -> changeTheme(R.style.Theme_LiftLink_SummerCool));
        findViewById(R.id.btnAutumnWarm).setOnClickListener(v -> changeTheme(R.style.Theme_LiftLink_AutumnWarm));
        findViewById(R.id.btnWinterCool).setOnClickListener(v -> changeTheme(R.style.Theme_LiftLink_WinterCool));
        findViewById(R.id.btnDefault).setOnClickListener(v -> changeTheme(R.style.Theme_LiftLink));
    }

    private void changeTheme(int themeResId) {
        // 테마 저장
        ThemeManager.setTheme(this, themeResId);

        // 결과 반환 후 MainActivity로 돌아가기
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_theme", themeResId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

