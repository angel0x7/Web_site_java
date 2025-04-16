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

public class CategoriesPage extends JPanel {

    private User currentUser;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel productsPanel;
    private String categorie;

    // === NOUVEAU : pour gérer le clic produit ===
    private ProductClickListener productClickListener;

    public interface ProductClickListener {
        void onProductClick(Produit produit);
    }

    public void setProductClickListener(ProductClickListener listener) {
        this.productClickListener = listener;
    }

    public CategoriesPage(User user,String categorie) {
        this.currentUser = user;
        this.categorie = categorie;


        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        JPanel searchPanel = createSearchPanel();
        this.add(searchPanel, BorderLayout.NORTH);

        productsPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        productsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        productsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane, BorderLayout.CENTER);

        loadAndDisplayProducts(categorie);
    }

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
        searchButton.addActionListener(e -> loadAndDisplayProducts(searchField.getText()));
        searchPanel.add(searchButton, BorderLayout.EAST);

        return searchPanel;
    }

    private void loadAndDisplayProducts(String keyword) {
        productsPanel.removeAll();
        List<Produit> produits = fetchProducts(keyword);
        for (Produit produit : produits) {
            productsPanel.add(createProductCard(produit));
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 300));

        JLabel productImage = new JLabel();
        productImage.setHorizontalAlignment(SwingConstants.CENTER);
        productImage.setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon productIcon = new ImageIcon(new ImageIcon(produit.getImagePath()).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH));
            productImage.setIcon(productIcon);
        } catch (Exception e) {
            productImage.setText("Image introuvable");
        }

        card.add(productImage, BorderLayout.CENTER);

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
        productPrice.setForeground(new Color(0, 123, 167));
        productPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPrice.setHorizontalAlignment(SwingConstants.CENTER);
        detailsPanel.add(productPrice);

        Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());
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

        JButton addToCartButton = new JButton("Ajouter au panier");
        addToCartButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addToCartButton.setBackground(new Color(0, 128, 255));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartButton.addActionListener(e -> handleAddToCart(produit));
        detailsPanel.add(addToCartButton);

        card.add(detailsPanel, BorderLayout.SOUTH);

        // === NOUVEAU : clic sur la carte produit ===
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (productClickListener != null) {
                    productClickListener.onProductClick(produit);
                }
            }
        });

        return card;
    }

    private List<Produit> fetchProducts(String keyword) {
        List<Produit> produits = new ArrayList<>();
        boolean fetchAll = keyword == null || keyword.trim().isEmpty() || keyword.equalsIgnoreCase("tout");

        String query = fetchAll
                ? "SELECT * FROM produit"
                : "SELECT * FROM produit WHERE nom LIKE ? OR category LIKE ?";

        try (Connection connection = JdbcDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if (!fetchAll) {
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

    private void handleAddToCart(Produit produit) {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez vous connecter pour ajouter un article au panier.",
                        "Utilisateur non connecté",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection connection = JdbcDataSource.getConnection();
            PanierDAO panierDAO = new PanierDAO(connection);

            int panierId = panierDAO.getOrCreatePanier(currentUser.getId());
            panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), 1);
            panierDAO.updatePanierTaille(panierId);

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
            JdbcDataSource.closeConnection();
        }
    }
}
