package Dao;

import Modele.Produit;
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
}