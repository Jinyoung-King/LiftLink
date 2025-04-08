package com.jiny.liftlink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jiny.liftlink.chat.ui.ChatFragment;
import com.jiny.liftlink.common.utils.ThemeManager;
import com.jiny.liftlink.home.ui.HomeFragment;
import com.jiny.liftlink.inquiry.ui.InquiryListFragment;
import com.jiny.liftlink.profile.ui.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private static final int THEME_SELECTION_REQUEST_CODE = 1;
    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 테마 먼저 적용
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 📌 마지막 프래그먼트 복원
        SharedPreferences prefs = getSharedPreferences("liftlink", Context.MODE_PRIVATE);
        String lastFragment = prefs.getString("lastFragment", "HomeFragment");

        Fragment initialFragment;
        switch (lastFragment) {
            case "ProfileFragment":
                initialFragment = new ProfileFragment();
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                break;
            case "InquiryListFragment":
                initialFragment = new InquiryListFragment();
                bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
                break;
            case "ChatFragment":
                initialFragment = new ChatFragment();
                bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
                break;
            default: // 기본값
                initialFragment = new HomeFragment();
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
                break;
        }

        // 초기 프래그먼트 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.container, initialFragment).commit();

        // once used, remove the stored fragment
        prefs.edit().remove("lastFragment").apply();

        // 하단바 클릭 처리
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String fragmentName = "HomeFragment";

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_reservation) {
                selectedFragment = new InquiryListFragment();
                fragmentName = "InquiryListFragment";
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                fragmentName = "ProfileFragment";
            } else if (item.getItemId() == R.id.navigation_chat) {
                selectedFragment = new ChatFragment();
                fragmentName = "ChatFragment";
            }

            if (selectedFragment != null) {
                // 저장 (혹시 모를 향후 recreate에 대비)
                prefs.edit().putString("lastFragment", fragmentName).apply();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == THEME_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            int selectedTheme = data.getIntExtra("selected_theme", R.style.Theme_LiftLink);
            ThemeManager.setTheme(this, selectedTheme);
            recreate(); // 재시작하면서 onCreate에서 lastFragment로 복원
        }
    }

    private int getStatusBarHeight(View view) {
        Log.d(LOG_TAG, "빌드 버전: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets windowInsets = view.getRootWindowInsets();
            if (windowInsets != null) {
                return windowInsets.getInsets(WindowInsets.Type.statusBars()).top;
            }
        } else {
            @SuppressLint({"DiscouragedApi", "InternalInsetResource"}) int resourceId = view.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return view.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }
}
