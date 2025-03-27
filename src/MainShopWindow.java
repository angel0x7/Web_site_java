import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainShopWindow extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainShopWindow(User user) {
        this.currentUser = user;

        setTitle("Boutique en ligne");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);


        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);


        contentPanel.add(new JLabel("Accueil"), "home");
        contentPanel.add(new JLabel("Catégories"), "Catégories");
        contentPanel.add(new JLabel("Vente Flash"), "Vente Flash");
        contentPanel.add(new JLabel("Vendre"), "Vendre");
        contentPanel.add(new JLabel("Panier"), "Panier");
        contentPanel.add(new JLabel("Admin"), "Admin");

        showPage("home");
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new GridLayout(1, 6));

        JButton btnCategories = new JButton("Catégories");
        JButton btnVentesFlash = new JButton("Vente Flash");
        JButton btnVentes = new JButton("Vendre");
        JButton btnAccount = new JButton("Mon Compte");
        JButton btnPanier = new JButton("Panier");
        JButton btnAdmin = new JButton("Admin");

        btnCategories.addActionListener(e -> showPage("Catégories"));
        btnVentesFlash.addActionListener(e -> showPage("Vente Flash"));
        btnVentes.addActionListener(e -> showPage("Vendre"));
        btnPanier.addActionListener(e -> showPage("Panier"));
        btnAdmin.addActionListener(e -> showPage("Admin"));

        btnAccount.addActionListener(e -> openAuthApp());

        navBar.add(btnCategories);
        navBar.add(btnVentesFlash);
        navBar.add(btnVentes);
        navBar.add(btnAccount);
        navBar.add(btnPanier);
        navBar.add(btnAdmin);

        return navBar;
    }

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    private void openAuthApp() {
        AuthApp authApp = new AuthApp();
        authApp.setVisible(true);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        JOptionPane.showMessageDialog(this, "Connecté en tant que: " + user.getNom());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainShopWindow(null).setVisible(true);
        });
    }
}
