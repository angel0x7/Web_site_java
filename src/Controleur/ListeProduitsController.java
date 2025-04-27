package Controleur;

import Dao.JdbcDataSource;
import Dao.PanierDAO;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;
import Vue.ListeProduitsPage;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListeProduitsController {

    private ListeProduitsPage vue;
    private User currentUser;

    public ListeProduitsController(ListeProduitsPage vue, User user) {
        this.vue = vue;
        this.currentUser = user;
    }

    public void loadAndDisplayProducts(String keyword) {
        List<Produit> produits = fetchProducts(keyword);
        vue.displayProducts(produits);
    }

    public Reduction getReductionForProduct(int produitId) {
        return ProduitDAO.getReductionByProduitId(produitId);
    }

    public void handleAddToCart(Produit produit) {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(vue,
                        "Veuillez vous connecter pour ajouter un article au panier.",
                        "Utilisateur non connecté",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Vérification si l'utilisateur est un administrateur
            if (currentUser.getRole().equals("ADMIN")) {
                JOptionPane.showMessageDialog(vue,
                        "Les administrateurs ne peuvent pas ajouter d'articles au panier.",
                        "Action non autorisée",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection connection = JdbcDataSource.getConnection();
            PanierDAO panierDAO = new PanierDAO(connection);

            int panierId = panierDAO.getOrCreatePanier(currentUser.getId());
            panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), 1);
            panierDAO.updatePanierTaille(panierId);

            JOptionPane.showMessageDialog(vue,
                    "Article ajouté : " + produit.getNomProduit(),
                    "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                    "Erreur lors de l'ajout au panier.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            JdbcDataSource.closeConnection();
        }
    }


    private List<Produit> fetchProducts(String keyword) {
        List<Produit> produits = new ArrayList<>();
        boolean fetchAll = keyword == null || keyword.trim().isEmpty() || keyword.equalsIgnoreCase("tout");

        String query = fetchAll
                ? "SELECT * FROM produit"
                : "SELECT * FROM produit WHERE nom LIKE ? OR category LIKE ?";

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
}