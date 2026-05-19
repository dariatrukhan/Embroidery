import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver {

    public static void savePanelAsPNG(PatternPanel panel) {
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
}
