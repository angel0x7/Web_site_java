import javax.swing.*;
import java.awt.*;

class AccountPage extends JPanel {
    public AccountPage(User currentUser) {
        setLayout(new BorderLayout());
        add(new JLabel("👤 Mon Compte"), BorderLayout.CENTER);
    }
}