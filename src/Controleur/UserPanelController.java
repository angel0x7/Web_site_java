package Controleur;

import Dao.JdbcDataSource;
import Modele.User;
import Vue.UserPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class UserPanelController {
    private User user;
    private UserPanel panel;

    public UserPanelController(User user, UserPanel panel) {
        this.user = user;
        this.panel = panel;
    }

    public void refreshPage() {
        chargerHistorique();
    }

    private void chargerHistorique() {
        try (Connection conn = JdbcDataSource.getConnection()) {
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
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = panel.getTableModel();
            model.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("commande_id");
                String produit = rs.getString("produit");
                double prix = rs.getDouble("prix_total");

                model.addRow(new Object[]{
                        "Commande " + id,
                        produit,
                        String.format("%.2f €", prix)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void afficherDetailsCommande() {
        JTable table = panel.getHistoriqueTable();
        DefaultTableModel model = panel.getTableModel();

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "Veuillez sélectionner une commande.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String commandeIdStr = model.getValueAt(selectedRow, 0).toString();
        int commandeId = Integer.parseInt(commandeIdStr.replace("Commande ", ""));

        JDialog dialog = new JDialog(panel.getParentFrame(), "Détails de la commande", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(panel);

        JLabel title = new JLabel("Détails de la commande #" + commandeId, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        dialog.add(title, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        try (Connection conn = JdbcDataSource.getConnection()) {
            String query = """
                SELECT prod.nom AS produit, ep.quantite, 
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
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            double total = 0;

            while (rs.next()) {
                String produit = rs.getString("produit");
                int quantite = rs.getInt("quantite");
                double prixUnitaire = rs.getDouble("prix_unitaire");
                String reductionNom = rs.getString("reduction_nom");
                double sousTotal = rs.getDouble("sous_total");

                if (reductionNom != null) {
                    sb.append(String.format("%s (x%d) - %.2f €/unité - Réduction : %s => Sous-total : %.2f €\n",
                            produit, quantite, prixUnitaire, reductionNom, sousTotal));
                } else {
                    sb.append(String.format("%s (x%d) - %.2f €/unité => Sous-total : %.2f €\n",
                            produit, quantite, prixUnitaire, sousTotal));
                }
                total += sousTotal;
            }

            sb.append("\n-----------------------------\n");
            sb.append(String.format("Prix total de la commande : %.2f €", total));
            area.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors de la récupération des détails.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(new JScrollPane(area), BorderLayout.CENTER);
        JButton close = new JButton("Fermer");
        close.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(close);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void ajouterAvis() {
        JTable table = panel.getHistoriqueTable();
        DefaultTableModel model = panel.getTableModel();

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(panel, "Veuillez sélectionner un produit pour ajouter un avis.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String produitNom = (String) model.getValueAt(row, 1);
        int produitId = getProductIdByName(produitNom);
        if (produitId == -1) {
            JOptionPane.showMessageDialog(panel, "Produit non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField titreField = new JTextField();
        JTextField noteField = new JTextField();
        JTextArea descArea = new JTextArea(5, 20);
        JPanel avisPanel = new JPanel(new GridLayout(0, 2));
        avisPanel.add(new JLabel("Titre :")); avisPanel.add(titreField);
        avisPanel.add(new JLabel("Note (1-5) :")); avisPanel.add(noteField);
        avisPanel.add(new JLabel("Description :")); avisPanel.add(new JScrollPane(descArea));

        int result = JOptionPane.showConfirmDialog(panel, avisPanel, "Ajouter un avis", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String titre = titreField.getText();
            int note;
            try {
                note = Integer.parseInt(noteField.getText());
                if (note < 1 || note > 5) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(panel, "Note invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String desc = descArea.getText();
            try (Connection conn = JdbcDataSource.getConnection()) {
                String insert = "INSERT INTO avis (titre, note, description, produit_id, user_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insert);
                stmt.setString(1, titre);
                stmt.setInt(2, note);
                stmt.setString(3, desc);
                stmt.setInt(4, produitId);
                stmt.setInt(5, user.getId());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Avis ajouté.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Erreur lors de l'ajout.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getProductIdByName(String nom) {
        try (Connection conn = JdbcDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM produit WHERE nom = ?");
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
