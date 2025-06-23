package healthybites.view;

import javax.swing.SwingUtilities;

// to safely launch the MainFrame GUI
public class ViewDriver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
