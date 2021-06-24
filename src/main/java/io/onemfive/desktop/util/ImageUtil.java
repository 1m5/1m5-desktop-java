package io.onemfive.desktop.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ra.util.FileUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static Image getQRCode(String input, int width, int height, String saveToPath) {
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(new String(input.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException writerException) {
            LOG.warning(writerException.getLocalizedMessage());
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
        } catch (IOException ioException) {
            LOG.warning(ioException.getMessage());
            return null;
        }
        byte[] qrCode = baos.toByteArray();
        Image image = new Image(new ByteArrayInputStream(qrCode));
        if(saveToPath!=null) {
            FileUtil.writeFile(qrCode, saveToPath);
        }
        return image;
    }
}
