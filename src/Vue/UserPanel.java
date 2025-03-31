package Vue;

import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel {
    public UserPanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("👤 Mon compte utilisateur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea userInfo = new JTextArea("📌 Informations utilisateur...\n\n🔑 Modifier mot de passe\n🛒 Historique des achats\n📧 Changer email\n❌ Supprimer le compte");
        userInfo.setEditable(false);

        add(titleLabel, BorderLayout.NORTH);
        add(userInfo, BorderLayout.CENTER);
    }
}
