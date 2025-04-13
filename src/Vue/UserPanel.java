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
    private User currentUser; // Utilisateur actuellement connecté
    private JTable historiqueTable; // Tableau pour l’historique des commandes
    private DefaultTableModel tableModel; // Modèle pour le tableau
    private JButton avisButton; // Bouton pour ajouter un avis
    private JButton detailsCommandeButton; // Bouton pour afficher les détails d'une commande
    private JButton refreshButton; // Bouton pour rafraîchir les commandes

    /**
     * Constructeur de UserPanel.
     *
     * @param user L'utilisateur actuellement connecté.
     */
    public UserPanel(User user) {
        this.currentUser = user;

        // Définir le layout principal de UserPanel
        this.setLayout(new BorderLayout());

        // Initialiser l'affichage des commandes (table)
        tableModel = new DefaultTableModel(new Object[]{"Commande ID", "Produit", "Prix Total"}, 0);
        historiqueTable = new JTable(tableModel);

        // Personnaliser l'affichage de la table
        historiqueTable.setFillsViewportHeight(true);
        historiqueTable.setRowHeight(25);
        historiqueTable.getTableHeader().setReorderingAllowed(false);

        // Centrer le contenu des colonnes dans la table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < historiqueTable.getColumnCount(); i++) {
            historiqueTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Ajouter le tableau dans un JScrollPane pour le défilement
        JScrollPane scrollPane = new JScrollPane(historiqueTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // Ajouter un panneau pour les boutons d'action (en bas)
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Bouton pour afficher les détails d'une commande
        detailsCommandeButton = new JButton("Détails de la commande");
        detailsCommandeButton.addActionListener(e -> afficherDetailsCommande());
        actionsPanel.add(detailsCommandeButton);

        // Bouton pour ajouter un avis
        avisButton = new JButton("Ajouter un avis");
        avisButton.addActionListener(e -> ajouterAvis());
        actionsPanel.add(avisButton);

        // Bouton pour rafraîchir l'historique des commandes
        refreshButton = new JButton("Rafraîchir");
        refreshButton.addActionListener(e -> refreshPage());
        actionsPanel.add(refreshButton);

        this.add(actionsPanel, BorderLayout.SOUTH);

        // Charger l'historique des commandes lors de l'initialisation
        refreshPage();
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
            // Requête SQL pour récupérer les commandes de l'utilisateur
            String query = """
                SELECT p.id AS commande_id, 
                       prod.nom AS produit,
                       IF(r.quantite_vrac > 0 AND ep.quantite >= r.quantite_vrac, 
                          FLOOR(ep.quantite / r.quantite_vrac) * r.prix_vrac + (ep.quantite % r.quantite_vrac) * prod.prix, 
                          ep.quantite * prod.prix
                       ) AS prix_total
                FROM panier p
                LEFT JOIN element_panier ep ON p.id = ep.panier_id
                LEFT JOIN produit prod ON ep.produit_id = prod.id
                LEFT JOIN reduction r ON prod.id = r.produit_id
                WHERE p.utilisateur_id = ?
            """;

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, currentUser.getId()); // ID de l'utilisateur connecté
            ResultSet resultSet = statement.executeQuery();

            // Réinitialisation des données dans le tableau
            tableModel.setRowCount(0);

            // Ajouter les commandes et produits dans la table
            while (resultSet.next()) {
                int commandeId = resultSet.getInt("commande_id");
                String produit = resultSet.getString("produit");
                double prixTotal = resultSet.getDouble("prix_total");

                tableModel.addRow(new Object[]{
                        "Commande " + commandeId, // Colonne 1 : ID de la commande
                        produit,                  // Colonne 2 : Nom du produit
                        String.format("%.2f €", prixTotal) // Colonne 3 : Prix total formaté
                });
            }
        } catch (Exception e) {
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'historique des commandes.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Méthode pour afficher les détails d'une commande sélectionnée.
     * Affiche les détails des produits commandés et prend en compte les réductions.
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

        // Fenêtre détaillée
        JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Détails de la commande", true);
        detailsDialog.setLayout(new BorderLayout());
        detailsDialog.setSize(600, 400);
        detailsDialog.setLocationRelativeTo(this);

        JLabel titreLabel = new JLabel("Détails de la commande #" + commandeId);
        titreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsDialog.add(titreLabel, BorderLayout.NORTH);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);

        try (Connection conn = JdbcDataSource.getConnection()) {
            String query = """
                SELECT prod.nom AS produit, ep.quantite AS quantite, 
                       prod.prix AS prix_unitaire, 
                       r.nom AS reduction_nom, r.quantite_vrac, r.prix_vrac,
                       IF(r.quantite_vrac > 0 AND ep.quantite >= r.quantite_vrac,
                          FLOOR(ep.quantite / r.quantite_vrac) * r.prix_vrac + (ep.quantite % r.quantite_vrac) * prod.prix,
                          ep.quantite * prod.prix
                       ) AS sous_total
                FROM element_panier ep
                JOIN produit prod ON ep.produit_id = prod.id
                LEFT JOIN reduction r ON prod.id = r.produit_id
                WHERE ep.panier_id = ?
            """;
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, commandeId);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder detailsBuilder = new StringBuilder();
            double total = 0;

            while (resultSet.next()) {
                String produit = resultSet.getString("produit");
                int quantite = resultSet.getInt("quantite");
                double prixUnitaire = resultSet.getDouble("prix_unitaire");
                String reductionNom = resultSet.getString("reduction_nom");
                double sousTotal = resultSet.getDouble("sous_total");

                if (reductionNom != null) {
                    detailsBuilder.append(String.format(
                            "%s (x%d) - %.2f €/unité - Réduction : %s => Sous-total : %.2f €\n",
                            produit, quantite, prixUnitaire, reductionNom, sousTotal
                    ));
                } else {
                    detailsBuilder.append(String.format(
                            "%s (x%d) - %.2f €/unité => Sous-total : %.2f €\n",
                            produit, quantite, prixUnitaire, sousTotal
                    ));
                }
                total += sousTotal;
            }

            detailsBuilder.append("\n-----------------------------\n");
            detailsBuilder.append(String.format("Prix total de la commande : %.2f €", total));
            detailsArea.setText(detailsBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des détails de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        detailsDialog.add(scrollPane, BorderLayout.CENTER);

        JButton fermerButton = new JButton("Fermer");
        fermerButton.addActionListener(e -> detailsDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(fermerButton);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    /**
     * Méthode pour ajouter un avis sur une commande ou un produit.
     */
    private void ajouterAvis() {
        int selectedRow = historiqueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit pour ajouter un avis.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String produitNom = (String) tableModel.getValueAt(selectedRow, 1);
        int produitId = getProductIdByName(produitNom);

        if (produitId == -1) {
            JOptionPane.showMessageDialog(this, "Impossible de trouver le produit sélectionné.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Afficher une boîte de dialogue pour saisir l'avis
        JTextField titreField = new JTextField();
        JTextField noteField = new JTextField();
        JTextArea descriptionArea = new JTextArea(5, 20);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Titre de l'avis :"));
        panel.add(titreField);
        panel.add(new JLabel("Note (1 à 5) :"));
        panel.add(noteField);
        panel.add(new JLabel("Description :"));
        panel.add(new JScrollPane(descriptionArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Ajouter un avis", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String titre = titreField.getText();
            int note;
            try {
                note = Integer.parseInt(noteField.getText());
                if (note < 1 || note > 5) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La note doit être un nombre entre 1 et 5.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String description = descriptionArea.getText();

            // Insérer les données dans la base de données
            try (Connection conn = JdbcDataSource.getConnection()) {
                String insertQuery = """
                    INSERT INTO avis (titre, note, description, produit_id, user_id)
                    VALUES (?, ?, ?, ?, ?)
                """;

                PreparedStatement statement = conn.prepareStatement(insertQuery);
                statement.setString(1, titre);
                statement.setInt(2, note);
                statement.setString(3, description);
                statement.setInt(4, produitId);
                statement.setInt(5, currentUser.getId());
                statement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Votre avis a été ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de votre avis.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Méthode pour récupérer l'ID d'un produit à partir de son nom.
     */
    private int getProductIdByName(String produitNom) {
        try (Connection conn = JdbcDataSource.getConnection()) {
            String query = "SELECT id FROM produit WHERE nom = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, produitNom);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si le produit n'est pas trouvé
    }

}