import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Capabilities extends JPanel {
    private static final Color BUTTON_BG = new Color(202, 238, 248);
    private static final Color BUTTON_TEXT = new Color(74, 59, 59);
    private final Color HOVER_BG = new Color(60, 189, 209);

    public Capabilities(JPanel mainContainer, CardLayout cardLayout, PatternPanel patternPanel, Font baseFont, DrawPanel drawPanel) {

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Font menuFont = baseFont.deriveFont(14f);

        String[] menuItems = {"Новий","Редагувати", "Зберегти", "Вставити"};

        for (String menuItem : menuItems) {
            JButton btn = statButton(menuItem, menuFont);

            btn.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    if (menuItem.equals("Зберегти")) {
                        ImageSaver.savePanelAsPNG(patternPanel, drawPanel.getController());
                    }else if (menuItem.equals("Новий")) {
                        cardLayout.show(mainContainer, "DRAW");
                    } else if (menuItem.equals("Вставити")) {
                        int[][] loadedData = ImageSaver.loadPanelFromPNG(Capabilities.this);
                        if (loadedData != null) {
                            drawPanel.loadGridData(loadedData);
                            cardLayout.show(mainContainer, "DRAW");
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(HOVER_BG);
                    btn.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(BUTTON_BG);
                    btn.setForeground(BUTTON_TEXT);
                }

            });
            add(btn);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    public static JButton statButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_BG);

        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.setFocusPainted(false);
        button.setBorder(new LineBorder(BUTTON_TEXT, 3));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setPreferredSize(new Dimension(200, 45));

        return button;
    }
}