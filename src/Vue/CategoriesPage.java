package Vue;

import Dao.JdbcDataSource;
import Dao.PanierDAO;
import Modele.Produit;
import Modele.User;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesPage extends JPanel {

    private User currentUser; // L'utilisateur connecté
    private JTextField searchField; // Barre de recherche
    private JButton searchButton; // Bouton de recherche
    private JPanel productsPanel; // Panneau pour afficher les produits

    public CategoriesPage(User user) {
        this.currentUser = user; // L'utilisateur qui utilise la page

        setLayout(new BorderLayout());

        // Barre de recherche en haut
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchPanel.add(new JLabel("Rechercher un produit :"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Panneau des produits
        productsPanel = new JPanel();
        productsPanel.setLayout(new GridLayout(0, 3, 10, 10)); // Affiche jusqu'à 3 produits par ligne
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Charger et afficher tous les produits au démarrage
        loadAndDisplayProducts("");

        // Gestion de l'événement de recherche
        searchButton.addActionListener((ActionEvent e) -> {
            String keyword = searchField.getText().trim();
            loadAndDisplayProducts(keyword);
        });
    }

    /**
     * Charge et affiche les produits en fonction du mot-clé de recherche.
     *
     * @param keyword Le mot-clé pour filtrer les produits.
     */
    private void loadAndDisplayProducts(String keyword) {
        productsPanel.removeAll(); // Nettoyer les produits affichés

        List<Produit> produits = fetchProducts(keyword); // Charger les produits depuis la base
        if (produits.isEmpty()) {
            productsPanel.add(new JLabel("Aucun produit trouvé."));
        } else {
            for (Produit produit : produits) {
                productsPanel.add(createProductCard(produit)); // Créer une carte pour chaque produit
            }
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    /**
     * Récupère les produits depuis la base de données selon un mot-clé.
     *
     * @param keyword Mot-clé pour filtrer les produits (nom ou catégorie).
     * @return Liste de produits.
     */
    private List<Produit> fetchProducts(String keyword) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE nom LIKE ? OR category LIKE ?";

        try (Connection conn = JdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return produits;
    }

    /**
     * Crée une carte (JPanel) pour afficher les détails d'un produit.
     *
     * @param produit Le produit à afficher dans la carte.
     * @return Un JPanel représentant la carte produit.
     */
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(Color.GRAY));
        card.setPreferredSize(new Dimension(200, 300)); // Taille fixe pour les cartes

        // Image du produit
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon(produit.getImagePath());
        Image scaledImage = imageIcon.getImage().getScaledInstance(180, 150, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        card.add(imageLabel, BorderLayout.NORTH);

        // Informations sur le produit
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(produit.getNomProduit());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel priceLabel = new JLabel(String.format("%.2f €", produit.getPrix()));
        JLabel descriptionLabel = new JLabel("<html>" + produit.getDescription() + "</html>");
        descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(descriptionLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        // Bouton pour ajouter au panier
        JButton addToCartButton = new JButton("Ajouter au panier");
        addToCartButton.addActionListener((ActionEvent e) -> {
            handleAddToCart(produit);
        });
        card.add(addToCartButton, BorderLayout.SOUTH);

        return card;
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