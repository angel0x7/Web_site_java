package Vue;

import Dao.*;
import Modele.Produit;
import Modele.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PanierPage extends JPanel {
    private User currentUser;
    private List<Produit> produitsPanier;
    private List<Integer> quantitesPanier;
    private JLabel totalLabel;
    private JPanel produitsPanel;

    private UserPanel userPanel; // Référence vers le panneau UserPanel

    public PanierPage(User user) {
        this.currentUser = user;
        this.userPanel = userPanel; // Initialisation du panneau utilisateur
        this.produitsPanier = new ArrayList<>();
        this.quantitesPanier = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel title = new JLabel("🛒 Votre panier", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Panneau des produits
        produitsPanel = new JPanel();
        produitsPanel.setLayout(new BoxLayout(produitsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(produitsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Panneau inférieur
        JPanel footerPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Total : 0€", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        footerPanel.add(totalLabel, BorderLayout.NORTH);

        JButton commanderButton = new JButton("Commander");
        commanderButton.setBackground(new Color(0, 123, 255));
        commanderButton.setForeground(Color.WHITE);
        commanderButton.addActionListener(e -> passerCommande()); // Mise à jour ici
        footerPanel.add(commanderButton, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);

        if (currentUser != null) {
            chargerProduitsDuPanier();
        } else {
            afficherMessageUtilisateurNonConnecte();
        }
    }

    public void refreshPage() {
        if (currentUser != null) {
            chargerProduitsDuPanier();
        } else {
            afficherMessageUtilisateurNonConnecte();
        }
    }

    private void chargerProduitsDuPanier() {
        produitsPanier.clear();
        quantitesPanier.clear();
        produitsPanel.removeAll();

        try (Connection connection = JdbcDataSource.getConnection()) {
            PanierDAO panierDAO = new PanierDAO(connection);
            int panierId = panierDAO.getOrCreatePanier(currentUser.getId());

            String query = """
                    SELECT p.id, p.nom, p.prix, ep.quantite
                    FROM element_panier ep
                    JOIN produit p ON ep.produit_id = p.id
                    WHERE ep.panier_id = ?
                    """;

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, panierId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    double prix = rs.getDouble("prix");
                    int quantite = rs.getInt("quantite");

                    Produit produit = new Produit(id, nom, "", quantite, prix, "", "", 0);
                    produitsPanier.add(produit);
                    quantitesPanier.add(quantite);
                }
            }
            afficherProduits();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement du panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherProduits() {
        produitsPanel.removeAll();

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit produit = produitsPanier.get(i);
            int quantite = quantitesPanier.get(i);

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBorder(new EmptyBorder(5, 5, 5, 5));

            JLabel nameLabel = new JLabel(produit.getNomProduit());
            nameLabel.setPreferredSize(new Dimension(150, 30));
            row.add(nameLabel);

            JLabel priceLabel = new JLabel(produit.getPrix() + "€");
            priceLabel.setPreferredSize(new Dimension(100, 30));
            row.add(priceLabel);

            JLabel totalProduitLabel = new JLabel("Total : " + (produit.getPrix() * quantite) + "€");
            totalProduitLabel.setPreferredSize(new Dimension(150, 30));
            row.add(totalProduitLabel);

            produitsPanel.add(row);
        }

        produitsPanel.revalidate();
        produitsPanel.repaint();

        totalLabel.setText("Total : " + calculerTotal() + "€");
    }

    private void passerCommande() {
        if (produitsPanier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Votre panier est vide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Enregistrer des avis pour chaque produit
        for (Produit produit : produitsPanier) {
            JLabel produitLabel = new JLabel("Produit : " + produit.getNomProduit());
            JTextField titreField = new JTextField(); // Champ pour le titre
            JTextField commentaireField = new JTextField(); // Champ pour le commentaire
            SpinnerNumberModel noteModel = new SpinnerNumberModel(5, 0, 10, 1);
            JSpinner noteSpinner = new JSpinner(noteModel);

            JPanel panel = new JPanel(new GridLayout(5, 1));
            panel.add(produitLabel);
            panel.add(new JLabel("Titre de l'avis :"));
            panel.add(titreField);
            panel.add(new JLabel("Commentaire :"));
            panel.add(commentaireField);
            panel.add(new JLabel("Note (sur 10) :"));
            panel.add(noteSpinner);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Laisser un avis pour " + produit.getNomProduit(),
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                String titre = titreField.getText();
                String commentaire = commentaireField.getText();
                int note = (int) noteSpinner.getValue();

                if (titre.isEmpty() || commentaire.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Le titre et le commentaire ne peuvent pas être vides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                } else {
                    enregistrerAvis(produit.getIdProduit(), titre, note, commentaire);
                }
            }
        }

        // Confirmation
        JOptionPane.showMessageDialog(this,
                "Merci pour votre commande !",
                "Commande validée",
                JOptionPane.INFORMATION_MESSAGE);

        // Vider le panier
        produitsPanier.clear();
        quantitesPanier.clear();
        produitsPanel.removeAll();
        totalLabel.setText("Total : 0€");
        produitsPanel.repaint();

        // Rafraîchir le UserPanel si disponible
        if (userPanel != null) {
            userPanel.refreshPage();
        }
    }

    private void enregistrerAvis(int produitId, String titre, int note, String commentaire) {
        try (Connection connection = JdbcDataSource.getConnection()) {
            // Requête d’insertion
            String query = "INSERT INTO avis (produit_id, user_id, titre, note, description) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                // Définir les paramètres
                stmt.setInt(1, produitId);                  // ID du produit
                stmt.setInt(2, currentUser.getId());        // ID de l'utilisateur
                stmt.setString(3, titre);                  // Titre de l'avis
                stmt.setInt(4, note);                      // Note
                stmt.setString(5, commentaire);            // Commentaire

                // Exécuter la requête
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Gestion d'erreur lors de l'insertion
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement de l'avis pour le produit ID " + produitId + ".",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void afficherMessageUtilisateurNonConnecte() {
        // Supprimer les produits affichés s'il y en a
        produitsPanel.removeAll();

        // Création d'un message utilisateur non connecté
        JLabel message = new JLabel("Veuillez vous connecter pour accéder à votre panier.", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.ITALIC, 18));
        message.setForeground(Color.RED);

        // Ajouter le message au panel
        produitsPanel.add(message);

        // Réinitialiser et rafraîchir l'affichage
        produitsPanel.revalidate();
        produitsPanel.repaint();
    }

    private double calculerTotal() {
        double total = 0;
        for (int i = 0; i < produitsPanier.size(); i++) {
            total += produitsPanier.get(i).getPrix() * quantitesPanier.get(i);
        }
        return total;
    }
}