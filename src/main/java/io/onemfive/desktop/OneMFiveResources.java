package io.onemfive.desktop;

import onemfive.ManCon;
import ra.util.Resources;

import java.net.URL;

public class OneMFiveResources extends Resources {

    // Web
    public static final URL IMS_WEB_INDEX = OneMFiveResources.class.getResource("/web/1m5/index.html");

    // ManCon Icons
    public static final URL ICON_BLUE = OneMFiveResources.class.getResource("/io/onemfive/desktop/images/icons/icon_blue.png");
    public static final URL ICON_GRAY = OneMFiveResources.class.getResource("/io/onemfive/desktop/images/icons/icon_gray.png");
    public static final URL ICON_GREEN = OneMFiveResources.class.getResource("/io/onemfive/desktop/images/icons/icon_green.png");
    public static final URL ICON_ORANGE = OneMFiveResources.class.getResource("/io/onemfive/desktop/images/icons/icon_orange.png");
    public static final URL ICON_RED = OneMFiveResources.class.getResource("/io/onemfive/desktop/images/icons/icon_red.png");
    public static final URL ICON_WHITE = OneMFiveResources.class.getResource("/io/onemfive/desktop/images/icons/icon_white.png");
    public static final URL ICON_YELLOW = OneMFiveResources.class.getResource("/io/onemfive/desktop/images/icons/icon_yellow.png");

    public static URL getManConIcon(ManCon manCon) {
        switch (manCon) {
            case LOW: return ICON_GREEN;
            case MEDIUM: return ICON_BLUE;
            case HIGH: return ICON_YELLOW;
            case VERYHIGH: return ICON_ORANGE;
            case EXTREME: return ICON_RED;
            case NEO: return ICON_GRAY;
            default: return ICON_WHITE;
        }
    }

    public static URL getManConIcon(Integer index) {
        switch (index) {
            case 0: return ICON_GRAY;
            case 1: return ICON_RED;
            case 2: return ICON_ORANGE;
            case 3: return ICON_YELLOW;
            case 4: return ICON_BLUE;
            case 5: return ICON_GREEN;
            default: return ICON_WHITE;
        }
    }

}
