import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class DrawPanel extends JPanel {
    private final int GRID_COUNT = 17;
    private final int cellSize = 30;

    private final int[][] grid = new int[GRID_COUNT][GRID_COUNT];

    private Color selectedColor = new Color(193, 91, 91);
    private boolean isEraserActive = false;

    private final Color CANVAS_COLOR = new Color(245, 235, 215);
    private final Color HOLE_COLOR = new Color(180, 170, 150);
    private final Color TEXT_COLOR = new Color(74, 59, 59);

    private final Color PALETTE_BG = new Color(225, 215, 195);
    private final Color PALETTE_BORDER = new Color(140, 130, 110);

    public DrawPanel(JPanel mainContainer, CardLayout cardLayout, Font baseFont) {

        setLayout(new BorderLayout());

        JPanel topPanel = getJPanel(mainContainer, cardLayout, baseFont);
        add(topPanel, BorderLayout.NORTH);

        JPanel palettePanel = new JPanel();
        palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.Y_AXIS));
        palettePanel.setBackground(PALETTE_BG);
        palettePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PALETTE_BORDER, 4, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel paletteTitle = new JLabel("ІНСТРУМЕНТИ");
        paletteTitle.setFont(baseFont.deriveFont(11f));
        paletteTitle.setForeground(TEXT_COLOR);
        paletteTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        palettePanel.add(paletteTitle);
        palettePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // індикатор поточного кольору
        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(selectedColor);
        colorIndicator.setPreferredSize(new Dimension(60, 60));
        colorIndicator.setMaximumSize(new Dimension(60, 60));
        colorIndicator.setBorder(new LineBorder(TEXT_COLOR, 3));
        colorIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
        palettePanel.add(colorIndicator);
        palettePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton chooseColorBtn = createToolButton("ОБРАТИ КОЛІР", TEXT_COLOR, baseFont);
        chooseColorBtn.setBackground(new Color(245, 235, 215));
        chooseColorBtn.setForeground(TEXT_COLOR);

        chooseColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Оберіть колір", selectedColor);
            if (chosen != null) {
                selectedColor = chosen;
                colorIndicator.setBackground(selectedColor);
                isEraserActive = false;
                colorIndicator.setBorder(new LineBorder(Color.WHITE, 3));
            }
        });

        // гумка
        JButton eraserBtn = createToolButton("ГУМКА", TEXT_COLOR, baseFont);
        eraserBtn.setBackground(CANVAS_COLOR);
        eraserBtn.setForeground(TEXT_COLOR);

        eraserBtn.addActionListener(e -> {
            isEraserActive = !isEraserActive;

            if (isEraserActive) {
                eraserBtn.setBorder(new LineBorder(new Color(60, 189, 209), 3));
                colorIndicator.setBorder(new LineBorder(TEXT_COLOR, 3));
            } else {
                eraserBtn.setBorder(new LineBorder(TEXT_COLOR, 3));
                colorIndicator.setBorder(new LineBorder(Color.WHITE, 3));
            }
        });
        chooseColorBtn.addActionListener(e -> eraserBtn.setBorder(new LineBorder(TEXT_COLOR, 2)));

        palettePanel.add(chooseColorBtn);
        palettePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        palettePanel.add(eraserBtn);

        JPanel paletteWrapper = new JPanel(new GridBagLayout());
        paletteWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 40);
        paletteWrapper.add(palettePanel, gbc);
        add(paletteWrapper, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int gridTotalSize = GRID_COUNT * cellSize;
                int offsetX = (getWidth() - gridTotalSize) / 2;
                int offsetY = (getHeight() - gridTotalSize) / 2;

                int col = (e.getX() - offsetX) / cellSize;
                int row = (e.getY() - offsetY) / cellSize;

                if (col >= 0 && col < GRID_COUNT && row >= 0 && row < GRID_COUNT) {
                    if (isEraserActive) {
                        grid[row][col] = 0;
                    } else {
                        grid[row][col] = selectedColor.getRGB();
                    }
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drawAtPoint(e.getX(), e.getY()); // при одному кліку
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawAtPoint(e.getX(), e.getY()); // поки затиснута мишка
            }
        });
    }

    private void drawAtPoint(int mouseX, int mouseY) {
        int gridTotalSize = GRID_COUNT * cellSize;
        int offsetX = (getWidth() - gridTotalSize) / 2;
        int offsetY = (getHeight() - gridTotalSize) / 2;

        int col = (mouseX - offsetX) / cellSize;
        int row = (mouseY - offsetY) / cellSize;

        if (col >= 0 && col < GRID_COUNT && row >= 0 && row < GRID_COUNT) {
            if (isEraserActive) {
                grid[row][col] = 0;
            } else {
                grid[row][col] = selectedColor.getRGB();
            }
            repaint();
        }
    }

    private JPanel getJPanel(JPanel mainContainer, CardLayout cardLayout, Font baseFont) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        topPanel.setOpaque(false);

        JLabel label = new JLabel("Режим малювання");
        label.setFont(baseFont.deriveFont(20f));
        label.setForeground(TEXT_COLOR);

        String menuItem = "Назад";
        Font menuFont = baseFont.deriveFont(14f);
        JButton backBtn = Capabilities.createPixelButton(menuItem, menuFont);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "MAIN"));
        topPanel.add(label);
        topPanel.add(backBtn);
        return topPanel;
    }

    private JButton createToolButton(String text, Color borderCol, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font.deriveFont(10f));
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(borderCol, 2));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(150, 40));
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

                if (grid[row][col] != 0) {
                    Color crossColor = new Color(grid[row][col]);
                    PatternPanel.cross(g2, x, y, cellSize, crossColor);

                }
            }
        }
        PatternPanel.hole(g2, offsetX, offsetY, cellSize, gridTotalSize);
    }

    public void loadGridData(int[][] loadedData) {
        if (loadedData != null) {
            for (int row = 0; row < GRID_COUNT; row++) {
                System.arraycopy(loadedData[row], 0, this.grid[row], 0, GRID_COUNT);
            }
            repaint(); // Оновлюємо полотно, щоб хрестики з'явилися
        }
    }

}
