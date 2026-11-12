package eventtracker.gui;

import javax.swing.JPanel;

public abstract class RefreshablePanel extends JPanel {
    protected MainFrame frame;

    public RefreshablePanel(MainFrame frame) {
        this.frame = frame;
    }

    public abstract void refresh();
}
