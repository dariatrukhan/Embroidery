import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ImageSaver {

    public static void savePanelAsPNG(Component panel, DrawController controller) {
        int rows = controller.getRowsCount();
        int cols = controller.getColsCount();

        int maxDimension = Math.max(rows, cols);
        int cellSize = (maxDimension <= 17) ? 30 : (maxDimension <= 27 ? 18 : 13);

        int canvasWidth = cols * cellSize + 40;
        int canvasHeight = rows * cellSize + 40;

        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color canvasColor = new Color(245, 235, 215);
        g2d.setColor(canvasColor);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);

        int[][] grid = controller.getGrid();
        int startOffset = 20;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
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

                int innerWidth = image.getWidth() - 40;
                int innerHeight = image.getHeight() - 40;

//————————————————————————————————————ВИЗНАЧЕННЯ cellSize
                int maxInner = Math.max(innerWidth, innerHeight);
                int cellSize;

                if (maxInner % 30 == 0 || (maxInner >= 510 && maxInner <= 540)) {
                    cellSize = 30;
                } else if (maxInner % 18 == 0 || (maxInner >= 486 && maxInner <= 500)) {
                    cellSize = 18;
                } else {
                    cellSize = 13;
                }

                int detectedCols = (int) Math.round((double) innerWidth / cellSize);
                int detectedRows = (int) Math.round((double) innerHeight / cellSize);

                if (detectedCols <= 0) detectedCols = 1;
                if (detectedRows <= 0) detectedRows = 1;

                int[][] loadedGrid = new int[detectedRows][detectedCols];
                int startOffset = 20;

                for (int row = 0; row < detectedRows; row++) {
                    for (int col = 0; col < detectedCols; col++) {

                        int centerX = (int) Math.round(startOffset + col * cellSize + (cellSize / 2.0));
                        int centerY = (int) Math.round(startOffset + row * cellSize + (cellSize / 2.0));

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

                JOptionPane.showMessageDialog(parent,
                        "Малюнок успішно завантажено!\nРозмір полотна: " + detectedCols + "x" + detectedRows,
                        "Завантажено", JOptionPane.INFORMATION_MESSAGE);

                return loadedGrid;

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Помилка при читанні файла: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}