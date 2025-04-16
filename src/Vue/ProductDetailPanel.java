package Vue;

import Modele.Produit;
import Modele.User;
import Dao.JdbcDataSource;
import Dao.PanierDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ProductDetailPanel extends JPanel {

    public ProductDetailPanel(Produit produit, User user, Runnable onClose) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setBackground(Color.WHITE);

        // Image produit
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(produit.getImagePath()).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            imageLabel.setText("Image indisponible");
        }

        // Texte à droite
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(produit.getNomProduit());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("Prix : " + produit.getPrix() + " €");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        priceLabel.setForeground(new Color(0, 123, 167));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(nameLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(priceLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Quantité
        JLabel quantityLabel = new JLabel("Quantité :");
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComboBox<Integer> quantityCombo = new JComboBox<>();
        for (int i = 1; i <= 10; i++) quantityCombo.addItem(i);
        quantityCombo.setMaximumSize(new Dimension(100, 30));
        quantityCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(quantityLabel);
        rightPanel.add(quantityCombo);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Boutons
        JButton addButton = new JButton("Ajouter au panier");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(new Color(0, 128, 255));
        addButton.setForeground(Color.WHITE);

        addButton.addActionListener(e -> {
            int selectedQuantity = (Integer) quantityCombo.getSelectedItem();
            try (Connection connection = JdbcDataSource.getConnection()) {
                if (user == null) {
                    JOptionPane.showMessageDialog(this, "Veuillez vous connecter.", "Non connecté", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                PanierDAO panierDAO = new PanierDAO(connection);
                int panierId = panierDAO.getOrCreatePanier(user.getId());
                panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), selectedQuantity);
                panierDAO.updatePanierTaille(panierId);

                JOptionPane.showMessageDialog(this, selectedQuantity + " x " + produit.getNomProduit() + " ajouté(s) au panier !");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout au panier", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Fermer");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> onClose.run());

        rightPanel.add(addButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(closeButton);

        add(imageLabel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
}
