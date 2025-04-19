package Vue;

import Modele.Produit;
import Modele.User;
import Controleur.ProductDetailController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ProductDetailPanel extends JPanel {

    private final JComboBox<Integer> quantityCombo = new JComboBox<>();
    private final JButton addButton = createStyledButton("Ajouter au panier", new Color(0, 153, 76));
    private final JButton closeButton = createStyledButton("Fermer", new Color(200, 50, 50));

    public ProductDetailPanel(Produit produit, User user, Runnable onClose) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 4, true));
        setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(produit.getImagePath()).getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            imageLabel.setText("Image indisponible");
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(produit.getNomProduit());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 26));
        nameLabel.setForeground(new Color(30, 30, 30));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("Prix : " + produit.getPrix() + " €");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        priceLabel.setForeground(new Color(0, 123, 255));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(produit.getDescription());
        descArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(new TitledBorder("Description"));
        descArea.setMaximumSize(new Dimension(500, 120));

        JLabel quantityLabel = new JLabel("Quantité :");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 1; i <= 10; i++) quantityCombo.addItem(i);
        quantityCombo.setMaximumSize(new Dimension(100, 30));
        quantityCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityCombo.setBackground(Color.WHITE);
        quantityCombo.setFont(new Font("Arial", Font.PLAIN, 14));

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

        add(imageLabel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Ajout des contrôleurs
        ProductDetailController controller = new ProductDetailController(produit, user, this, onClose);
        addButton.addActionListener(controller.getAddToCartListener());
        closeButton.addActionListener(e -> onClose.run());
    }

    public int getSelectedQuantity() {
        return (Integer) quantityCombo.getSelectedItem();
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
