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

    public PanierPage(User user) {
        this.currentUser = user;
        this.produitsPanier = new ArrayList<>();
        this.quantitesPanier = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel title = new JLabel("🛒 Votre panier", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Panneau pour afficher les produits du panier
        produitsPanel = new JPanel();
        produitsPanel.setLayout(new BoxLayout(produitsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(produitsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Panneau pour le total global et le bouton "Commander"
        JPanel footerPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Total : 0€", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        footerPanel.add(totalLabel, BorderLayout.NORTH);

        JButton commanderButton = new JButton("Commander");
        commanderButton.setBackground(new Color(0, 123, 255));
        commanderButton.setForeground(Color.WHITE);
        commanderButton.setFocusPainted(false);
        commanderButton.addActionListener(e -> {
            double total = calculerTotal();
            JOptionPane.showMessageDialog(this,
                    "Montant payé : " + total + "€",
                    "Confirmation de commande",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        footerPanel.add(commanderButton, BorderLayout.SOUTH);
        add(footerPanel, BorderLayout.SOUTH);

        // Charger les produits dans le panier
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
                    int idProduit = rs.getInt("id");
                    String nomProduit = rs.getString("nom");
                    double prix = rs.getDouble("prix");
                    int quantite = rs.getInt("quantite");

                    produitsPanier.add(new Produit(idProduit, nomProduit, "", quantite, prix, "", "", 0));
                    quantitesPanier.add(quantite);
                }
            }
            afficherProduits();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Une erreur s'est produite lors du chargement du panier.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherMessageUtilisateurNonConnecte() {
        produitsPanel.removeAll();
        JLabel message = new JLabel("Vous devez être connecté pour voir votre panier.", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.ITALIC, 18));
        produitsPanel.add(message);
        produitsPanel.revalidate();
        produitsPanel.repaint();
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

            JButton minusButton = new JButton("-");
            minusButton.addActionListener(e -> modifierQuantite(produit.getIdProduit(), -1));
            row.add(minusButton);

            JLabel quantiteLabel = new JLabel(String.valueOf(quantite));
            quantiteLabel.setPreferredSize(new Dimension(30, 30));
            row.add(quantiteLabel);

            JButton plusButton = new JButton("+");
            plusButton.addActionListener(e -> modifierQuantite(produit.getIdProduit(), 1));
            row.add(plusButton);

            JButton deleteButton = new JButton("Supprimer");
            deleteButton.addActionListener(e -> supprimerProduit(produit.getIdProduit()));
            row.add(deleteButton);

            produitsPanel.add(row);
        }

        produitsPanel.revalidate();
        produitsPanel.repaint();

        totalLabel.setText("Total : " + calculerTotal() + "€");
    }

    private void modifierQuantite(int produitId, int modification) {
        for (int i = 0; i < produitsPanier.size(); i++) {
            if (produitsPanier.get(i).getIdProduit() == produitId) {
                int nouvelleQuantite = quantitesPanier.get(i) + modification;
                if (nouvelleQuantite > 0) {
                    quantitesPanier.set(i, nouvelleQuantite);
                    try (Connection connection = JdbcDataSource.getConnection()) {
                        String query = "UPDATE element_panier SET quantite = ? WHERE produit_id = ?";
                        try (PreparedStatement stmt = connection.prepareStatement(query)) {
                            stmt.setInt(1, nouvelleQuantite);
                            stmt.setInt(2, produitId);
                            stmt.executeUpdate();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    supprimerProduit(produitId); // Si quantité 0, supprimer le produit
                }
                refreshPage();
                return;
            }
        }
    }

    private void supprimerProduit(int produitId) {
        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = "DELETE FROM element_panier WHERE produit_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, produitId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        refreshPage();
    }

    private double calculerTotal() {
        double total = 0;
        for (int i = 0; i < produitsPanier.size(); i++) {
            total += produitsPanier.get(i).getPrix() * quantitesPanier.get(i);
        }
        return total;
    }
}