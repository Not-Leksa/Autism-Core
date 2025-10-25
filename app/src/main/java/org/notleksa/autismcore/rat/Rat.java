package org.notleksa.autismcore.rat;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Rat {

    private static final String ASCII_CHARS =
            "@#W$9876543210?!abc;:+=-,._";

    public static String convertToAscii(File file, int width) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("Failed to read image: " + file.getAbsolutePath());
        }

        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        int height = (int) ((double) originalHeight / originalWidth * width * 0.5);

        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffered.getGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();

        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = buffered.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int gC = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                double luminance = (0.2126 * r + 0.7152 * gC + 0.0722 * b);
                int index = (int) (luminance / 255.0 * (ASCII_CHARS.length() - 1));
                char ascii = ASCII_CHARS.charAt(index);

                // Use ANSI 24-bit color
                sb.append(String.format("\u001B[38;2;%d;%d;%dm%c", r, gC, b, ascii));
            }
            sb.append("\u001B[0m\n");
        }

        return sb.toString();
    }
}
