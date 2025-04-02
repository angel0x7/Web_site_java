import Vue.*;

import javax.swing.*;
import java.awt.*;
import Modele.User;
public class MainShopWindow extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton btnAdmin;
    private JButton btnUser;

    public MainShopWindow(User user) {
        this.currentUser = user;
        setTitle("Boutique en ligne");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Met la fenêtre en plein écran

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Ajouter des panneaux distincts
        contentPanel.add(new HomePanel(), "home");
        contentPanel.add(new CategoriesPage(), "Catégories");
        contentPanel.add(new VentesFlashPage(), "Vente Flash");
        contentPanel.add(new VentesPage(), "Vendre");
        contentPanel.add(new PanierPage(), "Panier");
        contentPanel.add(new AdminPanel(), "Admin");
        contentPanel.add(new UserPanel(), "User"); // Ajout du panneau utilisateur

        showPage("home");
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new GridLayout(1, 7)); // Ajusté pour 7 boutons

        JButton btnCategories = new JButton("Catégories");
        JButton btnVentesFlash = new JButton("Vente Flash");
        JButton btnVentes = new JButton("Vendre");
        JButton btnAccount = new JButton("Mon Compte");
        JButton btnPanier = new JButton("Panier");
        btnAdmin = new JButton("Admin");
        btnUser = new JButton("User");

        btnCategories.addActionListener(e -> showPage("Catégories"));
        btnVentesFlash.addActionListener(e -> showPage("Vente Flash"));
        btnVentes.addActionListener(e -> showPage("Vendre"));
        btnPanier.addActionListener(e -> showPage("Panier"));
        btnAdmin.addActionListener(e -> showPage("Admin"));
        btnUser.addActionListener(e -> showPage("User")); // Correction du nom

        btnAccount.addActionListener(e -> showAccountOptions());

        // Cacher les boutons Admin et User selon le rôle de l'utilisateur
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            btnAdmin.setVisible(false);
        }
        if (currentUser == null) {
            btnUser.setVisible(false);
        }

        navBar.add(btnCategories);
        navBar.add(btnVentesFlash);
        navBar.add(btnVentes);
        navBar.add(btnAccount);
        navBar.add(btnPanier);
        navBar.add(btnAdmin);
        navBar.add(btnUser);

        return navBar;
    }

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    private void showAccountOptions() {
        if (currentUser == null) {
            new AuthApp().setVisible(true);
            dispose();
        } else {
            String[] options = {"Déconnexion", "Voir Profil"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Bonjour, " + currentUser.getNom() + "\nQue voulez-vous faire ?",
                    "Mon Compte",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) { // Déconnexion
                currentUser = null;
                new AuthApp().setVisible(true);
                dispose();
            } else if (choice == 1) { // Voir Profil
                JOptionPane.showMessageDialog(this, "Nom : " + currentUser.getNom() + "\nEmail : " + currentUser.getEmail());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainShopWindow(null).setVisible(true));
    }
}
