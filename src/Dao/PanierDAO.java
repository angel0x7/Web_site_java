package Dao;

import java.sql.*;

public class PanierDAO {

    private Connection connection;

    public PanierDAO(Connection connection) {
        this.connection =  JdbcDataSource.getConnection();


    }

    public int getOrCreatePanier(int utilisateurId) throws SQLException {
        // Vérifie si un panier existe déjà
        String selectQuery = "SELECT id FROM panier WHERE utilisateur_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // Crée un nouveau panier si aucun n'existe
        String insertQuery = "INSERT INTO panier (utilisateur_id, taille) VALUES (?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, utilisateurId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Échec de la création du panier");
    }

    public void addOrUpdateElementPanier(int panierId, int produitId, int quantite) throws SQLException {
        // Vérifie si le produit est déjà dans le panier
        String selectQuery = "SELECT id, quantite FROM element_panier WHERE panier_id = ? AND produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setInt(1, panierId);
            stmt.setInt(2, produitId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Produit déjà présent, mise à jour
                int nouvelleQuantite = rs.getInt("quantite") + quantite;
                String updateQuery = "UPDATE element_panier SET quantite = ? WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, nouvelleQuantite);
                    updateStmt.setInt(2, rs.getInt("id"));
                    updateStmt.executeUpdate();
                }
            } else {
                // Produit non présent, insertion
                String insertQuery = "INSERT INTO element_panier (quantite, produit_id, panier_id) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, quantite);
                    insertStmt.setInt(2, produitId);
                    insertStmt.setInt(3, panierId);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    public void updatePanierTaille(int panierId) throws SQLException {
        String updateQuery = "UPDATE panier SET taille = (SELECT SUM(quantite) FROM element_panier WHERE panier_id = ?) WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setInt(1, panierId);
            stmt.setInt(2, panierId);
            stmt.executeUpdate();
        }
    }
}