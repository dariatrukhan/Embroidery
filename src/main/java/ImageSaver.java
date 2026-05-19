import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ImageSaver {

    public static void savePanelAsPNG(JPanel panel) {
        int cellSize = 30;
        int gridCount = 17;
        int gridTotalSize = gridCount * cellSize;

        int canvasWidth = gridTotalSize + 40;
        int canvasHeight = gridTotalSize + 40;

        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int currentOffsetX = (panel.getWidth() - gridTotalSize) / 2;
        int currentOffsetY = (panel.getHeight() - gridTotalSize) / 2;

        g2d.translate(-(currentOffsetX - 20), -(currentOffsetY - 20));
        panel.paint(g2d);
        g2d.dispose();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Зберегти вишиванку як PNG");

        fileChooser.setSelectedFile(new File("vyshyvanka.png"));

        int userSelection = fileChooser.showSaveDialog(panel);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                fileToSave = new File(filePath + ".png");
            }

            try {
                ImageIO.write(image, "PNG", fileToSave);
                JOptionPane.showMessageDialog(panel, "Малюнок успішно збережено!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(panel, "Помилка при збереженні: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public static int[][] loadPanelFromPNG(Component parent, int gridCount, int cellSize) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Оберіть файл вишиванки PNG");

        int userSelection = fileChooser.showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(fileToLoad);
                if (image == null) {
                    JOptionPane.showMessageDialog(parent, "Не вдалося прочитати файл.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                int[][] loadedGrid = new int[gridCount][gridCount];

                int startOffset = 20;

                for (int row = 0; row < gridCount; row++) {
                    for (int col = 0; col < gridCount; col++) {
                        int centerX = startOffset + col * cellSize + (cellSize / 2);
                        int centerY = startOffset + row * cellSize + (cellSize / 2);

                        if (centerX < image.getWidth() && centerY < image.getHeight()) {
                            int rgb = image.getRGB(centerX, centerY);

                            Color pixelColor = new Color(rgb, true);
                            if (pixelColor.getAlpha() > 0 &&
                                    !(pixelColor.getRed() == 245 && pixelColor.getGreen() == 235 && pixelColor.getBlue() == 215)) {
                                loadedGrid[row][col] = rgb;
                            } else {
                                loadedGrid[row][col] = 0; // пусто
                            }
                        }
                    }
                }

                JOptionPane.showMessageDialog(parent, "Малюнок успішно завантажено!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                return loadedGrid;

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Помилка при читанні файлу: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
