package org.notleksa.autismcore.rat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Rat {

    private static final char[] ASCII_CHARS = { '@', '#', 'S', '%', '?', '*', '+', ';', ':', ',', '.' };

    public static String convertToAscii(File file, int width) throws IOException {
        BufferedImage image = ImageIO.read(file);

        if (image == null) {
            throw new IOException("Failed to read image: " + file.getAbsolutePath());
        }

        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        int height = (int) ((double) originalHeight / originalWidth * width * 0.5);

        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscaleImage.getGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();

        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = grayscaleImage.getRGB(x, y);
                int gray = color & 0xFF;
                int index = Math.min((gray * (ASCII_CHARS.length - 1)) / 255, ASCII_CHARS.length - 1);
                sb.append(ASCII_CHARS[index]);
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
