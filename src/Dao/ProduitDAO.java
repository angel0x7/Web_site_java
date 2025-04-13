package Dao;

import Modele.Produit;
import Modele.Reduction;

import java.sql.*;
import java.util.*;

public class ProduitDAO {

    public static List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";

        try (Connection con = JdbcDataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix"),
                        rs.getString("image"),
                        rs.getString("category"),
                        rs.getInt("marque_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    public static Reduction getReductionByProduitId(int produitId) {
        String sql = "SELECT * FROM reduction WHERE produit_id = ?";
        Reduction reduction = null;

        try (Connection con = JdbcDataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                reduction = new Reduction(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("quantite_vrac"),
                        rs.getDouble("prix_vrac"),
                        rs.getInt("produit_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reduction;
    }

}