import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainShopWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainShopWindow(User currentUser) {
        setTitle("Shopping App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Ajouter les pages
        mainPanel.add(new CategoriesPage(), "Categories");
        mainPanel.add(new FlashSalesPage(), "Vente Flash");
        mainPanel.add(new SellPage(), "Vente");
        mainPanel.add(new AccountPage(currentUser), "account");
        mainPanel.add(new PanierPage(), "Panier");
        mainPanel.add(new AdminPage(), "admin");

        // Barre de navigation
        JPanel navBar = createNavBar();

        // Layout principal
        setLayout(new BorderLayout());
        add(navBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton btnCategories = new JButton("Catégories");
        JButton btnFlashSale = new JButton("Vente Flash");
        JButton btnSell = new JButton("Vendre");
        JButton btnAccount = new JButton("Mon Compte");
        JButton btnCart = new JButton("Panier");
        JButton btnAdmin = new JButton("Admin");

        // Actions des boutons
        btnCategories.addActionListener(e -> cardLayout.show(mainPanel, "categories"));
        btnFlashSale.addActionListener(e -> cardLayout.show(mainPanel, "flash_sale"));
        btnSell.addActionListener(e -> cardLayout.show(mainPanel, "sell"));
        btnAccount.addActionListener(e -> cardLayout.show(mainPanel, "account"));
        btnCart.addActionListener(e -> cardLayout.show(mainPanel, "cart"));
        btnAdmin.addActionListener(e -> cardLayout.show(mainPanel, "admin"));

        // Ajouter les boutons à la barre de navigation
        navBar.add(btnCategories);
        navBar.add(btnFlashSale);
        navBar.add(btnSell);
        navBar.add(btnAccount);
        navBar.add(btnCart);
        navBar.add(btnAdmin);

        return navBar;
    }
}



