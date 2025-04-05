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
    public MainShopWindow() {
        this(null);  // Appelle le constructeur principal avec user = null, principalement lorsque l'on se déconnecte
    }
    public MainShopWindow(User user) {
        this.currentUser = user;
        setTitle("Boutique en ligne");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel top = createTop();// Création première étiquette contenant le logo
        JPanel navBar = createNavBar();// Création 2 eme étiquette contenant les differentes catégories possibles

        JPanel topContainer = new JPanel(new BorderLayout());//Création etiquette mettant le logo en haut et les categories en dessous
        topContainer.add(top, BorderLayout.NORTH);
        topContainer.add(navBar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH); //Ajout de l'etiquette avec logo et categorie en haut de la page
        add(contentPanel, BorderLayout.CENTER);//Ajout de l'etiquette les pages en dessous

        contentPanel.add(new HomePanel(), "home");
        contentPanel.add(new CategoriesPage(), "Catégories");
        contentPanel.add(new VentesFlashPage(), "Vente Flash");
        contentPanel.add(new VentesPage(), "Vendre");
        contentPanel.add(new PanierPage(), "Panier");
        contentPanel.add(new AdminPanel(), "Admin");
        contentPanel.add(new UserPanel(), "User");

        showPage("home");
    }
    private JPanel createTop(){// Panneau en haut de la page d'acceuil avec le logo
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(new Color(30, 30, 30));

        ImageIcon originalIcon = new ImageIcon("Logo.png");
        Image resizedImage = originalIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(resizedImage));

        top.add(logoLabel);
        return top;
    }
    private JPanel createNavBar() {// Panneau en dessous du logo avec tout les catégories possibles
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
        btnPanier.addActionListener(e -> showPage("Panier"));
        btnAdmin.addActionListener(e -> showPage("Admin"));
        btnUser.addActionListener(e -> showPage("User"));
        btnAccount.addActionListener(e -> showAccountOptions());

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

    private JButton createStyledButton(String text) {// Fonction qui définit le style des boutons
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50));
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    private void showPage(String page) {//Fonction qui affiche la page
        cardLayout.show(contentPanel, page);
    }

    private void showAccountOptions() {// Fonction appeler quand on appuie sur la catégorie "MON COMPTE"
        AccountOptionHandler.handle(this, currentUser);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainShopWindow(null).setVisible(true));
    }
}
