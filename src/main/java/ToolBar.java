import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ToolBar extends JPanel {
    private final DrawController controller;
    private final JPanel canvasPanel;

    private final Color TEXT_COL = new Color(74, 59, 59);
    private final Color CANVAS_COL = new Color(245, 235, 215);
    private final Color PALETTE_BORDER = new Color(140, 130, 110);

    public ToolBar(DrawController controller, JPanel canvasPanel, Font baseFont) {
        this.controller = controller;
        this.canvasPanel = canvasPanel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(225, 215, 195));
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PALETTE_BORDER, 3),
                BorderFactory.createEmptyBorder(20, -1, 20, -1)
        ));
        setMaximumSize(new Dimension(220, 500));
        setPreferredSize(new Dimension(220, 500));

//———————————————————ЗАГОЛОВОК
        JLabel paletteTitle = new JLabel("ІНСТРУМЕНТИ");
        paletteTitle.setFont(baseFont.deriveFont(13f));
        paletteTitle.setForeground(TEXT_COL);
        paletteTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(paletteTitle);
        add(Box.createRigidArea(new Dimension(0, 15)));

//———————————————————ЛІНІЯ
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
        separator.setForeground(PALETTE_BORDER);
        separator.setBackground(PALETTE_BORDER);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(separator);
        add(Box.createRigidArea(new Dimension(0, 15)));

//———————————————————ІНДИКАТОР КОЛЬОРУ
        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(controller.getSelectedColor());
        colorIndicator.setPreferredSize(new Dimension(60, 60));
        colorIndicator.setMaximumSize(new Dimension(60, 60));
        colorIndicator.setBorder(new LineBorder(TEXT_COL, 3));
        colorIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(colorIndicator);
        add(Box.createRigidArea(new Dimension(0, 15)));

//———————————————————ОБРАТИ КОЛІР
        JButton chooseColorBtn = createToolButton("ОБРАТИ КОЛІР", TEXT_COL, baseFont);
        chooseColorBtn.setBackground(CANVAS_COL);
        chooseColorBtn.setForeground(TEXT_COL);
        chooseColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Оберіть колір", controller.getSelectedColor());
            if (chosen != null) {
                controller.setSelectedColor(chosen);
                colorIndicator.setBackground(chosen);
                controller.setEraserActive(false);
                colorIndicator.setBorder(new LineBorder(Color.WHITE, 3));
            }
        });

//———————————————————ПІПЕТКА
        JButton pipetteBtn = createToolButton("ПІПЕТКА", TEXT_COL, baseFont);
        pipetteBtn.setBackground(CANVAS_COL);
        pipetteBtn.setForeground(TEXT_COL);

        pipetteBtn.addActionListener(e -> {
            canvasPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            pipetteBtn.setBackground(new Color(175, 230, 245));

            java.awt.event.MouseListener pipetteListener = new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent pip) {
                    try {
                        Robot robot = new Robot();
                        Point screenLocation = pip.getLocationOnScreen();
                        Color pickedColor = robot.getPixelColor(screenLocation.x, screenLocation.y);

                        controller.setSelectedColor(pickedColor);
                        colorIndicator.setBackground(pickedColor);
                        controller.setEraserActive(false);
                        colorIndicator.setBorder(new LineBorder(Color.WHITE, 3));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        canvasPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        pipetteBtn.setBackground(CANVAS_COL);

                        canvasPanel.removeMouseListener(this);
                        canvasPanel.repaint();
                        repaint();
                    }
                }
            };
            canvasPanel.addMouseListener(pipetteListener);
        });
        add(pipetteBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));

//———————————————————ГУМКА
        JButton eraserBtn = createToolButton("ГУМКА", TEXT_COL, baseFont);
        eraserBtn.setBackground(CANVAS_COL);
        eraserBtn.setForeground(TEXT_COL);
        eraserBtn.addActionListener(e -> {
            controller.setEraserActive(!controller.EraserActive());
            if (controller.EraserActive()) {
                eraserBtn.setBorder(new LineBorder(new Color(60, 189, 209), 3));
                colorIndicator.setBorder(new LineBorder(TEXT_COL, 3));
            } else {
                eraserBtn.setBorder(new LineBorder(TEXT_COL, 3));
                colorIndicator.setBorder(new LineBorder(Color.WHITE, 3));
            }
        });
        chooseColorBtn.addActionListener(e -> eraserBtn.setBorder(new LineBorder(TEXT_COL, 3)));

//———————————————————ОЧИСТИТИ
        JButton clearBtn = createToolButton("ОЧИСТИТИ", TEXT_COL, baseFont);
        clearBtn.setBackground(new Color(255, 210, 210));
        clearBtn.setForeground(TEXT_COL);
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Повністю очистити полотно?", "Очистити", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.clearGrid();
                canvasPanel.repaint();
            }
        });

        add(chooseColorBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(eraserBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(clearBtn);
        add(Box.createRigidArea(new Dimension(0, 15)));

//———————————————————РОЗМІР ПОЛОТНА
        JButton sizeBtn = createToolButton("РОЗМІР ПОЛОТНА", PALETTE_BORDER, baseFont);
        sizeBtn.addActionListener(e -> showSizeChooser());
        add(sizeBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));

//———————————————————ЗБЕРЕГТИ
        JButton saveBtn = createToolButton("ЗБЕРЕГТИ", TEXT_COL, baseFont);
        saveBtn.setBackground(new Color(202, 238, 248));
        saveBtn.setForeground(TEXT_COL);
        saveBtn.addActionListener(e -> {
            ImageSaver.savePanelAsPNG(this, controller);
        });
        add(saveBtn);
        add(Box.createRigidArea(new Dimension(0, 15)));

//——————————————————КНОПКИ СИМЕТРІЇ
        JLabel symmetryTitle = new JLabel("СИМЕТРІЯ");
        symmetryTitle.setFont(baseFont.deriveFont(11f));
        symmetryTitle.setForeground(TEXT_COL);
        symmetryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(symmetryTitle);
        add(Box.createRigidArea(new Dimension(0, 5)));

        JCheckBox horizCheck = new JCheckBox("горизонтальна");
        horizCheck.setFont(baseFont.deriveFont(12f));
        horizCheck.setForeground(TEXT_COL);
        horizCheck.setOpaque(false);
        horizCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        horizCheck.addActionListener(e -> controller.setHorActive(horizCheck.isSelected()));

        JCheckBox vertCheck = new JCheckBox("вертикальна");
        vertCheck.setFont(baseFont.deriveFont(12f));
        vertCheck.setForeground(TEXT_COL);
        vertCheck.setOpaque(false);
        vertCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        vertCheck.addActionListener(e -> controller.setVerActive(vertCheck.isSelected()));

        add(horizCheck);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(vertCheck);

//———————————————————ДУБЛЮВАННЯ
        add(Box.createRigidArea(new Dimension(0, 15)));
        JLabel repeatTitle = new JLabel("ДУБЛЮВАННЯ");
        repeatTitle.setFont(baseFont.deriveFont(11f));
        repeatTitle.setForeground(TEXT_COL);
        repeatTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(repeatTitle);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel stepPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        stepPanel.setOpaque(false);

        JCheckBox stepLabel = new JCheckBox("Крок:");
        stepLabel.setFont(baseFont.deriveFont(11f));
        stepLabel.setForeground(TEXT_COL);

        SpinnerModel spinnerModel = new SpinnerNumberModel(controller.getRepeatStep(), 5, 15, 1);
        JSpinner stepSpinner = new JSpinner(spinnerModel);
        stepSpinner.setFont(baseFont.deriveFont(11f));
        stepSpinner.setPreferredSize(new Dimension(50, 22));
        stepSpinner.setEnabled(false);

        stepLabel.addActionListener(e -> {
            boolean active = stepLabel.isSelected();
            controller.setRepeatActive(active);
            stepSpinner.setEnabled(active);
        });

        stepSpinner.addChangeListener(e -> {
            int newStep = (int) stepSpinner.getValue();
            controller.setRepeatStep(newStep);
        });

        stepPanel.add(stepLabel);
        stepPanel.add(stepSpinner);

        add(stepLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(stepPanel);

//———————————————————КНОПКИ ДУБЛЮВАННЯ
        JButton btnDupVert = createToolButton("вертикально", TEXT_COL, baseFont);
        btnDupVert.addActionListener(e -> {
            controller.duplicateVertically();
            canvasPanel.repaint();
        });

        JButton btnDupHor = createToolButton("горизонтально", PALETTE_BORDER, baseFont);
        btnDupHor.addActionListener(e -> {
            controller.duplicateHorizontally();
            canvasPanel.repaint();
        });

        add(Box.createRigidArea(new Dimension(0, 15)));
        add(btnDupVert);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(btnDupHor);
    }
    private void showSizeChooser() {
        SpinnerNumberModel widthModel = new SpinnerNumberModel(controller.getColsCount(), 17, 68, 1);

        JSpinner widthSpinner = new JSpinner(widthModel);
        JButton defaultButton = new JButton("17 x 17 за замовчуванням");

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 10));

        defaultButton.addActionListener(e -> {
            widthSpinner.setValue(17);
        });

        panel.add(new JLabel("Розмір (17-68):"));
        panel.add(widthSpinner);
        panel.add(widthSpinner);
        panel.add(new JLabel(""));
        panel.add(defaultButton);

        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                panel,
                "Оберіть кількість клітинок в полотні",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int width = (int) widthSpinner.getValue();

            controller.changeGridSize(width, width);
            canvasPanel.repaint();
        }
    }

//——————————————————————КНОПКИ ІНСТРУМЕНТІВ
    private JButton createToolButton(String text, Color borderCol, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font.deriveFont(10f));
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(borderCol, 2));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(150, 40));
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }
}