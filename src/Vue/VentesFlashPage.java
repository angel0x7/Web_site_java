package Vue;

import Dao.JdbcDataSource;
import Modele.Produit;
import Modele.User;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VentesFlashPage extends JPanel {

    private final User currentUser;

    /**
     * Constructeur pour la page des ventes flash.
     * @param user L'utilisateur actuellement connecté.
     */
    public VentesFlashPage(User user) {
        this.currentUser = user;

        // Configuration du layout principal
        this.setLayout(new BorderLayout());
        JLabel title = new JLabel("Ventes Flash : Produits en réduction", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(title, BorderLayout.NORTH);

        // Récupérer les produits avec réduction
        List<Produit> produitsAvecReduc = getProduitsAvecReduction();

        // Affichage des produits dans un panneau en grille
        JPanel produitsPanel = new JPanel();
        produitsPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 colonnes, espace entre les produits
        produitsPanel.setBorder(new LineBorder(Color.GRAY, 1, true));

        if (produitsAvecReduc.isEmpty()) {
            // Aucun produit en réduction
            JLabel noProductsLabel = new JLabel("Aucun produit en réduction pour le moment.");
            noProductsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noProductsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            produitsPanel.add(noProductsLabel);
        } else {
            // Affichage des produits avec réduction
            for (Produit produit : produitsAvecReduc) {
                produitsPanel.add(createProductCard(produit));
            }
        }

        JScrollPane scrollPane = new JScrollPane(produitsPanel);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Méthode pour récupérer les produits ayant une réduction.
     * @return Une liste des produits qui ont une entrée dans la table `reduction`.
     */
    private List<Produit> getProduitsAvecReduction() {
        List<Produit> produits = new ArrayList<>();
        try (Connection conn = JdbcDataSource.getConnection()) {
            String query = """
                SELECT p.id, p.nom, p.description, p.prix, p.quantite, p.image, p.category, p.marque_id
                FROM produit p
                JOIN reduction r ON r.produit_id = p.id
            """;

            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Produit produit = new Produit(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("description"),
                        resultSet.getInt("quantite"),
                        resultSet.getDouble("prix"),
                        resultSet.getString("image"),
                        resultSet.getString("category"),
                        resultSet.getInt("marque_id")
                );
                produits.add(produit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des produits en réduction.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return produits;
    }

    /**
     * Méthode pour créer une "carte produit" avec un bouton fonctionnel.
     * @param produit Le produit à afficher.
     * @return JPanel contenant les informations du produit.
     */
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setPreferredSize(new Dimension(200, 300));

        // Nom du produit
        JLabel productName = new JLabel(produit.getNomProduit());
        productName.setHorizontalAlignment(SwingConstants.CENTER);
        productName.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(productName, BorderLayout.NORTH);

        // Image simulée ou nom du produit
        JLabel productImage = new JLabel(produit.getImage(), SwingConstants.CENTER);
        productImage.setBorder(new LineBorder(Color.BLACK, 1, true));
        productImage.setPreferredSize(new Dimension(200, 150));
        card.add(productImage, BorderLayout.CENTER);

        // Prix avec possibilité de réduction
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        JLabel priceLabel = new JLabel(String.format("Prix : %.2f €", produit.getPrix()), JLabel.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bottomPanel.add(priceLabel);

        // Bouton pour ajouter au panier
        JButton addToCartButton = new JButton("Ajouter au panier");
        addToCartButton.addActionListener(e -> handleAddToCart(produit));
        bottomPanel.add(addToCartButton);

        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
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