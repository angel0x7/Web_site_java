package Vue;

import javax.swing.*;
import java.awt.*;
import Modele.User;

public class MainShopWindow extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton btnAdmin;
    private JButton btnUser;
    private PanierPage panierPage;

    public MainShopWindow() {
        this(null);
    }

    public MainShopWindow(User user) {
        this.currentUser = user;
        setTitle("Boutique en ligne");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel top = createTop();
        JPanel navBar = createNavBar();

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(top, BorderLayout.NORTH);
        topContainer.add(navBar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Pages communes
        contentPanel.add(new HomePanel(), "home");
        contentPanel.add(new CategoriesPage(currentUser), "Catégories");
        contentPanel.add(new VentesFlashPage(currentUser), "Vente Flash");
        contentPanel.add(new VentesPage(), "Vendre");

        // Panier
        panierPage = new PanierPage(currentUser);
        contentPanel.add(panierPage, "Panier");

        // Ajout de AccountPage avec gestion du listener de succès
        AccountPage accountPage = new AccountPage(currentUser);
        accountPage.setLoginSuccessListener(userLoggedIn -> {
            // Recrée complètement la fenêtre avec le nouvel utilisateur
            this.dispose();
            new MainShopWindow(userLoggedIn).setVisible(true);
        });
        contentPanel.add(accountPage, "Mon Compte");

        // Autres pages conditionnelles
        contentPanel.add(new AdminPanel(), "Admin");
        contentPanel.add(new UserPanel(currentUser), "User");

        showPage("home");
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
        JPanel navBar = new JPanel(new GridLayout(1, 7));
        navBar.setBackground(new Color(30, 30, 30));

        JButton MonCompte = createStyledButton("Mon Compte");
        JButton btnCategories = createStyledButton("Catégories");
        JButton btnVentesFlash = createStyledButton("Vente Flash");
        JButton btnVentes = createStyledButton("Vendre");
        JButton btnPanier = createStyledButton("Panier");
        btnAdmin = createStyledButton("Admin");
        btnUser = createStyledButton("User");

        btnCategories.addActionListener(e -> showPage("Catégories"));
        btnVentesFlash.addActionListener(e -> showPage("Vente Flash"));
        btnVentes.addActionListener(e -> showPage("Vendre"));
        MonCompte.addActionListener(e -> showPage("Mon Compte"));

        btnPanier.addActionListener(e -> {
            if (panierPage != null) {
                panierPage.refreshPage();
            }
            showPage("Panier");
        });

        btnAdmin.addActionListener(e -> showPage("Admin"));
        btnUser.addActionListener(e -> showPage("User"));

        // Gestion visibilité selon les rôles
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            btnAdmin.setVisible(false);
        }

        if (currentUser == null) {
            btnUser.setVisible(false);
        }

        navBar.add(btnCategories);
        navBar.add(btnVentesFlash);
        navBar.add(btnVentes);
        navBar.add(MonCompte);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainShopWindow(null).setVisible(true));
    }
}