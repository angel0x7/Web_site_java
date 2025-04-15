package Vue;

import Dao.AdminMarqueDaoImpl;
import Dao.AdminProduitDaoImpl;
import Dao.AdminReductionDaoImpl;
import Dao.DaoFactory;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private CardLayout contentLayout;
    private JPanel contentPanel;

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

        // Centrage horizontal avec gridx
        gbc.gridx = 0;
        buttonPanel.add(btnMarques, gbc);

        gbc.gridx = 1;
        buttonPanel.add(btnProduits, gbc);

        gbc.gridx = 2;
        buttonPanel.add(btnCategories, gbc);

        add(buttonPanel, BorderLayout.NORTH);

        // --- Panel central avec CardLayout ---
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Color.lightGray);
        add(contentPanel, BorderLayout.CENTER);

        // --- DAO Factory ---
        DaoFactory daoFactory = new DaoFactory("jdbc:mysql://localhost:3306/shopping", "root", "");

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
}
