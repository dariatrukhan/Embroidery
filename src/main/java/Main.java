import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Вишиванка");
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Dimension screenSize = toolkit.getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Font customFont;
        try {
            File fontFile = new File("src/fonts/PressStart2P-Regular.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(58f);
        } catch (Exception e) {
            customFont = new Font("Serif", Font.PLAIN, 58);
        }
        CardLayout cardLayout = new CardLayout();
        JPanel mainContainer = new JPanel(cardLayout);

        JPanel mainPage = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 15));
        topPanel.setBorder(new EmptyBorder(15, 240, 0, 0));

        DrawPanel drawPanel = new DrawPanel(mainContainer, cardLayout, customFont);
        PatternPanel panel = new PatternPanel();
        Capabilities menuPanel = new Capabilities(mainContainer, cardLayout, panel, customFont, drawPanel );
        menuPanel.setBorder(new EmptyBorder(50, 20, 20, 20));

        frame.setLayout(new BorderLayout());

        frame.add(menuPanel, BorderLayout.WEST);
        String name = "Дар'я";

        for (int i = 0; i < name.length(); i++) {
            String letter = String.valueOf(name.charAt(i));
            JLabel letterLabel = new JLabel(letter);
            letterLabel.setFont(customFont);
            letterLabel.setForeground(new Color(60, 189, 209));

            final int index = i;

            letterLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    letterLabel.setForeground(new Color(255, 100, 100));
                    panel.highlightSector(index);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    letterLabel.setForeground(new Color(60, 189, 209));
                    panel.highlightSector(-1);
                }
            });

            topPanel.add(letterLabel);
        }
        mainPage.add(menuPanel, BorderLayout.WEST);
        mainPage.add(topPanel, BorderLayout.NORTH);
        mainPage.add(panel, BorderLayout.CENTER);

        mainContainer.add(mainPage, "MAIN");
        mainContainer.add(drawPanel, "DRAW");

        frame.add(mainContainer);

        cardLayout.show(mainContainer, "MAIN");

        frame.setVisible(true);
    }
}