import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class DrawPanel extends JPanel {
    private final DrawController controller;

    private final Color CANVAS_COLOR = new Color(245, 235, 215);
    private final Color HOLE_COLOR = new Color(180, 170, 150);
    private final Color TEXT_COLOR = new Color(74, 59, 59);
    private final int fixedCanvasSize = 510;

    public DrawPanel(JPanel mainContainer, CardLayout cardLayout, Font baseFont) {
        this.controller = new DrawController();
        setLayout(new BorderLayout());
        setDoubleBuffered(true);
        initUIComponents(mainContainer, cardLayout, baseFont);
        setupKeyBindings();
        setupMouseHandlers();
    }

    private void initUIComponents(JPanel mainContainer, CardLayout cardLayout, Font baseFont) {
        JPanel topPanel = getJPanel(mainContainer, cardLayout, baseFont);
        add(topPanel, BorderLayout.NORTH);

        ToolBar palettePanel = new ToolBar(controller, this, baseFont);

        JPanel paletteWrapper = new JPanel(new GridBagLayout());
        paletteWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 30);
        paletteWrapper.add(palettePanel, gbc);

        add(paletteWrapper, BorderLayout.EAST);
    }

    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // +
        inputMap.put(KeyStroke.getKeyStroke('+'), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke('='), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke("EQUALS"), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke("ADD"), "zoomIn");

        // -
        inputMap.put(KeyStroke.getKeyStroke('-'), "zoomOut");
        inputMap.put(KeyStroke.getKeyStroke("MINUS"), "zoomOut");
        inputMap.put(KeyStroke.getKeyStroke("SUBTRACT"), "zoomOut");

        actionMap.put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.zoomIn();
                repaint();
            }
        });

        actionMap.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.zoomOut();
                repaint();
            }
        });
    }

    private void setupMouseHandlers() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                controller.saveStateToUndo();
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
        int rows = controller.getRowsCount();
        int cols = controller.getColsCount();
        int maxDimension = Math.max(rows, cols);

        double exactCellSize = ((double) fixedCanvasSize / maxDimension) * controller.getScaleFactor();
        int gridTotalWidth = (int) Math.round(cols * exactCellSize);
        int gridTotalHeight = (int) Math.round(rows * exactCellSize);

        int offsetX = (getWidth() - gridTotalWidth - 250) / 2;
        int offsetY = (getHeight() - gridTotalHeight - 10) / 2;

        controller.handleDraw(mouseX, mouseY, offsetX, offsetY, fixedCanvasSize);
        repaint();
    }

    private JPanel getJPanel(JPanel mainContainer, CardLayout cardLayout, Font baseFont) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 15));
        topPanel.setOpaque(false);

        JLabel label = new JLabel("Режим малювання");
        label.setFont(baseFont.deriveFont(20f));
        label.setForeground(TEXT_COLOR);
        JButton backBtn = Capabilities.statButton("Назад", baseFont.deriveFont(13f));
        backBtn.setFocusPainted(true);
        backBtn.setOpaque(false);
        backBtn.setContentAreaFilled(false);

        Color normalText = new Color(0x075767);
        Color themeColor = new Color(0x448695);
        Color themeColor2 = new Color(0xA4D3D8);

        backBtn.setForeground(normalText);
        backBtn.setBorder(new LineBorder(themeColor, 2));
        backBtn.setMaximumSize(new Dimension(100, 25));
        backBtn.setPreferredSize(new Dimension(100, 25));

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backBtn.setForeground(themeColor);
                backBtn.setBorder(new LineBorder(themeColor2, 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backBtn.setForeground(normalText);
                backBtn.setBorder(new LineBorder(themeColor, 2));
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "MAIN"));
        topPanel.add(label);
        topPanel.add(backBtn);
        return topPanel;
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
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = controller.getRowsCount();
        int cols = controller.getColsCount();
        int[][] grid = controller.getGrid();

        int maxDimension = Math.max(rows, cols);
        double exactCellSize = ((double) fixedCanvasSize / maxDimension) * controller.getScaleFactor();

        int gridTotalWidth = (int) Math.round(cols * exactCellSize);
        int gridTotalHeight = (int) Math.round(rows * exactCellSize);

        int offsetX = (getWidth() - gridTotalWidth - 250) / 2;
        int offsetY = (getHeight() - gridTotalHeight - 10) / 2;

        g2.setColor(CANVAS_COLOR);
        g2.fillRect(offsetX - 20, offsetY - 20, gridTotalWidth + 40, gridTotalHeight + 40);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] != 0) {
                    int x = (int) Math.round(offsetX + col * exactCellSize);
                    int y = (int) Math.round(offsetY + row * exactCellSize);

                    int currentCellSize = (int) Math.round((col + 1) * exactCellSize) - (int) Math.round(col * exactCellSize);

                    Color crossColor = new Color(grid[row][col]);
                    drawAdaptiveCross(g2, x, y, currentCellSize, crossColor);
                }
            }
        }

        g2.setColor(HOLE_COLOR);
        for (int i = 0; i <= rows; i++) {
            int yPos = (int) Math.round(offsetY + i * exactCellSize);
            for (int j = 0; j <= cols; j++) {
                int xPos = (int) Math.round(offsetX + j * exactCellSize);
                g2.fillOval(xPos - 2, yPos - 2, 4, 4);
            }
        }

        g2.setStroke(new BasicStroke(2));
    }
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