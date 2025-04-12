package Vue;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Dao.JdbcDataSource;
import Modele.User;

public class UserPanel extends JPanel {
    private User currentUser; // L'utilisateur actuellement connecté
    private JTable historiqueTable; // Tableau pour afficher l'historique des commandes
    private DefaultTableModel tableModel; // Modèle pour le tableau
    private JButton avisButton; // Bouton pour ajouter un avis
    private JButton detailsCommandeButton; // Bouton pour afficher les détails de la commande

    public UserPanel(User user) {
        this.currentUser = user;
        this.setLayout(new BorderLayout());

        // Initialisation du modèle de table
        tableModel = new DefaultTableModel(new Object[]{"Commande", "Produits"}, 0);
        historiqueTable = new JTable(tableModel);

        // Rendu personnalisé pour la colonne Produits
        DefaultTableCellRenderer produitsCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea textArea = new JTextArea();
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setText(value != null ? value.toString() : "");
                textArea.setFont(table.getFont());
                textArea.setEditable(false);
                if (isSelected) {
                    textArea.setBackground(table.getSelectionBackground());
                    textArea.setForeground(table.getSelectionForeground());
                } else {
                    textArea.setBackground(table.getBackground());
                    textArea.setForeground(table.getForeground());
                }
                return textArea;
            }
        };
        historiqueTable.getColumnModel().getColumn(1).setCellRenderer(produitsCellRenderer);
        historiqueTable.setRowHeight(50);

        // Ajout d'un scroll pane pour le tableau
        JScrollPane scrollPane = new JScrollPane(historiqueTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // Panneau pour les boutons en bas
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton "Ajouter un Avis"
        avisButton = new JButton("Ajouter un Avis");
        avisButton.addActionListener(e -> ajouterAvis());
        buttonPanel.add(avisButton);

        // Bouton "Détails Commande"
        detailsCommandeButton = new JButton("Détails Commande");
        detailsCommandeButton.addActionListener(e -> afficherDetailsCommande());
        buttonPanel.add(detailsCommandeButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // Chargement initial de l'historique des commandes
        chargerHistorique();
    }

    /**
     * Recharge l'affichage de l'historique des commandes.
     */
    public void refreshPage() {
        chargerHistorique();
    }

    /**
     * Méthode pour charger toutes les commandes de l'utilisateur connecté.
     */
    private void chargerHistorique() {
        try (Connection conn = JdbcDataSource.getConnection()) {
            // Requête pour récupérer les commandes et leurs produits associés
            String query = """
                SELECT p.id AS commande_id, GROUP_CONCAT(prod.nom SEPARATOR ', ') AS produits
                FROM panier p
                LEFT JOIN element_panier ep ON p.id = ep.panier_id
                LEFT JOIN produit prod ON ep.produit_id = prod.id
                WHERE p.utilisateur_id = ?
                GROUP BY p.id;
            """;

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, currentUser.getId()); // remplacer par l'ID de l'utilisateur connecté
            ResultSet resultSet = statement.executeQuery();

            // Réinitialisation des données du tableau
            tableModel.setRowCount(0);

            // Parcourir les résultats de la requête pour construire la table
            while (resultSet.next()) {
                int commandeId = resultSet.getInt("commande_id");
                String produits = resultSet.getString("produits");
                if (produits == null) produits = "Aucun produit trouvé"; // Si aucun produit n'est associé
                tableModel.addRow(new Object[]{"Commande " + commandeId, produits});
            }
        } catch (Exception e) {
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'historique des commandes.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Méthode pour afficher les détails d'une commande sélectionnée.
     */
    private void afficherDetailsCommande() {
        int selectedRow = historiqueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Récupérer l'ID de la commande
        String commandeIdStr = tableModel.getValueAt(selectedRow, 0).toString();
        int commandeId = Integer.parseInt(commandeIdStr.replace("Commande ", ""));

        // Fenêtre affichant les détails
        JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Détails de la commande", true);
        detailsDialog.setLayout(new BorderLayout());
        detailsDialog.setSize(600, 400);
        detailsDialog.setLocationRelativeTo(this);

        // Titre
        JLabel titreLabel = new JLabel("Détails de la commande #" + commandeId);
        titreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsDialog.add(titreLabel, BorderLayout.NORTH);

        // Détails des produits de la commande
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);

        try (Connection conn = JdbcDataSource.getConnection()) {
            String query = """
                SELECT p.nom, ep.quantite, p.prix
                FROM element_panier ep
                JOIN produit p ON ep.produit_id = p.id
                WHERE ep.panier_id = ?
            """;
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, commandeId);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder detailsBuilder = new StringBuilder();
            double total = 0;

            while (resultSet.next()) {
                String produitNom = resultSet.getString("nom");
                int quantite = resultSet.getInt("quantite");
                double prixUnitaire = resultSet.getDouble("prix");
                double sousTotal = quantite * prixUnitaire;

                detailsBuilder.append(String.format("%s (x%d) - %.2f €/unité : %.2f €\n", produitNom, quantite, prixUnitaire, sousTotal));
                total += sousTotal;
            }

            detailsBuilder.append("\n-----------------------------\n");
            detailsBuilder.append(String.format("Prix total de la commande : %.2f €", total));
            detailsArea.setText(detailsBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des détails de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Ajouter les détails (avec scrolling)
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        detailsDialog.add(scrollPane, BorderLayout.CENTER);

        // Bouton pour fermer la fenêtre
        JButton fermerButton = new JButton("Fermer");
        fermerButton.addActionListener(e -> detailsDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(fermerButton);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    /**
     * Méthode pour ajouter un avis (logique existante).
     */
    private void ajouterAvis() {
        // Implémentation pour ajouter un avis
    }
}