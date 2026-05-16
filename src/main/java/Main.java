import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Вишиванка");
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Dimension screenSize = toolkit.getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));

        Font customFont;
        try {
            File fontFile = new File("src/fonts/PressStart2P-Regular.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(58f);
        } catch (Exception e) {
            customFont = new Font("Serif", Font.PLAIN, 58);
        }

        PatternPanel panel = new PatternPanel();

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

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}