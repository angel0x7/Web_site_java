package Vue;

import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;
import Controleur.VentesFlashController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentesFlashPage extends JPanel {

    private final User currentUser;
    private final JPanel productsPanel;
    private final VentesFlashController controller;
    private VentesFlashPage.ProductClickListener productClickListener;
    public interface ProductClickListener {
        void onProductClick(Produit produit);
    }

    public void setProductClickListener(VentesFlashPage.ProductClickListener listener) {
        this.productClickListener = listener;
    }

    public VentesFlashPage(User user) {
        this.currentUser = user;
        this.controller = new VentesFlashController(this, user);

        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Ventes Flash", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        productsPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        productsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        productsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane, BorderLayout.CENTER);

        loadAndDisplayProducts();
    }

    public void afficherProduits(List<JPanel> productCards) {
        productsPanel.removeAll();
        for (JPanel card : productCards) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            productsPanel.add(card);
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    public void loadAndDisplayProducts() {
        List<Produit> produitsAvecReduction = this.controller.getProduitsAvecReduction();
        List<JPanel> productCards = new ArrayList<>();

        for (Produit produit : produitsAvecReduction) {
            productCards.add(createProductCard(produit));
        }

        afficherProduits(productCards);
    }

    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 300));

        String imagePath = produit.getImagePath();
        JLabel productImage = new JLabel();
        productImage.setHorizontalAlignment(SwingConstants.CENTER);
        productImage.setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon productIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH));
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
        addToCartButton.addActionListener(e -> this.controller.handleAddToCart(produit));
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