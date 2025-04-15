package Vue;

import Dao.JdbcDataSource;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentesFlashPage extends JPanel {

    private final User currentUser;
    private JPanel productsPanel;

    /**
     * Constructeur pour la page des ventes flash.
     * @param user L'utilisateur actuellement connecté.
     */
    public VentesFlashPage(User user) {
        this.currentUser = user;

        // Mettre en place la mise en page principale
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Ajouter un titre en haut de la page
        JLabel titleLabel = new JLabel("Ventes Flash", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // Panneau principal pour afficher les produits
        productsPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // Grille avec 4 colonnes
        productsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        productsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(productsPanel); // Défilement
        scrollPane.setBorder(null);
        this.add(scrollPane, BorderLayout.CENTER);

        // Charger et afficher les produits en vente flash
        loadAndDisplayProducts();
    }

    /**
     * Charge et affiche les produits ayant une réduction.
     */
    private void loadAndDisplayProducts() {
        // Effacer les produits affichés précédemment
        productsPanel.removeAll();

        // Récupérer les produits avec réduction depuis la base de données
        List<Produit> produitsAvecReduction = getProduitsAvecReduction();

        // Ajouter chaque produit comme une carte
        for (Produit produit : produitsAvecReduction) {
            productsPanel.add(createProductCard(produit));
        }

        // Rafraîchir l'affichage
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    /**
     * Méthode pour créer une carte produit avec image, nom, prix et un bouton d'achat.
     * @param produit Le produit à afficher.
     * @return JPanel représentant une carte produit.
     */
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 300));

        // Image du produit
        String imagePath = produit.getImagePath();
        JLabel productImage = new JLabel();
        productImage.setHorizontalAlignment(SwingConstants.CENTER);
        productImage.setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon productIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH));
            productImage.setIcon(productIcon); // Ajouter l'image redimensionnée
        } catch (Exception e) {
            productImage.setText("Image introuvable");
        }
        card.add(productImage, BorderLayout.CENTER);

        // Section des détails : nom + prix + bouton
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
        productPrice.setForeground(new Color(255, 69, 0)); // Couleur rouge pour souligner la réduction
        productPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPrice.setHorizontalAlignment(SwingConstants.CENTER);
        Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());
        detailsPanel.add(productPrice);

        if (reduction != null) {
            JLabel reductionLabel = new JLabel(
                    String.format("Offre : %d pour %.2f €", reduction.getQuantite_vrac(), reduction.getPrix_vrac())
            );
            reductionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            productPrice.setHorizontalAlignment(SwingConstants.CENTER);
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

    /**
     * Méthode pour récupérer les produits ayant une réduction depuis la base de données.
     * @return Une liste des produits avec une réduction.
     */
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


    /**
     * Gère l'ajout d'un produit au panier.
     * @param produit Le produit à ajouter au panier.
     */
    private void handleAddToCart(Produit produit) {
        // Ajouter ici la logique pour ajouter un produit au panier en base de données
        try (Connection conn = JdbcDataSource.getConnection()) {
            String query = "INSERT INTO element_panier (produit_id, panier_id, quantite) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);

            int panierId = currentUser.getPanierId();
            if (panierId == -1) {
                JOptionPane.showMessageDialog(this, "Erreur : Panier non initialisé pour l'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            statement.setInt(1, produit.getIdProduit());
            statement.setInt(2, panierId);
            statement.setInt(3, 1); // Ajout d'une seule unité du produit

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Le produit a été ajouté au panier avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du produit au panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}