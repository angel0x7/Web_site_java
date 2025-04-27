package Controleur;

import Dao.JdbcDataSource;
import Dao.PanierDAO;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesController {
    private User currentUser;

    public CategoriesController(User user) {
        this.currentUser = user;
    }

    public List<Produit> fetchProducts(String keyword) {
        List<Produit> produits = new ArrayList<>();
        boolean fetchAll = keyword == null || keyword.trim().isEmpty() || keyword.equalsIgnoreCase("tout");

        String query = fetchAll ?
                "SELECT * FROM produit" :
                "SELECT * FROM produit WHERE nom LIKE ? OR category LIKE ?";

        try (Connection connection = JdbcDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if (!fetchAll) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public Reduction getReduction(int produitId) {
        return ProduitDAO.getReductionByProduitId(produitId);
    }

    public void handleAddToCart(JComponent parent, Produit produit) {
        try (Connection conn = JdbcDataSource.getConnection()) {
            // Vérification de l'utilisateur
            if (currentUser == null) {
                JOptionPane.showMessageDialog(parent, "Veuillez vous connecter pour ajouter un article au panier.", "Utilisateur non connecté", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Récupération de l'ID du panier
            int panierId = currentUser.getPanierId();
            if (panierId == -1) {
                JOptionPane.showMessageDialog(parent, "Erreur : Panier non initialisé pour l'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Préparation de la requête SQL
            String query = "INSERT INTO element_panier (produit_id, panier_id, quantite) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, produit.getIdProduit());
            statement.setInt(2, panierId);
            statement.setInt(3, 1);

            // Exécution de la requête
            statement.executeUpdate();
            JOptionPane.showMessageDialog(parent, "Le produit a été ajouté au panier avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Erreur lors de l'ajout du produit au panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

}
