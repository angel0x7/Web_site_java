package Controleur;

import Dao.JdbcDataSource;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;
import Vue.PanierPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class PanierController {
    private PanierPage vue;
    private User currentUser;
    private List<Produit> produitsPanier;
    private List<Integer> quantitesPanier;

    public PanierController(PanierPage vue, User currentUser) {
        this.vue = vue;
        this.currentUser = currentUser;
        this.produitsPanier = vue.getProduitsPanier();
        this.quantitesPanier = vue.getQuantitesPanier();
    }

    public void chargerProduitsDuPanier() {
        produitsPanier.clear();
        quantitesPanier.clear();
        vue.getProduitsPanel().removeAll();

        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = """
                SELECT ep.produit_id, p.nom, p.prix, ep.quantite
                FROM element_panier ep
                JOIN produit p ON ep.produit_id = p.id
                WHERE ep.panier_id = ?
            """;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, currentUser.getPanierId());
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
            JOptionPane.showMessageDialog(vue, "Erreur lors du chargement du panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherProduits() {
        JPanel produitsPanel = vue.getProduitsPanel();
        produitsPanel.removeAll();

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit produit = produitsPanier.get(i);
            int quantite = quantitesPanier.get(i);
            int finalI = i;

            JPanel produitPanel = new JPanel(new BorderLayout());
            produitPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            produitPanel.setBackground(Color.WHITE);
            produitPanel.setPreferredSize(new Dimension(600, 120));
            produitPanel.setMaximumSize(new Dimension(600, 120));

            JPanel infoPanel = new JPanel(new GridLayout(3, 1));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel(produit.getNomProduit());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(nameLabel);

            JLabel priceLabel = new JLabel("Prix unitaire : " + produit.getPrix() + "€");
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            priceLabel.setForeground(Color.GRAY);
            infoPanel.add(priceLabel);

            Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());
            if (reduction != null) {
                JLabel reductionLabel = new JLabel(String.format("Offre : %d pour %.2f €",
                        reduction.getQuantite_vrac(), reduction.getPrix_vrac()));
                reductionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                reductionLabel.setForeground(Color.RED);
                infoPanel.add(reductionLabel);
            }

            produitPanel.add(infoPanel, BorderLayout.WEST);

            JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            quantitePanel.setBackground(Color.WHITE);

            JButton decrementButton = new JButton("-");
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
                    JOptionPane.showMessageDialog(vue, "Erreur lors de la suppression du produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            actionPanel.add(removeButton);
            produitPanel.add(actionPanel, BorderLayout.EAST);

            double prixTotalParProduit;
            if (reduction != null && quantite >= reduction.getQuantite_vrac()) {
                int lotCount = quantite / reduction.getQuantite_vrac();
                int remainingItems = quantite % reduction.getQuantite_vrac();
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
        vue.getTotalLabel().setText("Total : " + calculerTotal() + "€");
    }

    public void passerCommande() {
        if (produitsPanier.isEmpty()) {
            JOptionPane.showMessageDialog(vue, "Votre panier est vide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = JdbcDataSource.getConnection()) {
            connection.setAutoCommit(false);

            int nouveauPanierId = -1;
            String insertPanierQuery = "INSERT INTO panier (utilisateur_id, taille) VALUES (?, ?)";
            try (PreparedStatement insertPanierStmt = connection.prepareStatement(insertPanierQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertPanierStmt.setInt(1, currentUser.getId());
                insertPanierStmt.setInt(2, produitsPanier.size());
                insertPanierStmt.executeUpdate();

                ResultSet generatedKeys = insertPanierStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    nouveauPanierId = generatedKeys.getInt(1);
                }
            }

            if (nouveauPanierId == -1) {
                throw new SQLException("Erreur lors de la création du nouveau panier.");
            }

            String insertElementPanierQuery = """
            INSERT INTO element_panier (panier_id, produit_id, quantite)
            VALUES (?, ?, ?)
        """;

            try (PreparedStatement insertElementStmt = connection.prepareStatement(insertElementPanierQuery)) {
                for (int i = 0; i < produitsPanier.size(); i++) {
                    Produit produit = produitsPanier.get(i);
                    int quantite = quantitesPanier.get(i);
                    insertElementStmt.setInt(1, nouveauPanierId);
                    insertElementStmt.setInt(2, produit.getIdProduit());
                    insertElementStmt.setInt(3, quantite);
                    insertElementStmt.addBatch();
                }
                insertElementStmt.executeBatch();
            }

            connection.commit();

            produitsPanier.clear();
            quantitesPanier.clear();
            vue.getProduitsPanel().removeAll();
            vue.getTotalLabel().setText("Total : 0€");
            vue.getProduitsPanel().repaint();

            JOptionPane.showMessageDialog(vue, "Commande passée avec succès avec un nouvel ID !", "Succès", JOptionPane.INFORMATION_MESSAGE);

            if (vue.getUserPanel() != null) {
                vue.getUserPanel().refreshPage();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue, "Erreur lors du passage de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculerTotal() {
        double total = 0;

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit produit = produitsPanier.get(i);
            int quantite = quantitesPanier.get(i);
            Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());

            if (reduction != null && quantite >= reduction.getQuantite_vrac()) {
                int lotCount = quantite / reduction.getQuantite_vrac();
                total += lotCount * reduction.getPrix_vrac();
                int remainingItems = quantite % reduction.getQuantite_vrac();
                total += remainingItems * produit.getPrix();
            } else {
                total += quantite * produit.getPrix();
            }
        }

        return total;
    }

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

    public void afficherMessageUtilisateurNonConnecte() {
        JPanel produitsPanel = vue.getProduitsPanel();
        produitsPanel.removeAll();
        JLabel message = new JLabel("Veuillez vous connecter pour accéder à votre panier.", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.ITALIC, 18));
        message.setForeground(Color.RED);
        produitsPanel.add(message);
        produitsPanel.revalidate();
        produitsPanel.repaint();
    }
}
