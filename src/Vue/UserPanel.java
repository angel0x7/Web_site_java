package Vue;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Dao.*;
import Modele.User;

public class UserPanel extends JPanel {
    private User currentUser;

    public UserPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("👤 Mon compte utilisateur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel historiquePanel = new JPanel();
        historiquePanel.setLayout(new BoxLayout(historiquePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(historiquePanel);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Rafraîchir l'historique");
        refreshButton.addActionListener(e -> refreshPage()); // Actualiser le panneau au clic
        add(refreshButton, BorderLayout.SOUTH);

        chargerHistorique(historiquePanel);
    }
    public void refreshPage() {
        // On recrée le panneau d'historique
        removeAll();

        // Réinitialisation de l'affichage
        JLabel titleLabel = new JLabel("👤 Mon compte utilisateur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel historiquePanel = new JPanel();
        historiquePanel.setLayout(new BoxLayout(historiquePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(historiquePanel);
        add(scrollPane, BorderLayout.CENTER);

        // Recharger les données de l'historique dans le panneau
        chargerHistorique(historiquePanel);

        // Actualiser l'interface graphique
        revalidate();
        repaint();
    }

    private void chargerHistorique(JPanel panel) {
        if (currentUser == null) {
            panel.add(new JLabel("Vous devez être connecté pour voir votre historique."));
            return;
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

                while (rs.next()) {
                    String produit = rs.getString("nom");
                    int note = rs.getInt("note");
                    String description = rs.getString("description");
                    double prix = rs.getDouble("prix");

                    JPanel commandePanel = new JPanel(new GridLayout(3, 1));
                    commandePanel.add(new JLabel("Produit : " + produit));
                    commandePanel.add(new JLabel("Note : " + note + "/10"));
                    commandePanel.add(new JLabel("Commentaire : " + description));
                    commandePanel.add(new JLabel("Prix : " + prix + "€"));

                    commandePanel.setBorder(BorderFactory.createTitledBorder("Commande"));
                    panel.add(commandePanel);
                }
            }
        } catch (Exception e) {
            panel.add(new JLabel("Erreur lors du chargement de l'historique."));
            e.printStackTrace();
        }
        panel.revalidate();
        panel.repaint();
    }
}