package Vue;

import Dao.JdbcDataSource;
import Dao.PanierDAO;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.User;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VentesFlashPage extends JPanel {

    private final User currentUser;

    public VentesFlashPage(User user) {
        this.currentUser = user;

        // Configuration du layout principal
        setLayout(new BorderLayout());
        setBackground(Color.decode("#f8f9fa"));

        // Titre principal
        JLabel title = new JLabel("Ventes Flash", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.decode("#333333"));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Conteneur pour les cartes produits
        JPanel productGrid = new JPanel();
        productGrid.setLayout(new GridLayout(0, 4, 15, 15)); // Grille dynamique, 4 colonnes
        productGrid.setBackground(Color.decode("#f8f9fa"));

        // Récupération des produits
        ProduitDAO produitDAO = new ProduitDAO();
        List<Produit> produits = produitDAO.getAllProduits();

        // Création des cartes produit
        for (Produit produit : produits) {
            JPanel productCard = createProductCard(produit);
            productGrid.add(productCard);
        }

        // Ajout du conteneur dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Défilement fluide
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Méthode pour créer une "carte produit" avec un bouton fonctionnel.
     */
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 150));

        // Détails du produit
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(2, 1));
        detailsPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(produit.getNomProduit(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(nameLabel);

        JLabel priceLabel = new JLabel(produit.getPrix() + " €", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(Color.RED);
        detailsPanel.add(priceLabel);

        card.add(detailsPanel, BorderLayout.CENTER);

        // Bouton "Ajouter au Panier"
        JButton panierButton = new JButton("Ajouter au Panier");
        panierButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panierButton.setBackground(new Color(40, 167, 69)); // Vert moderne
        panierButton.setForeground(Color.WHITE);
        panierButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panierButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Événement au clic sur le bouton
        panierButton.addActionListener(e -> handleAddToCart(produit));

        card.add(panierButton, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Gère l'ajout d'un produit au panier.
     */
    private void handleAddToCart(Produit produit) {
        try {
            // Vérification de l'utilisateur connecté
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez vous connecter pour ajouter un article au panier.",
                        "Utilisateur non connecté",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Connexion à la base de données
            Connection connection = JdbcDataSource.getConnection();
            PanierDAO panierDAO = new PanierDAO(connection);

            // Vérifier ou créer un panier pour l'utilisateur
            int panierId = panierDAO.getOrCreatePanier(currentUser.getId());

            // Ajouter ou mettre à jour le produit dans le panier
            panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), 1);

            // Mise à jour de la taille du panier
            panierDAO.updatePanierTaille(panierId);

            // Confirmation à l'utilisateur
            JOptionPane.showMessageDialog(this,
                    "Article ajouté : " + produit.getNomProduit(),
                    "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout au panier.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // Fermer la connexion
            JdbcDataSource.closeConnection();
        }
    }
}