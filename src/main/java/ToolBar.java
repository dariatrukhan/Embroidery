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
                BorderFactory.createEmptyBorder(15, -1, 0, -1)
        ));
        setMaximumSize(new Dimension(220, 610));
        setPreferredSize(new Dimension(220, 610));

//———————————————————ЗАГОЛОВОК
        JLabel paletteTitle = new JLabel("ІНСТРУМЕНТИ");
        paletteTitle.setForeground(TEXT_COL);
        paletteTitle.setFont(baseFont.deriveFont(13f));
        paletteTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(paletteTitle);
        add(Box.createRigidArea(new Dimension(0, 10)));

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

        pipetteBtn.addActionListener(e -> {
            controller.setPipetteActive(true);
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
                        controller.setPipetteActive(false);

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

        eraserBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color activeBlueBorder = new Color(60, 189, 209);
            private final Color hoverColor = new Color(184, 143, 89);

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                eraserBtn.setBorder(new LineBorder(hoverColor, 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (controller.EraserActive()) {
                    eraserBtn.setBorder(new LineBorder(activeBlueBorder, 3));
                } else {
                    eraserBtn.setBorder(new LineBorder(TEXT_COL, 2));
                }
            }
        });
        eraserBtn.addActionListener(e -> {
            controller.setEraserActive(!controller.EraserActive());
            if (controller.EraserActive()) {
                eraserBtn.setBorder(new LineBorder(new Color(60, 189, 209), 3));
                colorIndicator.setBorder(new LineBorder(TEXT_COL, 2));
            } else {
                eraserBtn.setBorder(new LineBorder(TEXT_COL, 2));
            }
        });
//———————————————————ОЧИСТИТИ
        JButton clearBtn = createToolButton("ОЧИСТИТИ", TEXT_COL, baseFont);
        clearBtn.setBackground(new Color(255, 210, 210));
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
        JButton sizeBtn = createToolButton("РОЗМІР ПОЛОТНА", TEXT_COL, baseFont);
        sizeBtn.addActionListener(e -> showSizeChooser());
        add(sizeBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));

//———————————————————ЗБЕРЕГТИ
        JButton saveBtn = createToolButton("ЗБЕРЕГТИ", TEXT_COL, baseFont);
        saveBtn.setBackground(new Color(202, 238, 248));
        saveBtn.addActionListener(e -> {
            ImageSaver.savePanelAsPNG(canvasPanel, controller);
        });
        add(saveBtn);
        add(Box.createRigidArea(new Dimension(0, 15)));

//———————————————————ЛІНІЯ
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator1.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
        separator1.setForeground(PALETTE_BORDER);
        separator1.setBackground(PALETTE_BORDER);
        separator1.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(separator1);
        add(Box.createRigidArea(new Dimension(0, 15)));

//——————————————————КНОПКИ СИМЕТРІЇ
        JLabel symmetryTitle = new JLabel("СИМЕТРІЯ");
        symmetryTitle.setFont(baseFont.deriveFont(11f));
        symmetryTitle.setForeground(new Color(5, 83, 97));
        symmetryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(symmetryTitle);
        add(Box.createRigidArea(new Dimension(0, 5)));

        JCheckBox verttCheck = new JCheckBox("вертикальна");
        verttCheck.setFont(baseFont.deriveFont(12f));
        verttCheck.setOpaque(false);
        verttCheck.setForeground(TEXT_COL);
        verttCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        verttCheck.addActionListener(e -> controller.setHorActive(verttCheck.isSelected()));

        JCheckBox horCheck = new JCheckBox("горизонтальна");
        horCheck.setFont(baseFont.deriveFont(12f));
        horCheck.setOpaque(false);
        horCheck.setForeground(TEXT_COL);
        horCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        horCheck.setMargin(new Insets(0, 10, 0, 0));
        horCheck.addActionListener(e -> controller.setVerActive(horCheck.isSelected()));

        add(verttCheck);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(horCheck);
//———————————————————ЛІНІЯ
        JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator2.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
        separator2.setForeground(PALETTE_BORDER);
        separator2.setBackground(PALETTE_BORDER);
        separator2.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(separator2);
        add(Box.createRigidArea(new Dimension(0, 15)));

//———————————————————ДУБЛЮВАННЯ
        JLabel repeatTitle = new JLabel("ДУБЛЮВАННЯ");
        repeatTitle.setFont(baseFont.deriveFont(11f));
        repeatTitle.setForeground(new Color(101, 37, 5));
        repeatTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(repeatTitle);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel stepPanel = new JPanel(new GridBagLayout());
        stepPanel.setOpaque(false);
        stepPanel.setMaximumSize(new Dimension(150, 30));

        GridBagConstraints gbcStep = new GridBagConstraints();
        gbcStep.fill = GridBagConstraints.VERTICAL;
        gbcStep.gridy = 0;

        JCheckBox stepLabel = new JCheckBox("Крок");
        stepLabel.setFont(baseFont.deriveFont(11f));
        stepLabel.setOpaque(false);
        stepLabel.setMargin(new Insets(0, 20, 0, 0));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 20, 1);
        JSpinner stepSpinner = new JSpinner(spinnerModel);
        controller.setRepeatStep((int) spinnerModel.getValue() + 1);
        stepSpinner.addChangeListener(e -> {
            int spinnerValue = (int) stepSpinner.getValue();
            controller.setRepeatStep(spinnerValue + 1);
        });
        stepSpinner.setFont(baseFont.deriveFont(11f));
        stepSpinner.setPreferredSize(new Dimension(50, 30));
        stepSpinner.setEnabled(false);

        stepLabel.addActionListener(e -> {
            boolean active = stepLabel.isSelected();
            controller.setRepeatActive(active);
            stepSpinner.setEnabled(active);
        });

        gbcStep.gridx = 0;
        gbcStep.weightx = 0.0;
        gbcStep.insets = new Insets(0, 0, 0, 0);
        stepPanel.add(stepLabel, gbcStep);

        gbcStep.gridx = 1;
        gbcStep.weightx = 1;
        gbcStep.insets = new Insets(0, 0, 0, 0);
        stepPanel.add(stepSpinner, gbcStep);

        add(stepPanel);

//———————————————————КНОПКИ ДУБЛЮВАННЯ
        JButton btnDupVert = createToolButton("вертикально", PALETTE_BORDER, baseFont);
        btnDupVert.setFont(baseFont.deriveFont(10f));
        btnDupVert.addActionListener(e -> {
            controller.duplicateVertically();
            canvasPanel.repaint();
        });

        JButton btnDupHor = createToolButton("горизонтально", PALETTE_BORDER, baseFont);
        btnDupHor.setFont(baseFont.deriveFont(10f));
        btnDupHor.addActionListener(e -> {
            controller.duplicateHorizontally();
            canvasPanel.repaint();
        });

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnDupVert);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(btnDupHor);
        add(Box.createRigidArea(new Dimension(0, 8)));
// ———————————————————————UNDO / REDO
        JPanel undoRedoPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        undoRedoPanel.setOpaque(false);
        undoRedoPanel.setMaximumSize(new Dimension(150, 40));

        JButton undoButton = new JButton("◀");
        undoButton.setFont(baseFont.deriveFont(9f));
        undoButton.setForeground(TEXT_COL);
        undoButton.setBackground(new Color(235, 225, 205));
        undoButton.setFocusPainted(false);
        undoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton redoButton = new JButton("▶");
        redoButton.setFont(baseFont.deriveFont(9f));
        redoButton.setForeground(TEXT_COL);
        redoButton.setBackground(new Color(235, 225, 205));
        redoButton.setFocusPainted(false);
        redoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        undoButton.addActionListener(e -> {
            if (controller.undo()) {
                canvasPanel.repaint();
            }
        });

        redoButton.addActionListener(e -> {
            if (controller.redo()) {
                canvasPanel.repaint();
            }
        });
        undoRedoPanel.add(undoButton);
        undoRedoPanel.add(redoButton);
        add(undoRedoPanel);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }
    private void showSizeChooser() {
        SpinnerNumberModel widthModel = new SpinnerNumberModel(controller.getColsCount(), 17, 68, 1);
        SpinnerNumberModel heightModel = new SpinnerNumberModel(controller.getRowsCount(), 17, 68, 1);

        JSpinner widthSpinner = new JSpinner(widthModel);
        JSpinner heightSpinner = new JSpinner(heightModel);
        JButton defaultButton = new JButton("17 x 17 за замовчуванням");

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 10));

        defaultButton.addActionListener(e -> {
            widthSpinner.setValue(17);
            heightSpinner.setValue(17);
        });

        panel.add(new JLabel("Ширина (стовпці 17-68):"));
        panel.add(widthSpinner);
        panel.add(new JLabel("Висота (рядки 17-68):"));
        panel.add(heightSpinner);
        panel.add(new JLabel(""));
        panel.add(defaultButton);

        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                panel,
                "Оберіть розміри полотна",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int result1 = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Зміна масштабу очистить полотно. Продовжити? \n(попередній малюнок можна буде повернути через \"◀\")",
                    "Підтвердити",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (result1 == JOptionPane.OK_OPTION) {
                int width = (int) widthSpinner.getValue();
                int height = (int) heightSpinner.getValue();
                controller.changeGridSize(height, width);
                canvasPanel.repaint();
            }
        }
    }

    //——————————————————————КНОПКИ ІНСТРУМЕНТІВ
    private JButton createToolButton(String text, Color borderCol, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font.deriveFont(10f));
        btn.setForeground(TEXT_COL);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(borderCol, 2));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(150, 40));
        btn.setPreferredSize(new Dimension(150, 40));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBorder(new LineBorder(new Color(184, 143, 89), 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBorder(new LineBorder(borderCol, 2));
            }
        });

        return btn;
    }
}