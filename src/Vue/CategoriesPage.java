package Vue;

import Modele.Produit;
import Modele.Reduction;
import Modele.User;
import Controleur.CategoriesController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class CategoriesPage extends JPanel {
    private User currentUser;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel productsPanel;
    private String categorie;
    private ProductClickListener productClickListener;
    private CategoriesController controller;

    public interface ProductClickListener {
        void onProductClick(Produit produit);
    }

    public void setProductClickListener(ProductClickListener listener) {
        this.productClickListener = listener;
    }

    public CategoriesPage(User user, String categorie) {
        this.currentUser = user;
        this.categorie = categorie;
        this.controller = new CategoriesController(user);

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
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchButton = new JButton("Rechercher");

        searchButton.addActionListener(e -> loadAndDisplayProducts(searchField.getText()));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        return searchPanel;
    }

    private void loadAndDisplayProducts(String keyword) {
        productsPanel.removeAll();
        List<Produit> produits = controller.fetchProducts(keyword);
        for (Produit produit : produits) {
            productsPanel.add(createProductCard(produit));
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel(new BorderLayout());
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
        JLabel productPrice = new JLabel(String.format("Prix : %.2f €", produit.getPrix()));
        detailsPanel.add(productName);
        detailsPanel.add(productPrice);

        Reduction reduction = controller.getReduction(produit.getIdProduit());
        if (reduction != null) {
            JLabel reductionLabel = new JLabel(String.format("Offre : %d pour %.2f €", reduction.getQuantite_vrac(), reduction.getPrix_vrac()));
            reductionLabel.setForeground(Color.RED);
            detailsPanel.add(reductionLabel);
        }

        JButton addToCartButton = new JButton("Ajouter au panier");
        addToCartButton.addActionListener(e -> controller.handleAddToCart(this, produit));
        detailsPanel.add(addToCartButton);

        card.add(detailsPanel, BorderLayout.SOUTH);

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
}