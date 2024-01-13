import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ImageEditorPanel extends JPanel implements KeyListener {

    Color[][] pixels;
    Color[][][] history;

    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            imageIn = ImageIO.read(new File("cow.jpg"));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length + 150, pixels.length));
        setBackground(Color.WHITE);
        addKeyListener(this);
        history = new Color[10][pixels.length][pixels[0].length];
    }

    public void paintComponent(Graphics g) {
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 8));
        g.drawString("(h) - Flip horizontal", pixels[0].length + 5, 10);
        g.drawString("(v) - Flip vertical", pixels[0].length + 5, 20);
        g.drawString("(g) - Greyscale", pixels[0].length + 5, 30);
        g.drawString("(s) - Sepia", pixels[0].length + 5, 40);
        g.drawString("(p) - Posterize", pixels[0].length + 5, 50);
        g.drawString("(i) - Brighten", pixels[0].length + 5, 60);
        g.drawString("(o) - Darken", pixels[0].length + 5, 70);
        g.drawString("(j) - More contrast", pixels[0].length + 5, 80);
        g.drawString("(k) - Less contrast", pixels[0].length + 5, 90);
        g.drawString("(b) - Blur", pixels[0].length + 5, 100);
        g.drawString("(u) - Undo", pixels[0].length + 5, 110);
        g.drawString("(q) - Save Picture", pixels[0].length + 5, 120);
    }

    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        return result;
    }

    public static Color[][] flipHorizontal(Color[][] input) {
        Color[][] flipHori = new Color[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                flipHori[r][c] = input[r][input[0].length - c - 1];
            }
        }
        return flipHori;
    }

    public static Color[][] flipVertical(Color[][] input) {
        Color[][] flipVert = new Color[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                flipVert[i][j] = input[input.length - i - 1][j];
            }
        }
        return flipVert;

    }

    public static Color[][] greyscale(Color[][] input) {
        Color[][] result = new Color[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                Color pixel = input[r][c];
                int average = (pixel.getRed() + pixel.getBlue() + pixel.getGreen()) / 3;
                result[r][c] = new Color(average, average, average);
            }
        }
        return result;
    }

    public static Color[][] sepia(Color[][] input) {
        Color[][] result = new Color[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                Color pixel = input[r][c];
                int newRed = (int) (0.393 * pixel.getRed() + 0.769 * pixel.getGreen() + 0.189 * pixel.getBlue());
                int newGreen = (int) (0.349 * pixel.getRed() + 0.686 * pixel.getGreen() + 0.168 * pixel.getBlue());
                int newBlue = (int) (0.272 * pixel.getRed() + 0.534 * pixel.getGreen() + 0.131 * pixel.getBlue());
                if (newRed > 255) {
                    newRed = 255;
                }
                if (newBlue > 255) {
                    newBlue = 255;
                }
                if (newGreen > 255) {
                    newGreen = 255;
                }
                result[r][c] = new Color(newRed, newGreen, newBlue);
            }
        }
        return result;
    }

    public static Color[][] posterize(Color[][] input) {
        Color[][] postered = new Color[input.length][input[0].length];
        Color poster1 = new Color(0, 64, 255);
        Color poster2 = new Color(1, 37, 143);
        Color poster3 = new Color(131, 162, 252);
        Color poster4 = new Color(235, 231, 167);
        for (int r = 0; r < postered.length; r++) {
            for (int c = 0; c < postered[0].length; c++) {
                Color pixel = input[r][c];
                int red = pixel.getRed();
                int green = pixel.getGreen();
                int blue = pixel.getBlue();
                double distance1 = Math.sqrt(Math.pow((poster1.getRed() - red), 2)
                        + Math.pow((poster1.getGreen() - green), 2) + Math.pow((poster1.getBlue() - blue), 2));
                double distance2 = Math.sqrt(Math.pow((poster2.getRed() - red), 2)
                        + Math.pow((poster2.getGreen() - green), 2) + Math.pow((poster2.getBlue() - blue), 2));
                double distance3 = Math.sqrt(Math.pow((poster3.getRed() - red), 2)
                        + Math.pow((poster3.getGreen() - green), 2) + Math.pow((poster3.getBlue() - blue), 2));
                double distance4 = Math.sqrt(Math.pow((poster4.getRed() - red), 2)
                        + Math.pow((poster4.getGreen() - green), 2) + Math.pow((poster4.getBlue() - blue), 2));
                double[] list = { distance1, distance2, distance3, distance4 };
                double max = list[0];
                for (int k = 0; k < list.length; k++) {
                    if (list[k] > max)
                        max = list[k];
                }
                if (max == distance1)
                    postered[r][c] = poster1;
                if (max == distance2)
                    postered[r][c] = poster2;
                if (max == distance3)
                    postered[r][c] = poster3;
                if (max == distance4)
                    postered[r][c] = poster4;

            }
        }
        return postered;
    }

    public static Color[][] brighten(Color[][] input) {
        Color[][] brightened = new Color[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                int brightRed = input[r][c].getRed() + 10;
                int brightGreen = input[r][c].getGreen() + 10;
                int brightBlue = input[r][c].getBlue() + 10;
                if (brightRed > 255){
                    brightRed = 255;
                }
                if (brightGreen > 255){
                    brightGreen = 255;
                }
                if (brightBlue > 255){
                    brightBlue = 255;
                }
                Color brightPixel = new Color(brightRed, brightGreen, brightBlue);
                brightened[r][c] = brightPixel;
            }
        }
        return brightened;
    }

    public static Color[][] darken(Color[][] input) {
        Color[][] darkened = new Color[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                int darkRed = input[r][c].getRed() - 10;
                int darkGreen = input[r][c].getGreen() - 10;
                int darkBlue = input[r][c].getBlue() - 10;
                if (darkRed < 0){
                    darkRed = 0;
                }
                if (darkGreen < 0){
                    darkGreen = 0;
                }
                if (darkBlue < 0){
                    darkBlue = 0;
                }
                Color darkPixel = new Color(darkRed, darkGreen, darkBlue);
                darkened[r][c] = darkPixel;
            }
        }
        return darkened;
    }

    public static Color[][] contrastUp(Color[][] input) {
        Color[][] moreContrast = new Color[input.length][input[0].length];
        int contrastRed = 0;
        int contrastGreen = 0;
        int contrastBlue = 0;
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                if (input[r][c].getRed() > 128) {
                    contrastRed = (int) (input[r][c].getRed() * 1.1);
                }
                if (input[r][c].getGreen() > 128) {
                    contrastGreen = (int) (input[r][c].getGreen() * 1.1);
                }
                if (input[r][c].getBlue() > 128) {
                    contrastBlue = (int) (input[r][c].getBlue() * 1.1);
                }
                if (input[r][c].getRed() < 128) {
                    contrastRed = (int) (input[r][c].getRed() * 0.9);
                }
                if (input[r][c].getGreen() < 128) {
                    contrastGreen = (int) (input[r][c].getGreen() * 0.9);
                }
                if (input[r][c].getBlue() < 128) {
                    contrastBlue = (int) (input[r][c].getBlue() * 0.9);
                }
                if (contrastRed > 255){
                    contrastRed = 255;
                }
                if (contrastGreen > 255){
                    contrastGreen = 255;
                }
                if (contrastBlue > 255){
                    contrastBlue = 255;
                }
                Color contrastPixel = new Color(contrastRed, contrastGreen, contrastBlue);
                moreContrast[r][c] = contrastPixel;
            }
        }
        return moreContrast;
    }

    public static Color[][] contrastDown(Color[][] input) {
        // move closer to 128
        Color[][] lessContrast = new Color[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                int lContRed = 0;
                int lContGreen = 0;
                int lContBlue = 0;
                if (input[r][c].getRed() < 128) {
                    lContRed = (int) (input[r][c].getRed() * 1.1);
                }
                if (input[r][c].getGreen() < 128) {
                    lContGreen = (int) (input[r][c].getGreen() * 1.1);
                }
                if (input[r][c].getBlue() < 128) {
                    lContBlue = (int) (input[r][c].getBlue() * 1.1);
                }
                if (input[r][c].getRed() > 128) {
                    lContRed = (int) (input[r][c].getRed() * 0.9);
                }
                if (input[r][c].getGreen() > 128) {
                    lContGreen = (int) (input[r][c].getGreen() * 0.9);
                }
                if (input[r][c].getBlue() > 128) {
                    lContBlue = (int) (input[r][c].getBlue() * 0.9);
                }
                Color lContPixel = new Color(lContRed, lContGreen, lContBlue);
                lessContrast[r][c] = lContPixel;

            }
        }
        return lessContrast;
    }

    public static Color[][] blur(Color[][] input) {
        Color[][] blurry = new Color[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[0].length; c++) {
                int tempRed = 0;
                int tempGreen = 0;
                int tempBlue = 0;
                int numPixels = 0;
                for (int k = -3; k < 3; k++) {
                    for (int m = -3; m < 3; m++) {
                        int x = r + k;
                        int y = c + m;
                        if (x >= 0 && y >= 0 && x < input.length && y < input[0].length) {
                            numPixels++;
                            tempRed += (input[r + k][c + m].getRed());
                            tempGreen += (input[r + k][c + m].getGreen());
                            tempBlue += (input[r + k][c + m].getBlue());
                        }
                    }
                }
                int avgRed = tempRed / numPixels;
                int avgGreen = tempGreen / numPixels;
                int avgBlue = tempBlue / numPixels;
                Color blurredPixel = new Color(avgRed, avgGreen, avgBlue);
                blurry[r][c] = blurredPixel;
            }
        }
        return blurry;
    }

    // implement a save function that uses the java files dialog box and lets you
    // choose a file name.
    public void saveImage() {
        BufferedImage savedPic = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                savedPic.setRGB(r, c, pixels[r][c].getRGB());

            }
        }
        try {
            ImageIO.write(savedPic, "jpg", new File("cowPic.jpg"));
            System.out.println("Image saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving image: " + e.getMessage());
        }

    }

    public void undo() {
        pixels = new Color[history[0].length][history[0][0].length];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                pixels[i][j] = history[9][i][j];
            }
        }
        for (int k = 9; k > 0; k--) {
            history[k] = history[k - 1];
        }
    }

    public void shiftStorage(Color[][] newPic) {
        Color[][] copiedPic = new Color[newPic.length][newPic[0].length];
        for (int i = 0; i < newPic.length; i++) {
            for (int j = 0; j < newPic[0].length; j++) {
                copiedPic[i][j] = newPic[i][j];
            }
        }
        for (int k = 1; k < 10; k++) {
            history[k - 1] = history[k];
        }
        history[9] = copiedPic;
    }

    // TODO: update github posting in order to show off the full functionality of
    // the program and make it fully presentable


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'h') {
            shiftStorage(pixels);
            pixels = flipHorizontal(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'v') {
            shiftStorage(pixels);
            pixels = flipVertical(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'g') {
            shiftStorage(pixels);
            pixels = greyscale(pixels);
            repaint();
        }
        if (e.getKeyChar() == 's') {
            shiftStorage(pixels);
            pixels = sepia(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'u') {
            undo();
            repaint();
        }
        if (e.getKeyChar() == 'b') {
            shiftStorage(pixels);
            pixels = blur(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'p') {
            shiftStorage(pixels);
            pixels = posterize(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'j') {
            shiftStorage(pixels);
            pixels = contrastUp(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'k') {
            shiftStorage(pixels);
            pixels = contrastDown(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'i') {
            shiftStorage(pixels);
            pixels = brighten(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'o') {
            shiftStorage(pixels);
            pixels = darken(pixels);
            repaint();
        }
        if (e.getKeyChar() == 'q') {
            saveImage();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
