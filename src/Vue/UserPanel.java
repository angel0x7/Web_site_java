package Vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Dao.*;
import Modele.User;

public class UserPanel extends JPanel {
    private User currentUser;
    private JTable historiqueTable;
    private DefaultTableModel tableModel;

    public UserPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        // Panneau supérieur avec titre et bouton "Rafraîchir historique"
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("👤 Mon compte utilisateur", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshButton = new JButton("🔄 Rafraîchir l'historique");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(new Color(70, 130, 180)); // Couleur agréable pour le bouton
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> refreshPage());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Création du modèle de tableau (colonnes)
        String[] columnNames = {"Produit", "Note (/10)", "Commentaire", "Prix (€)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Empêcher la modification des cellules
            }
        };

        // Initialisation du JTable avec modèle
        historiqueTable = new JTable(tableModel);
        historiqueTable.setRowHeight(30);
        historiqueTable.setFont(new Font("Arial", Font.PLAIN, 14));
        historiqueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        historiqueTable.getTableHeader().setBackground(new Color(200, 200, 200));
        historiqueTable.setFillsViewportHeight(true);
        historiqueTable.setSelectionBackground(new Color(70, 130, 180));
        historiqueTable.setSelectionForeground(Color.WHITE);

        // ScrollPane pour rendre le tableau défilable
        JScrollPane scrollPane = new JScrollPane(historiqueTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // Charger l'historique pour remplir le tableau
        chargerHistorique();
    }

    public void refreshPage() {
        // Effacer les lignes actuelles dans le tableau
        tableModel.setRowCount(0);

        // Recharger les données dans le tableau
        chargerHistorique();

        // Rafraîchir l'affichage graphique
        revalidate();
        repaint();
    }

    private void chargerHistorique() {
        if (currentUser == null) {
            return; // Si non connecté, ne rien afficher (tableau vide)
        }

        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = """
                    SELECT p.nom, a.note, a.description, p.prix
                    FROM avis a
                    JOIN produit p ON a.produit_id = p.id
                    WHERE a.user_id = ?
                    """;
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, currentUser.getId());
                ResultSet rs = stmt.executeQuery();

                // Remplissage des données du tableau
                while (rs.next()) {
                    String produit = rs.getString("nom");
                    int note = rs.getInt("note");
                    String description = rs.getString("description");
                    double prix = rs.getDouble("prix");

                    // Ajouter les données au modèle de tableau
                    tableModel.addRow(new Object[]{produit, note, description, prix});
                }

                if (tableModel.getRowCount() == 0) {
                    // Ajouter une ligne indiquant que l'historique est vide si aucune donnée n'est trouvée
                    tableModel.addRow(new Object[]{"Aucun produit", "-", "Historique vide", "-"});
                }
            }
        } catch (Exception e) {
            // Ajout d'une ligne d'erreur dans le tableau en cas de problème avec la base de données
            tableModel.addRow(new Object[]{"Erreur", "-", "Impossible de charger les données.", "-"});
            e.printStackTrace();
        }
    }
}