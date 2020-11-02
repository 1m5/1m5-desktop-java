package io.onemfive.desktop;

import javafx.scene.Scene;

public class CssTheme {

    public static final int CSS_THEME_LIGHT = 0;
    public static final int CSS_THEME_DARK = 1;

    private static String ONEMFIVE = CssTheme.class.getResource("1m5.css").toExternalForm();
    private static String IMAGES = CssTheme.class.getResource("images.css").toExternalForm();
    private static String LIGHT_THEME = CssTheme.class.getResource("theme-light.css").toExternalForm();
    private static String DARK_THEME = CssTheme.class.getResource("theme-dark.css").toExternalForm();

    private static Boolean initialized = false;

    public static void loadSceneStyles(Scene scene, int cssTheme) {

        if(!initialized) {
            scene.getStylesheets().add(ONEMFIVE);
            scene.getStylesheets().add(IMAGES);
            initialized = true;
        }

        switch (cssTheme) {

            case CSS_THEME_DARK: {
                scene.getStylesheets().remove(LIGHT_THEME);
                scene.getStylesheets().add(DARK_THEME);
                break;
            }

            case CSS_THEME_LIGHT: {
                scene.getStylesheets().remove(DARK_THEME);
                scene.getStylesheets().add(LIGHT_THEME);
                break;
            }
        }

//        List<String> families = javafx.scene.text.Font.getFamilies();
//        for(String f : families){
//            System.out.println(f);
//        }
    }
}
