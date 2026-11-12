package eventtracker;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import eventtracker.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        // Coba atur Look and Feel sistem
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Gunakan default Look and Feel jika sistem tidak tersedia
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
