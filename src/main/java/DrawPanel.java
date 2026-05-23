import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DrawPanel extends JPanel {
    private final DrawController controller;

    private final Color CANVAS_COLOR = new Color(245, 235, 215);
    private final Color HOLE_COLOR = new Color(180, 170, 150);
    private final Color TEXT_COLOR = new Color(74, 59, 59);
    private final int fixedCanvasSize = 510;

    public DrawPanel(JPanel mainContainer, CardLayout cardLayout, Font baseFont) {
        this.controller = new DrawController();
        setLayout(new BorderLayout());

        JPanel topPanel = getJPanel(mainContainer, cardLayout, baseFont);
        add(topPanel, BorderLayout.NORTH);

        // палітра інструментів
        ToolBar palettePanel = new ToolBar(controller, this, baseFont);

        JPanel paletteWrapper = new JPanel(new GridBagLayout());
        paletteWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 100, 30);
        paletteWrapper.add(palettePanel, gbc);

        add(paletteWrapper, BorderLayout.EAST);

        // рух мишки
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                triggerDraw(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                triggerDraw(e.getX(), e.getY());
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void triggerDraw(int mouseX, int mouseY) {
        int offsetX = 50;
        int offsetY = (getHeight() - fixedCanvasSize) / 2;
        controller.handleDraw(mouseX, mouseY, offsetX, offsetY, fixedCanvasSize);
        this.repaint(offsetX, offsetY, fixedCanvasSize, fixedCanvasSize);
    }

    private JPanel getJPanel(JPanel mainContainer, CardLayout cardLayout, Font baseFont) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 15));
        topPanel.setOpaque(false);

        JLabel label = new JLabel("Режим малювання");
        label.setFont(baseFont.deriveFont(20f));
        label.setForeground(TEXT_COLOR);

        JButton backBtn = Capabilities.statButton("Назад", baseFont.deriveFont(13f));
        backBtn.setFocusPainted(false);
        backBtn.setMaximumSize(new Dimension(100, 35));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "MAIN"));
        topPanel.add(label);
        topPanel.add(backBtn);
        return topPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offsetX = 50;
        int offsetY = (getHeight() - fixedCanvasSize) / 2;

        g2.setColor(CANVAS_COLOR);
        g2.fillRect(offsetX - 20, offsetY - 20, fixedCanvasSize + 40, fixedCanvasSize + 40);

        int gridCount = controller.getGridCount();
        int[][] grid = controller.getGrid();
        double exactCellSize = (double) fixedCanvasSize / gridCount;

        // сітка
        for (int row = 0; row < gridCount; row++) {
            for (int col = 0; col < gridCount; col++) {
                int x = (int) Math.round(offsetX + col * exactCellSize);
                int y = (int) Math.round(offsetY + row * exactCellSize);
                int currentCellSize = (int) Math.round((col + 1) * exactCellSize) - (int) Math.round(col * exactCellSize);

                g2.setColor(HOLE_COLOR);
                g2.fillOval(x - 2, y - 2, 4, 4);

                if (grid[row][col] != 0) {
                    Color crossColor = new Color(grid[row][col]);
                    drawAdaptiveCross(g2, x, y, currentCellSize, crossColor);
                }
            }
        }

        // точки
        g2.setColor(HOLE_COLOR);
        for (int i = 0; i <= gridCount; i++) {
            int pos = (int) Math.round(i * exactCellSize);
            g2.fillOval(offsetX + fixedCanvasSize - 2, offsetY + pos - 2, 4, 4);
            g2.fillOval(offsetX + pos - 2, offsetY + fixedCanvasSize - 2, 4, 4);
        }
        g2.setStroke(new BasicStroke(2));
    }

    public static void drawAdaptiveCross(Graphics2D g2, int x, int y, int size, Color crossColor) {
        Color shadowColor = new Color(crossColor.getRed(), crossColor.getGreen(), crossColor.getBlue(), 80);
        g2.setColor(shadowColor);
        g2.fillRect(x + 2, y + 2, size - 3, size - 3);

        g2.setColor(crossColor);
        int strokeThickness = Math.max(2, size / 4);
        int padding = Math.max(2, size / 5);

        g2.setStroke(new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + padding, y + padding, x + size - padding, y + size - padding);
        g2.drawLine(x + size - padding, y + padding, x + padding, y + size - padding);
    }

    // завантаження файла
    public void loadGridData(int[][] loadedData) {
        if (loadedData != null) {
            controller.setGrid(loadedData);
            repaint();
        }
    }
    public DrawController getController() {
        return this.controller;
    }
}