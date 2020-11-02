package io.onemfive.desktop.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

public class ImageUtil {
    private static final Logger LOG = Logger.getLogger(ImageUtil.class.getName());

    public static final String REMOVE_ICON = "image-remove";

    public static ImageView getImageViewById(String id) {
        ImageView imageView = new ImageView();
        imageView.setId(id);
        return imageView;
    }

    public static Image getApplicationIconImage () {
        return getImageByUrl("/io/onemfive/desktop/images/icons/black/favicon-96x96.png");
    }

    private static Image getImageByUrl(String url) {
        return new Image(ImageUtil.class.getResourceAsStream(url));
    }

    private static ImageView getImageViewByUrl(String url) {
        return new ImageView(getImageByUrl(url));
    }

    public static boolean isRetina() {
        final GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final AffineTransform transform = gfxConfig.getDefaultTransform();
        return !transform.isIdentity();
    }
}
