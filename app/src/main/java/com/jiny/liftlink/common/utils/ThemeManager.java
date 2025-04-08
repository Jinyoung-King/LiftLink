package com.jiny.liftlink.common.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jiny.liftlink.R;

public class ThemeManager {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";

    public static void applyTheme(Activity activity) {
        // 저장된 테마 정보 또는 기본 테마를 적용
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int themeId = preferences.getInt("selected_theme", R.style.Theme_LiftLink);
        activity.setTheme(themeId); // 테마 적용
    }

    public static void setTheme(Activity activity, int themeId) {
        // 테마를 저장하고 적용
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        preferences.edit().putInt("selected_theme", themeId).apply();
        activity.setTheme(themeId); // 테마 적용
    }
}
