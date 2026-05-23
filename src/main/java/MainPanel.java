import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MainPanel extends JFrame {
    private Font customFont;

    public MainPanel() {
        super("Вишиванка | Трухан Дарʼя");
        initWindow();
        loadCustomFont();
        buildUI();
    }

    // фрейм
    private void initWindow() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setSize(900, screenSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    // шрифт
    private void loadCustomFont() {
        try {
            File fontFile = new File("src/fonts/PressStart2P-Regular.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(58f);
        } catch (Exception e) {
            customFont = new Font("Serif", Font.PLAIN, 58);
        }
    }

    private void buildUI() {
        CardLayout cardLayout = new CardLayout();
        JPanel mainContainer = new JPanel(cardLayout);

        // головна сторінка
        JPanel mainPage = new JPanel(new BorderLayout());

        // літерами імені
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 15));
        topPanel.setBorder(new EmptyBorder(15, 240, 0, 0));

        DrawPanel drawPanel = new DrawPanel(mainContainer, cardLayout, customFont);
        PatternPanel patternPanel = new PatternPanel();
        Capabilities menuPanel = new Capabilities(mainContainer, cardLayout, patternPanel, customFont, drawPanel);
        menuPanel.setBorder(new EmptyBorder(50, 20, 20, 20));

        String name = "Дар'я";
        for (int i = 0; i < name.length(); i++) {
            JLabel letterLabel = getJLabel(name, i, patternPanel);

            topPanel.add(letterLabel);
        }

        mainPage.add(menuPanel, BorderLayout.WEST);
        mainPage.add(topPanel, BorderLayout.NORTH);
        mainPage.add(patternPanel, BorderLayout.CENTER);

        mainContainer.add(mainPage, "MAIN");
        mainContainer.add(drawPanel, "DRAW");

        add(mainContainer, BorderLayout.CENTER);

        cardLayout.show(mainContainer, "MAIN");
    }

    private JLabel getJLabel(String name, int i, PatternPanel patternPanel) {
        String letter = String.valueOf(name.charAt(i));
        JLabel letterLabel = new JLabel(letter);
        letterLabel.setFont(customFont);
        letterLabel.setForeground(new Color(60, 189, 209));

        final int index = i;

        letterLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                letterLabel.setForeground(new Color(255, 100, 100));
                patternPanel.highlightSector(index);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                letterLabel.setForeground(new Color(60, 189, 209));
                patternPanel.highlightSector(-1);
            }
        });
        return letterLabel;
    }
}
