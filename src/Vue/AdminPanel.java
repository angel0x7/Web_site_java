package Vue;

import Dao.AdminMarqueDaoImpl;
import Dao.AdminProduitDaoImpl;
import Dao.AdminReductionDaoImpl;
import Dao.DaoFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminPanel extends JPanel {
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private DaoFactory daoFactory;

    public AdminPanel() {
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15)); // Centrage et espacement des boutons
        buttonPanel.setBackground(new Color(30, 30, 30));

        Dimension buttonSize = new Dimension(250, 45); // Definition de la longueur des boutons

        JButton btnMarques = createStyledButton("Gérer les Marques");
        btnMarques.setPreferredSize(buttonSize);

        JButton btnProduits = createStyledButton("Gérer les Produits");
        btnProduits.setPreferredSize(buttonSize);

        JButton btnCategories = createStyledButton("Gérer les Réductions");
        btnCategories.setPreferredSize(buttonSize);

        JButton btnShowTopSelling = createStyledButton("Articles les plus vendus");
        btnShowTopSelling.setPreferredSize(buttonSize);

// Ajout des boutons
        buttonPanel.add(btnMarques);
        buttonPanel.add(btnProduits);
        buttonPanel.add(btnCategories);
        buttonPanel.add(btnShowTopSelling);

        add(buttonPanel, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Color.lightGray);
        add(contentPanel, BorderLayout.CENTER);

        daoFactory = new DaoFactory("jdbc:mysql://localhost:3306/shopping", "root", "");

        AdminMarqueVue marqueVue = new AdminMarqueVue(new AdminMarqueDaoImpl(daoFactory));
        AdminProduitVue produitVue = new AdminProduitVue(new AdminProduitDaoImpl(daoFactory));
        AdminReductionVue reductionVue = new AdminReductionVue(new AdminReductionDaoImpl(daoFactory));
        JPanel topSellingPanel = createTopSellingPanel();

        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(Color.white);
        JLabel label = new JLabel("Veuillez sélectionner une section à gérer.");
        label.setForeground(Color.DARK_GRAY);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        emptyPanel.add(label);

        contentPanel.add(emptyPanel, "vide");
        contentPanel.add(marqueVue, "marques");
        contentPanel.add(produitVue, "produits");
        contentPanel.add(reductionVue, "reductions");
        contentPanel.add(topSellingPanel, "topselling");

        contentLayout.show(contentPanel, "vide");

        // Ajout des action Listener pour une action lors d'un clic sur 1 des 4 boutons
        btnMarques.addActionListener(e -> contentLayout.show(contentPanel, "marques"));
        btnProduits.addActionListener(e -> contentLayout.show(contentPanel, "produits"));
        btnCategories.addActionListener(e -> contentLayout.show(contentPanel, "reductions"));
        btnShowTopSelling.addActionListener(e -> {
            contentPanel.remove(topSellingPanel); // Supprimer l'ancien panneau
            JPanel updatedPanel = createTopSellingPanel(); // Créer le nouveau
            contentPanel.add(updatedPanel, "topselling"); // Reajoute
            contentLayout.show(contentPanel, "topselling");
        });
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

    private JPanel createTopSellingPanel() {
        AdminProduitDaoImpl produitDao = new AdminProduitDaoImpl(daoFactory);
        List<Map.Entry<String, Integer>> topSellingProducts = produitDao.getTopSellingProducts();

        topSellingProducts.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        String[] columnNames = {"Classement", "Produit", "Quantité Vendue"};
        Object[][] data = new Object[topSellingProducts.size()][3];

        for (int i = 0; i < topSellingProducts.size(); i++) {
            Map.Entry<String, Integer> entry = topSellingProducts.get(i);
            data[i][0] = (i + 1);
            data[i][1] = entry.getKey();
            data[i][2] = entry.getValue();
        }

        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.setGridColor(Color.GRAY);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        scrollPane.getViewport().setBackground(Color.white);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        JLabel title = new JLabel("Top 10 des Articles les Plus Vendus");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.setForeground(Color.DARK_GRAY);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
