import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainPanel frame = new MainPanel();
            frame.setVisible(true);
        });
    }
}