package Controleur;

import Dao.JdbcDataSource;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;
import Vue.VentesFlashPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentesFlashController {

    private final VentesFlashPage view;
    private final User currentUser;

    public VentesFlashController(VentesFlashPage view, User user) {
        this.view = view;
        this.currentUser = user;
    }

    public void loadAndDisplayProducts() {
        List<Produit> produitsAvecReduction = getProduitsAvecReduction();
        List<JPanel> productCards = new ArrayList<>();

        for (Produit produit : produitsAvecReduction) {
            productCards.add(createProductCard(produit));
        }

        view.afficherProduits(productCards);
    }

    private List<Produit> getProduitsAvecReduction() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT produit.* FROM produit INNER JOIN reduction ON produit.id = reduction.produit_id";

        try (Connection connection = JdbcDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix"),
                        rs.getString("image"),
                        rs.getString("category"),
                        rs.getInt("marque_id")
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produits;
    }

    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 300));

        String imagePath = produit.getImagePath();
        JLabel productImage = new JLabel();
        productImage.setHorizontalAlignment(SwingConstants.CENTER);
        productImage.setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon productIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH));
            productImage.setIcon(productIcon);
        } catch (Exception e) {
            productImage.setText("Image introuvable");
        }
        card.add(productImage, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JLabel productName = new JLabel(produit.getNomProduit());
        productName.setFont(new Font("Arial", Font.BOLD, 14));
        productName.setAlignmentX(Component.CENTER_ALIGNMENT);
        productName.setHorizontalAlignment(SwingConstants.CENTER);
        detailsPanel.add(productName);

        JLabel productPrice = new JLabel(String.format("Prix : %.2f €", produit.getPrix()));
        productPrice.setFont(new Font("Arial", Font.PLAIN, 14));
        productPrice.setForeground(new Color(0, 123, 167));
        productPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPrice.setHorizontalAlignment(SwingConstants.CENTER);
        detailsPanel.add(productPrice);

        Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());
        if (reduction != null) {
            JLabel reductionLabel = new JLabel(
                    String.format("Offre : %d pour %.2f €", reduction.getQuantite_vrac(), reduction.getPrix_vrac())
            );
            reductionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            reductionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            reductionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            reductionLabel.setForeground(Color.RED);
            detailsPanel.add(reductionLabel);
        }

        JButton addToCartButton = new JButton("Ajouter au panier");
        addToCartButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addToCartButton.setBackground(new Color(0, 128, 255));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartButton.addActionListener(e -> handleAddToCart(produit));
        detailsPanel.add(addToCartButton);

        card.add(detailsPanel, BorderLayout.SOUTH);
        return card;
    }

    private void handleAddToCart(Produit produit) {
        try (Connection conn = JdbcDataSource.getConnection()) {
            String query = "INSERT INTO element_panier (produit_id, panier_id, quantite) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);

            int panierId = currentUser.getPanierId();
            if (panierId == -1) {
                JOptionPane.showMessageDialog(view, "Erreur : Panier non initialisé pour l'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            statement.setInt(1, produit.getIdProduit());
            statement.setInt(2, panierId);
            statement.setInt(3, 1);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(view, "Le produit a été ajouté au panier avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Erreur lors de l'ajout du produit au panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
