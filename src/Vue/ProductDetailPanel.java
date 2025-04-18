package Vue;

import Modele.Produit;
import Modele.User;
import Dao.JdbcDataSource;
import Dao.PanierDAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ProductDetailPanel extends JPanel {

    public ProductDetailPanel(Produit produit, User user, Runnable onClose) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setBackground(Color.WHITE);

        // Bordure du panel global
        setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 4, true));

        // Image produit
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(produit.getImagePath()).getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            imageLabel.setText("Image indisponible");
        }

        // Panel à droite
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // Nom
        JLabel nameLabel = new JLabel(produit.getNomProduit());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 26));
        nameLabel.setForeground(new Color(30, 30, 30));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Prix
        JLabel priceLabel = new JLabel("Prix : " + produit.getPrix() + " €");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        priceLabel.setForeground(new Color(0, 123, 255));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JTextArea descArea = new JTextArea(produit.getDescription());
        descArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(new TitledBorder("Description"));
        descArea.setMaximumSize(new Dimension(500, 120));

        // Quantité
        JLabel quantityLabel = new JLabel("Quantité :");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<Integer> quantityCombo = new JComboBox<>();
        for (int i = 1; i <= 10; i++) quantityCombo.addItem(i);
        quantityCombo.setMaximumSize(new Dimension(100, 30));
        quantityCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityCombo.setBackground(Color.WHITE);
        quantityCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        // Boutons
        JButton addButton = createStyledButton("Ajouter au panier", new Color(0, 153, 76));
        JButton closeButton = createStyledButton("Fermer", new Color(200, 50, 50));

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

        closeButton.addActionListener(e -> onClose.run());

        // Section avis (exemple statique)
        JPanel avisPanel = new JPanel();
        avisPanel.setLayout(new BoxLayout(avisPanel, BoxLayout.Y_AXIS));
        avisPanel.setBackground(new Color(245, 245, 245));
        avisPanel.setBorder(BorderFactory.createTitledBorder("Avis clients"));
        avisPanel.setMaximumSize(new Dimension(500, 150));

        List<String> avisList = Arrays.asList(
                "⭐⭐⭐⭐⭐ Excellent produit !",
                "⭐⭐⭐ Bon rapport qualité/prix.",
                "⭐⭐⭐⭐ Très satisfait, je recommande."
        );
        for (String avis : avisList) {
            JLabel avisLabel = new JLabel(avis);
            avisLabel.setFont(new Font("Arial", Font.ITALIC, 13));
            avisLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            avisPanel.add(avisLabel);
        }

        // Ajout au panel droit
        rightPanel.add(nameLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(priceLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(descArea);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(avisPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(quantityLabel);
        rightPanel.add(quantityCombo);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(addButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(closeButton);

        // Ajout à la vue principale
        add(imageLabel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(180, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }
}
