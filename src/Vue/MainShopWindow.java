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
    private JPanel detailPanelContainer;

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

        detailPanelContainer = new JPanel(new BorderLayout());
        detailPanelContainer.setVisible(false); // caché par défaut
        detailPanelContainer.setPreferredSize(new Dimension(1000, 700)); // Hauteur adaptée
        add(detailPanelContainer, BorderLayout.SOUTH);


        // Pages communes
        contentPanel.add(new VentesFlashPage(currentUser), "Vente Flash");
        contentPanel.add(new ListeProduitsPage(currentUser), "Tous Produits");

        // Panier
        panierPage = new PanierPage(currentUser);
        contentPanel.add(panierPage, "Panier");

        // Account page
        AccountPage accountPage = new AccountPage(currentUser);
        accountPage.setLoginSuccessListener(userLoggedIn -> {
            this.dispose();
            new MainShopWindow(userLoggedIn).setVisible(true);
        });
        contentPanel.add(accountPage, "Mon Compte");

        // Admin & User pages
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
        JButton btnTousProduits = createStyledButton("Tous produits");
        JButton btnCategories = createStyledButton("Catégories ");
        JButton btnVentesFlash = createStyledButton("Vente Flash");
        JButton btnVentes = createStyledButton("Vendre");
        JButton btnPanier = createStyledButton("Panier");
        btnAdmin = createStyledButton("Admin");
        btnUser = createStyledButton("Mon Espace");

        btnVentesFlash.addActionListener(e -> showPage("Vente Flash"));
        btnVentes.addActionListener(e -> showPage("Vendre"));
        MonCompte.addActionListener(e -> showPage("Mon Compte"));
        btnTousProduits.addActionListener(e -> showPage("Tous Produits"));

        JPopupMenu categoriePopup = new JPopupMenu();
        String[] categories = {"Électronique", "Vêtement", "Vehicule", "Maison"};

        categoriePopup.setBackground(new Color(50, 50, 50));
        categoriePopup.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        for (String cat : categories) {
            JMenuItem item = new JMenuItem(cat);
            item.setFont(new Font("Arial", Font.PLAIN, 16));
            item.setForeground(Color.WHITE);
            item.setBackground(new Color(60, 60, 60));
            item.setOpaque(true);
            item.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

            item.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    item.setBackground(new Color(80, 80, 80));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    item.setBackground(new Color(60, 60, 60));
                }
            });

            item.addActionListener(e -> afficherCategoriesPage(cat));

            categoriePopup.add(item);
        }

        btnCategories.addActionListener(e -> {
            categoriePopup.show(btnCategories, 0, btnCategories.getHeight());
        });

        btnPanier.addActionListener(e -> {
            if (panierPage != null) {
                panierPage.refreshPage();
            }
            showPage("Panier");
        });

        btnAdmin.addActionListener(e -> showPage("Admin"));
        btnUser.addActionListener(e -> showPage("User"));

        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            btnAdmin.setVisible(false);
        }

        if (currentUser == null) {
            btnUser.setVisible(false);
        }

        navBar.add(btnTousProduits);
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

    /**
     * Méthode qui affiche une CategoriesPage avec gestion du clic produit
     */
    private void afficherCategoriesPage(String categorie) {
        CategoriesPage page = new CategoriesPage(currentUser, categorie);
        page.setProductClickListener(produit -> {
            detailPanelContainer.removeAll();
            detailPanelContainer.add(new ProductDetailPanel(produit, currentUser, () -> {
                detailPanelContainer.setVisible(false);
                detailPanelContainer.removeAll();
            }));
            detailPanelContainer.setVisible(true);
            revalidate();
            repaint();
        });

        contentPanel.add(page, "Catégories");
        showPage("Catégories");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainShopWindow(null).setVisible(true));
    }
}
