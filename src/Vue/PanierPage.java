package Vue;

import Dao.JdbcDataSource;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
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
    private User currentUser; // Représente l'utilisateur actuellement connecté
    private List<Produit> produitsPanier; // Liste des produits présents dans le panier
    private List<Integer> quantitesPanier; // Quantités associées à chaque produit
    private JLabel totalLabel; // Affiche le total du panier
    private JPanel produitsPanel; // Panel contenant la liste des produits
    private UserPanel userPanel; // Référence pour rafraîchir l'interface de UserPanel

    public PanierPage(User user) {
        this.currentUser = user;
        this.produitsPanier = new ArrayList<>();
        this.quantitesPanier = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre du panneau
        JLabel title = new JLabel("🛒 Votre panier", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Section centrale : Liste des produits
        produitsPanel = new JPanel();
        produitsPanel.setLayout(new BoxLayout(produitsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(produitsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Section inférieure : Total et bouton commander
        JPanel footerPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Total : 0€", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        footerPanel.add(totalLabel, BorderLayout.NORTH);

        JButton commanderButton = new JButton("Commander");
        commanderButton.setBackground(new Color(0, 123, 255));
        commanderButton.setForeground(Color.WHITE);
        commanderButton.addActionListener(e -> passerCommande());
        footerPanel.add(commanderButton, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);

        // Vérifie si l'utilisateur est connecté
        if (currentUser != null) {
            chargerProduitsDuPanier();
        } else {
            afficherMessageUtilisateurNonConnecte();
        }
    }

    /**
     * Rafraîchit l'affichage de l'interface du panier.
     */
    public void refreshPage() {
        if (currentUser != null) {
            chargerProduitsDuPanier();
        } else {
            afficherMessageUtilisateurNonConnecte();
        }
    }

    /**
     * Charge les produits et leurs quantités actuelles depuis la base de données.
     */
    private void chargerProduitsDuPanier() {
        produitsPanier.clear();
        quantitesPanier.clear();
        produitsPanel.removeAll();

        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = """
                SELECT ep.produit_id, p.nom, p.prix, ep.quantite
                FROM element_panier ep
                JOIN produit p ON ep.produit_id = p.id
                WHERE ep.panier_id = ?
            """;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, currentUser.getPanierId()); // Identifiant du panier de l'utilisateur
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("produit_id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");

                Produit produit = new Produit(id, nom, "", quantite, prix, "", "", 0);
                produitsPanier.add(produit);
                quantitesPanier.add(quantite);
            }
            afficherProduits();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement du panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Affiche les produits et leurs informations sur l'interface utilisateur.
     */
    private void afficherProduits() {
        produitsPanel.removeAll();

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit produit = produitsPanier.get(i);
            int quantite = quantitesPanier.get(i);

            JPanel produitPanel = new JPanel(new BorderLayout());
            produitPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            produitPanel.setBackground(Color.WHITE);
            produitPanel.setPreferredSize(new Dimension(600, 120));
            produitPanel.setMaximumSize(new Dimension(600, 120));

            // Informations produit
            JPanel infoPanel = new JPanel(new GridLayout(3, 1)); // Passage à 3 lignes pour inclure les infos sur la réduction
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel(produit.getNomProduit());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(nameLabel);

            JLabel priceLabel = new JLabel("Prix unitaire : " + produit.getPrix() + "€");
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            priceLabel.setForeground(Color.GRAY);
            infoPanel.add(priceLabel);

            // Ajout des informations sur les réductions (si présentes)
            Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());
            if (reduction != null) {
                JLabel reductionLabel = new JLabel(
                        String.format("Offre : %d pour %.2f €", reduction.getQuantite_vrac(), reduction.getPrix_vrac())
                );
                reductionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                reductionLabel.setForeground(Color.RED);
                infoPanel.add(reductionLabel);
            }

            produitPanel.add(infoPanel, BorderLayout.WEST);

            // Gestion des quantités
            JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            quantitePanel.setBackground(Color.WHITE);

            JButton decrementButton = new JButton("-");
            int finalI = i;
            decrementButton.addActionListener(e -> {
                if (quantitesPanier.get(finalI) > 1) {
                    quantitesPanier.set(finalI, quantitesPanier.get(finalI) - 1);
                    afficherProduits();
                }
            });

            JLabel quantiteLabel = new JLabel("Quantité : " + quantite);
            JButton incrementButton = new JButton("+");
            incrementButton.addActionListener(e -> {
                quantitesPanier.set(finalI, quantitesPanier.get(finalI) + 1);
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
                    produitsPanier.remove(finalI);
                    quantitesPanier.remove(finalI);
                    afficherProduits();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            actionPanel.add(removeButton);
            produitPanel.add(actionPanel, BorderLayout.EAST);

            // Calcul du prix avec ou sans réduction
            double prixTotalParProduit;
            if (reduction != null && quantite >= reduction.getQuantite_vrac()) {
                int lotCount = quantite / reduction.getQuantite_vrac(); // Nombre de lots en vrac
                int remainingItems = quantite % reduction.getQuantite_vrac(); // Articles restants

                prixTotalParProduit = (lotCount * reduction.getPrix_vrac()) + (remainingItems * produit.getPrix());
            } else {
                prixTotalParProduit = quantite * produit.getPrix();
            }

            JLabel prixFinalLabel = new JLabel(String.format("Prix total pour ce produit : %.2f €", prixTotalParProduit));
            prixFinalLabel.setFont(new Font("Arial", Font.BOLD, 13));
            prixFinalLabel.setForeground(new Color(34, 139, 34));
            produitPanel.add(prixFinalLabel, BorderLayout.SOUTH);

            produitsPanel.add(produitPanel);
        }

        produitsPanel.revalidate();
        produitsPanel.repaint();
        totalLabel.setText("Total : " + calculerTotal() + "€");
    }

    /**
     * Enregistre une commande dans la base de données à partir des données du panier.
     */
    private void passerCommande() {
        if (produitsPanier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Votre panier est vide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = JdbcDataSource.getConnection()) {
            connection.setAutoCommit(false); // Début d'une transaction

            // Étape 1 : Créer un nouveau panier pour l'utilisateur
            int nouveauPanierId = -1;
            String insertPanierQuery = "INSERT INTO panier (utilisateur_id, taille) VALUES (?, ?)";
            try (PreparedStatement insertPanierStmt = connection.prepareStatement(insertPanierQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertPanierStmt.setInt(1, currentUser.getId()); // ID de l'utilisateur connecté
                insertPanierStmt.setInt(2, produitsPanier.size()); // Taille du panier (nb de produits)
                insertPanierStmt.executeUpdate();

                // Récupérer l'ID du nouveau panier créé
                ResultSet generatedKeys = insertPanierStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    nouveauPanierId = generatedKeys.getInt(1); // ID auto-généré par la base de données
                }
            }

            if (nouveauPanierId == -1) {
                throw new SQLException("Erreur lors de la création du nouveau panier.");
            }

            // Étape 2 : Insérer chaque produit dans la table element_panier avec le nouvel ID de panier
            String insertElementPanierQuery = """
            INSERT INTO element_panier (panier_id, produit_id, quantite)
            VALUES (?, ?, ?)
        """;

            try (PreparedStatement insertElementStmt = connection.prepareStatement(insertElementPanierQuery)) {
                for (int i = 0; i < produitsPanier.size(); i++) {
                    Produit produit = produitsPanier.get(i);
                    int quantite = quantitesPanier.get(i);

                    insertElementStmt.setInt(1, nouveauPanierId); // Associer au nouvel ID de panier
                    insertElementStmt.setInt(2, produit.getIdProduit()); // Produit ID
                    insertElementStmt.setInt(3, quantite); // Quantité du produit
                    insertElementStmt.addBatch(); // Ajout au batch
                }

                insertElementStmt.executeBatch(); // Exécute toutes les commandes dans un batch
            }

            connection.commit(); // Valider la transaction SQL

            // Reset (Vider le panier après la commande)
            produitsPanier.clear();
            quantitesPanier.clear();
            produitsPanel.removeAll();
            totalLabel.setText("Total : 0€");
            produitsPanel.repaint();

            JOptionPane.showMessageDialog(this, "Commande passée avec succès avec un nouvel ID !", "Succès", JOptionPane.INFORMATION_MESSAGE);

            // Rafraîchir la page utilisateur, si applicable
            if (userPanel != null) {
                userPanel.refreshPage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du passage de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calcule le total du panier.
     */
    private double calculerTotal() {
        double total = 0;

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit produit = produitsPanier.get(i);
            int quantite = quantitesPanier.get(i);

            // Récupérer la réduction du produit
            Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());

            if (reduction != null && quantite >= reduction.getQuantite_vrac()) {
                // Calculer le prix pour les lots en vrac
                int lotCount = quantite / reduction.getQuantite_vrac();
                total += lotCount * reduction.getPrix_vrac();

                // Calculer le prix pour les articles restants au tarif unique
                int remainingItems = quantite % reduction.getQuantite_vrac();
                total += remainingItems * produit.getPrix();
            } else {
                // Si pas de réduction ou quantité insuffisante, utiliser le prix normal
                total += quantite * produit.getPrix();
            }
        }

        return total;
    }

    /**
     * Supprime un produit du panier dans la base de données.
     */
    private boolean supprimerProduitDansDB(int produitId) {
        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = "DELETE FROM element_panier WHERE produit_id = ? AND panier_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, produitId);
            statement.setInt(2, currentUser.getPanierId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Affiche un message si l'utilisateur n'est pas connecté.
     */
    private void afficherMessageUtilisateurNonConnecte() {
        produitsPanel.removeAll();
        JLabel message = new JLabel("Veuillez vous connecter pour accéder à votre panier.", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.ITALIC, 18));
        message.setForeground(Color.RED);
        produitsPanel.add(message);
        produitsPanel.revalidate();
        produitsPanel.repaint();
    }
}