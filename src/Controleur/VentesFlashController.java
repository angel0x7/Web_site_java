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



    public List<Produit> getProduitsAvecReduction() {
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



    public void handleAddToCart(Produit produit) {
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
