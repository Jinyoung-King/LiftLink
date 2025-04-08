package com.jiny.liftlink.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jiny.liftlink.R;

public class ThemeUtils {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_THEME = "selected_theme";

    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String themeName = prefs.getString(KEY_THEME, "Theme.LiftLink"); // 기본값 설정
        context.setTheme(getThemeId(themeName));
    }

    private static int getThemeId(String themeName) {
        return switch (themeName) {
            case "Theme.LiftLink.SpringWarm" -> R.style.Theme_LiftLink_SpringWarm;
            case "Theme.LiftLink.SummerCool" -> R.style.Theme_LiftLink_SummerCool;
            case "Theme.LiftLink.AutumnWarm" -> R.style.Theme_LiftLink_AutumnWarm;
            case "Theme.LiftLink.WinterCool" -> R.style.Theme_LiftLink_WinterCool;
            default -> R.style.Theme_LiftLink;
        };
    }
}
