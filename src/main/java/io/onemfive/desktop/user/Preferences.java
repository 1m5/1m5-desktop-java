package io.onemfive.desktop.user;

import io.onemfive.desktop.CssTheme;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Preferences {

    public static Locale locale;

    static {
        locale = Locale.getDefault();

        // On some systems there is no country defined, in that case we use en_US
        if (locale.getCountry() == null || locale.getCountry().isEmpty())
            locale = Locale.US;
    }

    public static Boolean useAnimations = true;
    public static Integer cssTheme = CssTheme.CSS_THEME_LIGHT;
    public static Map<String,Boolean> showAgainMap = new HashMap<>();

    public static Boolean showAgain(String key) {
        return !showAgainMap.containsKey(key) || !showAgainMap.get(key);
    }
    public static void showAgain(String key, Boolean show) {
        showAgainMap.put(key, show);
    }

    private Preferences() {}

}
