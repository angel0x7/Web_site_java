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
    private PanierPage panierPage; // Référence explicite à PanierPage

    public MainShopWindow() {
        this(null); // Appeler le constructeur principal sans utilisateur
    }

    public MainShopWindow(User user) {
        this.currentUser = user;
        setTitle("Boutique en ligne");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel top = createTop(); // Barre supérieure avec le logo.
        JPanel navBar = createNavBar(); // Barre de navigation.

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(top, BorderLayout.NORTH);
        topContainer.add(navBar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Créer et ajouter les pages au CardLayout.
        contentPanel.add(new HomePanel(), "home");
        contentPanel.add(new CategoriesPage(), "Catégories");
        contentPanel.add(new VentesFlashPage(currentUser), "Vente Flash");
        contentPanel.add(new VentesPage(), "Vendre");

        // Créer une instance de PanierPage et la stocker dans une variable membre.
        panierPage = new PanierPage(currentUser);
        contentPanel.add(panierPage, "Panier");

        contentPanel.add(new AdminPanel(), "Admin");
        contentPanel.add(new UserPanel(), "User");

        showPage("home"); // Afficher la page d'accueil au démarrage.
    }

    private JPanel createTop() {
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(new Color(30, 30, 30));

        ImageIcon originalIcon = new ImageIcon("Logo.png");
        Image resizedImage = originalIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(resizedImage));

        top.add(logoLabel);
        return top;
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new GridLayout(1, 7));
        navBar.setBackground(new Color(30, 30, 30));

        JButton btnCategories = createStyledButton("Catégories");
        JButton btnVentesFlash = createStyledButton("Vente Flash");
        JButton btnVentes = createStyledButton("Vendre");
        JButton btnAccount = createStyledButton("Mon Compte");
        JButton btnPanier = createStyledButton("Panier");
        btnAdmin = createStyledButton("Admin");
        btnUser = createStyledButton("User");

        btnCategories.addActionListener(e -> showPage("Catégories"));
        btnVentesFlash.addActionListener(e -> showPage("Vente Flash"));
        btnVentes.addActionListener(e -> showPage("Vendre"));
        btnAccount.addActionListener(e -> showAccountOptions());

        // Bouton Panier : Rafraîchir et afficher la page.
        btnPanier.addActionListener(e -> {
            if (panierPage != null) {
                panierPage.refreshPage(); // Rafraîchir les données du panier
            }
            showPage("Panier"); // Afficher la page du panier
        });

        btnAdmin.addActionListener(e -> showPage("Admin"));
        btnUser.addActionListener(e -> showPage("User"));

        // Mise à jour de la visibilité des boutons selon les droits de l'utilisateur.
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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50));
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    private void showAccountOptions() {
        AccountOptionHandler.handle(this, currentUser);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainShopWindow(null).setVisible(true));
    }
}