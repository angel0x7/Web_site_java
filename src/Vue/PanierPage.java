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
            final int currentIndex = i;

            // Panneau du produit
            JPanel produitPanel = new JPanel(new BorderLayout());
            produitPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            produitPanel.setBackground(Color.WHITE);
            produitPanel.setPreferredSize(new Dimension(600, 100));
            produitPanel.setMaximumSize(new Dimension(600, 100));

            // Informations sur le produit
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel(produit.getNomProduit());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(nameLabel);

            JLabel priceLabel = new JLabel("Prix : " + produit.getPrix() + "€");
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            priceLabel.setForeground(Color.GRAY);
            infoPanel.add(priceLabel);

            produitPanel.add(infoPanel, BorderLayout.WEST);

            // Gestion des quantités
            JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            quantitePanel.setBackground(Color.WHITE);

            JButton decrementButton = new JButton("-");
            decrementButton.addActionListener(e -> {
                if (quantitesPanier.get(currentIndex) > 1) {
                    quantitesPanier.set(currentIndex, quantitesPanier.get(currentIndex) - 1);
                    afficherProduits();
                }
            });

            JLabel quantiteLabel = new JLabel("Quantité : " + quantite);
            JButton incrementButton = new JButton("+");
            incrementButton.addActionListener(e -> {
                quantitesPanier.set(currentIndex, quantitesPanier.get(currentIndex) + 1);
                afficherProduits();
            });

            quantitePanel.add(decrementButton);
            quantitePanel.add(quantiteLabel);
            quantitePanel.add(incrementButton);
            produitPanel.add(quantitePanel, BorderLayout.CENTER);

            // Bouton Supprimer
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            actionPanel.setBackground(Color.WHITE);
            JButton removeButton = new JButton("Supprimer");
            removeButton.setForeground(Color.RED);
            removeButton.addActionListener(e -> {
                if (supprimerProduitDansDB(produit.getIdProduit())) {
                    produitsPanier.remove(currentIndex);
                    quantitesPanier.remove(currentIndex);
                    afficherProduits();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            actionPanel.add(removeButton);
            produitPanel.add(actionPanel, BorderLayout.EAST);

            produitsPanel.add(produitPanel);
        }

        produitsPanel.revalidate();
        produitsPanel.repaint();

        // Mise à jour du total
        totalLabel.setText("Total : " + calculerTotal() + "€");
    }


    private void passerCommande() {
        if (produitsPanier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Votre panier est vide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Création d'une fenêtre de dialogue pour ajouter des avis
        JDialog avisDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Finalisation de la commande", true);
        avisDialog.setLayout(new BorderLayout());
        avisDialog.setSize(600, 400);
        avisDialog.setLocationRelativeTo(this);

        // Titre
        JLabel titreLabel = new JLabel("Veuillez laisser vos avis avant de valider la commande");
        titreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titreLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        avisDialog.add(titreLabel, BorderLayout.NORTH);

        // Zone défilable pour ajouter des avis
        JPanel avisContainer = new JPanel();
        avisContainer.setLayout(new BoxLayout(avisContainer, BoxLayout.Y_AXIS));
        avisContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Ajouter un panneau distinct pour chaque produit
        for (Produit produit : produitsPanier) {
            JPanel produitPanel = new JPanel(new BorderLayout());
            produitPanel.setBorder(BorderFactory.createTitledBorder(produit.getNomProduit()));

            // Champs pour l'avis
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            inputPanel.add(new JLabel("Titre :"));
            JTextField titreField = new JTextField();
            inputPanel.add(titreField);

            inputPanel.add(new JLabel("Commentaire :"));
            JTextField commentaireField = new JTextField();
            inputPanel.add(commentaireField);

            inputPanel.add(new JLabel("Note (0-10) :"));
            SpinnerNumberModel noteModel = new SpinnerNumberModel(5, 0, 10, 1);
            JSpinner noteSpinner = new JSpinner(noteModel);
            inputPanel.add(noteSpinner);

            produitPanel.add(inputPanel, BorderLayout.CENTER);
            avisContainer.add(produitPanel);

            // Bouton pour enregistrer chaque avis
            JButton saveAvisButton = new JButton("Enregistrer cet avis");
            produitPanel.add(saveAvisButton, BorderLayout.SOUTH);

            saveAvisButton.addActionListener(e -> {
                String titre = titreField.getText();
                String commentaire = commentaireField.getText();
                int note = (int) noteSpinner.getValue();

                // Valider les champs
                if (titre.isEmpty() || commentaire.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Le titre et le commentaire ne peuvent pas être vides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                enregistrerAvis(produit.getIdProduit(), titre, note, commentaire);
                saveAvisButton.setEnabled(false); // Bloquer le bouton une fois l'avis enregistré
                saveAvisButton.setText("Avis enregistré !");
            });
        }

        // Ajouter une zone déroulante
        JScrollPane scrollPane = new JScrollPane(avisContainer);
        avisDialog.add(scrollPane, BorderLayout.CENTER);

        // Bouton Valider la commande
        JButton validerButton = new JButton("Valider la commande");
        validerButton.setFont(new Font("Arial", Font.BOLD, 14));
        validerButton.setBackground(new Color(0, 128, 0));
        validerButton.setForeground(Color.WHITE);
        validerButton.setOpaque(true);

        validerButton.addActionListener(e -> {
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

            if (userPanel != null) {
                userPanel.refreshPage();
            }
            avisDialog.dispose();
        });

        // Ajouter au bas de la fenêtre
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(validerButton);
        avisDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Afficher la fenêtre
        avisDialog.setVisible(true);
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
    private boolean supprimerProduitDansDB(int produitId) {
        // Vérifier si l'utilisateur connecté possède un panier valide
        if (currentUser.getPanierId() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Erreur : Aucun panier associé à l'utilisateur.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        try (Connection connection = JdbcDataSource.getConnection()) {
            // Requête pour supprimer le produit du panier
            String query = "DELETE FROM element_panier WHERE produit_id = ? AND panier_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, produitId); // ID du produit
            statement.setInt(2, currentUser.getPanierId()); // ID panier

            int rowsAffected = statement.executeUpdate();

            // Vérifiez si une ligne a été supprimée
            if (rowsAffected > 0) {
                return true; // Produit supprimé avec succès
            } else {
                JOptionPane.showMessageDialog(this,
                        "Produit non trouvé dans le panier.",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE
                );
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression du produit du panier.",
                    "Erreur SQL",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

}