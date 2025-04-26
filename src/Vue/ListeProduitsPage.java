package Vue;

import Modele.Produit;
import Modele.Reduction;
import Modele.User;
import Controleur.ListeProduitsController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class ListeProduitsPage extends JPanel {

    private User currentUser;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel productsPanel;
    private ListeProduitsController controller;
    private ListeProduitsPage.ProductClickListener productClickListener;

    public interface ProductClickListener {
        void onProductClick(Produit produit);
    }

    public void setProductClickListener(ListeProduitsPage.ProductClickListener listener) {
        this.productClickListener = listener;
    }

    public ListeProduitsPage(User user) {
        this.currentUser = user;
        this.controller = new ListeProduitsController(this, currentUser);

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

        controller.loadAndDisplayProducts("");
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
        searchButton.addActionListener(e -> controller.loadAndDisplayProducts(searchField.getText()));
        searchPanel.add(searchButton, BorderLayout.EAST);

        return searchPanel;
    }

    public void displayProducts(List<Produit> produits) {
        productsPanel.removeAll();

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
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        Reduction reduction = controller.getReductionForProduct(produit.getIdProduit());
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
        addToCartButton.addActionListener(e -> controller.handleAddToCart(produit));
        detailsPanel.add(addToCartButton);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (productClickListener != null) {
                    productClickListener.onProductClick(produit);
                }
            }
        });
        card.add(detailsPanel, BorderLayout.SOUTH);
        return card;

    }
}