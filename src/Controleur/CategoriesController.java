package Controleur;

import Dao.AdminMarqueDaoImpl;
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
                "SELECT * FROM produit WHERE nom LIKE ? OR category LIKE ?  ";

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
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(parent, "Veuillez vous connecter pour ajouter un article au panier.", "Utilisateur non connecté", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ("ADMIN".equals(currentUser.getRole())) {
                JOptionPane.showMessageDialog(parent, "Les administrateurs ne peuvent pas ajouter d'articles au panier.", "Action non autorisée", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("CLIENT".equals(currentUser.getRole())) {
                Connection connection = JdbcDataSource.getConnection();
                PanierDAO panierDAO = new PanierDAO(connection);

                int panierId = panierDAO.getOrCreatePanier(currentUser.getId());
                panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), 1);
                panierDAO.updatePanierTaille(panierId);

                JOptionPane.showMessageDialog(parent, "Article ajouté : " + produit.getNomProduit(), "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Erreur lors de l'ajout au panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            JdbcDataSource.closeConnection();
        }
    }
}
