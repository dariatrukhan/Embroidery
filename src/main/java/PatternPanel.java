import javax.swing.*;
import java.awt.*;

public class PatternPanel extends JPanel {
    private static final int GRID_COUNT = 17;
    private final int[][] grid = new int[GRID_COUNT][GRID_COUNT];

    // [індекс літери][рядок][стовпчик]
    private final int[][][] highlightMasks = new int[5][GRID_COUNT][GRID_COUNT];

    private int activeSector = -1;

    private final Color CANVAS_COLOR = new Color(245, 235, 215);
    private static final Color HOLE_COLOR = new Color(180, 170, 150);
    private final Color RED = new Color(193, 91, 91);
    private final Color BLACK = new Color(74, 59, 59);
    private final Color HIGHLIGHT_COLOR = new Color(60, 189, 209);

    public PatternPanel() {
        int[][] pattern = {
                {0,0,0,0,0,3,3,0,0,0,3,3,0,0,0,0,0},
                {0,1,0,1,0,3,0,3,1,3,0,3,0,1,0,1,0},
                {0,0,1,1,0,0,3,1,1,1,3,0,0,1,1,0,0},
                {0,1,1,1,1,3,1,0,3,0,1,3,0,1,1,1,0},
                {0,0,0,0,1,1,0,3,0,3,0,1,1,1,0,0,0},
                {3,3,0,3,1,1,3,0,3,0,3,1,1,3,0,3,3},
                {3,0,3,1,0,3,0,3,3,3,0,3,0,1,3,0,3},
                {0,3,1,0,3,0,3,0,0,0,3,0,3,0,1,3,0},
                {0,1,1,3,0,3,3,0,0,0,3,3,0,3,1,1,0}, // центр (row 8)
                {0,3,1,0,3,0,3,0,0,0,3,0,3,0,1,3,0},
                {3,0,3,1,0,3,0,3,3,3,0,3,0,1,3,0,3},
                {3,3,0,3,1,1,3,0,3,0,3,1,1,3,0,3,3},
                {0,0,0,1,1,1,0,3,0,3,0,1,1,0,0,0,0},
                {0,1,1,1,0,3,1,0,3,0,1,3,1,1,1,1,0},
                {0,0,1,1,0,0,3,1,1,1,3,0,0,1,1,0,0},
                {0,1,0,1,0,3,0,3,1,3,0,3,0,1,0,1,0},
                {0,0,0,0,0,3,3,0,0,0,3,3,0,0,0,0,0}
        };
        for (int r = 0; r < GRID_COUNT; r++) System.arraycopy(pattern[r], 0, grid[r], 0, GRID_COUNT);
        initMasks();
    }

    private void initMasks() {
        // маска для 'Д' (index 0)
        highlightMasks[0] = new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,1,0,1,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,1,1,1,0,1,0,0,0,0,0},
                {0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0},
                {0,0,0,1,0,1,1,0,0,0,1,1,0,1,0,0,0},// центр (row 8)
                {0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0},
                {0,0,0,0,0,1,0,1,1,1,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,1,0,1,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };

        // маска для 'а' (index 1)
        highlightMasks[1] = new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0},
                {0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0},
                {0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,0},// центр (row 8)
                {0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0},
                {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };

        // маска для 'р' (index 2)
        highlightMasks[2] = new int[][]{
                {0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0},
                {0,0,0,0,0,1,0,1,0,1,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,1},
                {1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
                {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},// центр (row 8)
                {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
                {1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
                {1,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,1,0,1,0,1,0,0,0,0,0},
                {0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0}
        };
        // маска для ' /'/ ' (index 3)
        highlightMasks[3] = new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,1,1,0,0,0,0,0,1,1,1,0,0,0},
                {0,0,0,0,1,1,0,0,0,0,0,1,1,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},// центр (row 8)
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,1,1,0,0,0,0,0,1,1,0,0,0,0},
                {0,0,0,1,1,1,0,0,0,0,0,1,1,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };
        // маска для ' я ' (index 4)
        highlightMasks[4] = new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0},
                {0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,0,0},
                {0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},// центр (row 8)
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0},
                {0,0,1,1,0,0,0,0,0,0,0,0,0,1,1,0,0},
                {0,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };

    }


    public void highlightSector(int sector) {
        this.activeSector = sector;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellSize = 30;
        int gridTotalSize = GRID_COUNT * cellSize;
        int offsetX = (getWidth() - gridTotalSize) / 2;
        int offsetY = (getHeight() - gridTotalSize) / 2;

        g2.setColor(CANVAS_COLOR);
        g2.fillRect(offsetX - 20, offsetY - 20, gridTotalSize + 40, gridTotalSize + 40);

        for (int row = 0; row < GRID_COUNT; row++) {
            for (int col = 0; col < GRID_COUNT; col++) {
                int x = offsetX + col * cellSize;
                int y = offsetY + row * cellSize;

                g2.setColor(HOLE_COLOR);
                g2.fillOval(x - 2, y - 2, 4, 4);

                if (grid[row][col] > 0) {
                    boolean isHighlighted = false;
                    if (activeSector >= 0 && activeSector < highlightMasks.length) {
                        if (highlightMasks[activeSector][row][col] == 1) {
                            isHighlighted = true;
                        }
                    }
                    Color crossColor;
                    if (isHighlighted) {
                        crossColor = HIGHLIGHT_COLOR;
                    } else {
                        crossColor = (grid[row][col] == 1) ? RED : BLACK;
                    }
                    cross(g2, x, y, cellSize, crossColor);

                }
            }
        }

       hole(g2, offsetX, offsetY, cellSize, gridTotalSize);
    }
    static void cross(Graphics2D g2, int x, int y, int cellSize, Color crossColor){
        Color shadowColor = new Color(crossColor.getRed(), crossColor.getGreen(), crossColor.getBlue(), 80);

        g2.setColor(shadowColor);
        g2.fillRect(x + 3, y + 3, cellSize - 5, cellSize - 5);

        g2.setColor(crossColor);
        g2.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int p = 8;
        g2.drawLine(x + p, y + p, x + cellSize - p, y + cellSize - p);
        g2.drawLine(x + cellSize - p, y + p, x + p, y + cellSize - p);
    }
    static void hole( Graphics2D g2, int offsetX, int offsetY, int cellSize, int gridTotalSize){
        // точки
        g2.setColor(HOLE_COLOR);
        for (int i = 0; i <= GRID_COUNT; i++) {
            g2.fillOval(offsetX + gridTotalSize - 2, offsetY + i * cellSize - 2, 4, 4);
            g2.fillOval(offsetX + i * cellSize - 2, offsetY + gridTotalSize - 2, 4, 4);
        }
    }
}