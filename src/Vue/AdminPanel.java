package Vue;

import Dao.AdminMarqueDaoImpl;
import Dao.AdminProduitDaoImpl;
import Dao.AdminReductionDaoImpl;
import Dao.DaoFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;

public class AdminPanel extends JPanel {
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private DaoFactory daoFactory;

    public AdminPanel() {
        setLayout(new BorderLayout());

        // --- Barre de boutons centrée ---
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Espacement entre les boutons
        gbc.gridy = 0;

        JButton btnMarques = createStyledButton("Gérer les Marques");
        JButton btnProduits = createStyledButton("Gérer les Produits");
        JButton btnCategories = createStyledButton("Gérer les Réductions");
        JButton btnShowTopSelling = createStyledButton("Afficher les articles les plus vendus");

        // Centrage horizontal avec gridx
        gbc.gridx = 0;
        buttonPanel.add(btnMarques, gbc);

        gbc.gridx = 1;
        buttonPanel.add(btnProduits, gbc);

        gbc.gridx = 2;
        buttonPanel.add(btnCategories, gbc);

        gbc.gridx = 3;
        buttonPanel.add(btnShowTopSelling, gbc);

        add(buttonPanel, BorderLayout.NORTH);

        // --- Panel central avec CardLayout ---
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Color.lightGray);
        add(contentPanel, BorderLayout.CENTER);

        // --- DAO Factory ---
        daoFactory = new DaoFactory("jdbc:mysql://localhost:3306/shopping", "root", "");

        // --- Vues spécifiques ---
        AdminMarqueVue marqueVue = new AdminMarqueVue(new AdminMarqueDaoImpl(daoFactory));
        AdminProduitVue produitVue = new AdminProduitVue(new AdminProduitDaoImpl(daoFactory));
        AdminReductionVue reductionVue = new AdminReductionVue(new AdminReductionDaoImpl(daoFactory));

        // --- Panneau vide par défaut ---
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(Color.white);
        JLabel label = new JLabel("Veuillez sélectionner une section à gérer.");
        label.setForeground(Color.DARK_GRAY);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        emptyPanel.add(label);

        // --- Ajout des vues ---
        contentPanel.add(emptyPanel, "vide");
        contentPanel.add(marqueVue, "marques");
        contentPanel.add(produitVue, "produits");
        contentPanel.add(reductionVue, "reductions");

        // --- Affichage initial ---
        contentLayout.show(contentPanel, "vide");

        // --- Gestion des clics ---
        btnMarques.addActionListener(e -> contentLayout.show(contentPanel, "marques"));
        btnProduits.addActionListener(e -> contentLayout.show(contentPanel, "produits"));
        btnCategories.addActionListener(e -> contentLayout.show(contentPanel, "reductions"));
        btnShowTopSelling.addActionListener(e -> showTopSellingProductsTable());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.DARK_GRAY);
        button.setBackground(Color.white);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    private void showTopSellingProductsTable() {
        AdminProduitDaoImpl produitDao = new AdminProduitDaoImpl(daoFactory);
        List<Map.Entry<String, Integer>> topSellingProducts = produitDao.getTopSellingProducts();

        // Trier les produits par quantité vendue dans l'ordre décroissant
        topSellingProducts.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        String[] columnNames = {"Produit", "Quantité Vendue"};
        Object[][] data = new Object[topSellingProducts.size()][2];

        for (int i = 0; i < topSellingProducts.size(); i++) {
            Map.Entry<String, Integer> entry = topSellingProducts.get(i);
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JFrame frame = new JFrame("Top 10 Articles les Mieux Vendus");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
