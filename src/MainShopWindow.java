import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainShopWindow extends JFrame {
    private User currentUser;

    public MainShopWindow(User user) {
        this.currentUser = user;

        setTitle("Accueil - Boutique en ligne");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());


        JLabel welcomeLabel = new JLabel("Bienvenue, " + currentUser.getPrenom() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, BorderLayout.CENTER);


        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.addActionListener(e -> {
            new AuthApp().setVisible(true);
            dispose();
        });

        panel.add(logoutButton, BorderLayout.SOUTH);

        add(panel);
    }
}
