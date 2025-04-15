package Vue;

import Dao.JdbcDataSource;
import Dao.PanierDAO;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListeProduitsPage extends JPanel{

    private User currentUser; // L'utilisateur connecté
    private JTextField searchField;
    private JButton searchButton;
    private JPanel productsPanel;

    public ListeProduitsPage(User user) {
        this.currentUser = user;

        // Mise en page de la page principale
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Ajouter une barre de recherche en haut
        JPanel searchPanel = createSearchPanel();
        this.add(searchPanel, BorderLayout.NORTH);

        // Zone d'affichage des produits
        productsPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // Grille avec 4 colonnes
        productsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        productsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(productsPanel); // Défilement des produits
        scrollPane.setBorder(null);
        this.add(scrollPane, BorderLayout.CENTER);

        // Charger et afficher tous les produits au lancement
        loadAndDisplayProducts("");
    }

    /**
     * Crée une barre de recherche avec champ texte et bouton.
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(searchField, BorderLayout.CENTER);

        searchButton = new JButton("Rechercher");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.setPreferredSize(new Dimension(120, 30));
        searchButton.addActionListener(e -> loadAndDisplayProducts(searchField.getText())); // Recherche déclenchée
        searchPanel.add(searchButton, BorderLayout.EAST);

        return searchPanel;
    }

    /**
     * Charge et affiche les produits depuis la base de données selon un mot-clé.
     *
     * @param keyword Mot-clé pour filtrer les produits (par nom ou catégorie).
     */
    private void loadAndDisplayProducts(String keyword) {
        // Effacer les produits affichés précédemment
        productsPanel.removeAll();

        // Récupérer les produits depuis la base de données
        java.util.List<Produit> produits = fetchProducts(keyword);

        // Ajouter chaque produit comme une carte
        for (Produit produit : produits) {
            productsPanel.add(createProductCard(produit));
        }

        // Rafraîchir l'affichage
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    /**
     * Crée une carte graphique pour afficher les détails d'un produit.
     *
     * @param produit Le produit à afficher.
     * @return Un JPanel pour le produit.
     */
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 300));

        // Chargement et affichage de l'image du produit
        String imagePath = produit.getImagePath(); // Chemin de l'image
        JLabel productImage = new JLabel();
        productImage.setHorizontalAlignment(SwingConstants.CENTER);
        productImage.setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon productIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH));
            productImage.setIcon(productIcon); // Ajouter l'image redimensionnée
        } catch (Exception e) {
            productImage.setText("Image introuvable");
        }
        card.add(productImage, BorderLayout.CENTER);

        // Ajouter les détails texte : nom du produit et prix
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JLabel productName = new JLabel(produit.getNomProduit());
        productName.setFont(new Font("Arial", Font.BOLD, 14));
        productName.setAlignmentX(Component.CENTER_ALIGNMENT);
        productName.setHorizontalAlignment(SwingConstants.CENTER);
        detailsPanel.add(productName);

        JLabel productPrice = new JLabel(String.format("Prix : %.2f €", produit.getPrix()));
        productPrice.setFont(new Font("Arial", Font.PLAIN, 14));
        productPrice.setForeground(new Color(0, 123, 167)); // Couleur rouge pour souligner la réduction
        productPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPrice.setHorizontalAlignment(SwingConstants.CENTER);
        Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());
        detailsPanel.add(productPrice);

        if (reduction != null) {
            JLabel reductionLabel = new JLabel(
                    String.format("Offre : %d pour %.2f €", reduction.getQuantite_vrac(), reduction.getPrix_vrac())
            );
            reductionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            reductionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            reductionLabel.setHorizontalAlignment(SwingConstants.CENTER);

            reductionLabel.setForeground(Color.RED);
            detailsPanel.add(reductionLabel);
        }

        // Ajouter un bouton d'achat
        JButton addToCartButton = new JButton("Ajouter au panier");
        addToCartButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addToCartButton.setBackground(new Color(0, 128, 255)); // Couleur bleue
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartButton.addActionListener(e -> handleAddToCart(produit));
        detailsPanel.add(addToCartButton);

        card.add(detailsPanel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Récupère les produits depuis la base de données en fonction d'un mot-clé.
     *
     * @param keyword Mot-clé pour filtrer les produits (peut être vide pour tout afficher).
     * @return Liste des produits récupérés.
     */
    /**
     * Récupère les produits depuis la base de données en fonction d'un mot-clé.
     *
     * @param keyword Mot-clé pour filtrer les produits (peut être vide ou "tout" pour tout afficher).
     * @return Liste des produits récupérés.
     */
    private java.util.List<Produit> fetchProducts(String keyword) {
        List<Produit> produits = new ArrayList<>();

        // Si le mot-clé est "tout", ou vide, on affiche tous les produits
        boolean fetchAll = keyword == null || keyword.trim().isEmpty() || keyword.equalsIgnoreCase("tout");

        String query = fetchAll
                ? "SELECT * FROM produit" // Requête pour tous les produits
                : "SELECT * FROM produit WHERE nom LIKE ? OR category LIKE ?"; // Requête avec filtre

        try (Connection connection = JdbcDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if (!fetchAll) {
                // Ajouter les paramètres pour le filtre si nécessaire
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getInt("quantite"),
                            rs.getDouble("prix"),
                            rs.getString("image"),
                            rs.getString("category"),
                            rs.getInt("marque_id")
                    );
                    produits.add(produit);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }


    /**
     * Gère l'ajout d'un produit au panier.
     *
     * @param produit Le produit à ajouter.
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
