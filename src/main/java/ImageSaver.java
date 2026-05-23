import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ImageSaver {

    public static void savePanelAsPNG(Component panel, DrawController controller) {
        int gridCount = controller.getGridCount();
        int cellSize = (gridCount == 17) ? 30 : (gridCount == 27 ? 18 : 13);
        int gridTotalSize = gridCount * cellSize;
        int canvasWidth = gridTotalSize + 40;
        int canvasHeight = gridTotalSize + 40;

        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color canvasColor = new Color(245, 235, 215);
        g2d.setColor(canvasColor);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);

        int[][] grid = controller.getGrid();
        int startOffset = 20;

        for (int row = 0; row < gridCount; row++) {
            for (int col = 0; col < gridCount; col++) {
                if (grid[row][col] != 0) {
                    int x = startOffset + col * cellSize;
                    int y = startOffset + row * cellSize;
                    Color crossColor = new Color(grid[row][col]);

                    DrawPanel.drawAdaptiveCross(g2d, x, y, cellSize, crossColor);
                }
            }
        }
        g2d.dispose();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Зберегти вишиванку як PNG");
        fileChooser.setSelectedFile(new File("vyshyvanka.png"));

        if (fileChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                fileToSave = new File(filePath + ".png");
            }
            try {
                ImageIO.write(image, "PNG", fileToSave);
                JOptionPane.showMessageDialog(panel, "Малюнок успішно збережено!", "Збережено", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(panel, "Помилка при збереженні: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static int[][] loadPanelFromPNG(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Оберіть файл");

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(fileToLoad);
                if (image == null) {
                    JOptionPane.showMessageDialog(parent, "Не вдалося прочитати файл.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                int width = image.getWidth();
                int innerWidth = width - 40;
                int detectedGridCount;

                //  17:17 * 30 = 510, 27:27 * 18 = 486, 37:37 * 13 = 481
                if (innerWidth >= 491 && innerWidth <= 510){
                    detectedGridCount = 17;
                }
                else if (innerWidth >= 485 && innerWidth <= 490) {
                    detectedGridCount = 27;
                } else {
                    detectedGridCount = 37;
                }
                double calculatedCellSize = (double) innerWidth / detectedGridCount;
                int[][] loadedGrid = new int[detectedGridCount][detectedGridCount];
                int startOffset = 20;

                for (int row = 0; row < detectedGridCount; row++) {
                    for (int col = 0; col < detectedGridCount; col++) {

                        int centerX = (int) Math.round(startOffset + col * calculatedCellSize + (calculatedCellSize / 2.0));
                        int centerY = (int) Math.round(startOffset + row * calculatedCellSize + (calculatedCellSize / 2.0));

                        if (centerX < image.getWidth() && centerY < image.getHeight()) {
                            int rgb = image.getRGB(centerX, centerY);
                            Color pixelColor = new Color(rgb, true);

                            if (pixelColor.getAlpha() > 0 &&
                                    !(pixelColor.getRed() == 245 && pixelColor.getGreen() == 235 && pixelColor.getBlue() == 215)) {
                                loadedGrid[row][col] = rgb;
                            } else {
                                loadedGrid[row][col] = 0;
                            }
                        }
                    }
                }

                JOptionPane.showMessageDialog(parent, "Малюнок успішно завантажено!\nМасштаб: " + detectedGridCount + "x" + detectedGridCount, "Завантажено", JOptionPane.INFORMATION_MESSAGE);
                return loadedGrid;

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Помилка при читанні файлу: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}