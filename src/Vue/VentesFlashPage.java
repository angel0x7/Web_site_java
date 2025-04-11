package Vue;

import Dao.ProduitDAO;
import Modele.Produit;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

public class VentesFlashPage extends JPanel {
    public VentesFlashPage() {
        setLayout(new BorderLayout());

        // Titre principal
        JLabel title = new JLabel("Produits en Vente", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Récupérer les produits depuis la base de données
        ProduitDAO produitDAO = new ProduitDAO();
        List<Produit> produits = produitDAO.getAllProduits();

        // Container pour afficher les produits en grille
        JPanel productGrid = new JPanel();
        productGrid.setLayout(new GridLayout(0, 4, 5, 5)); // 4 colonnes pour une grille compacte, avec des marges réduites

        // Ajouter chaque produit sous forme de carte
        for (Produit produit : produits) {
            JPanel productCard = createProductCard(produit);
            productGrid.add(productCard);
        }

        // Ajouter le conteneur dans un JScrollPane pour le défilement
        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges autour de la grille
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Méthode pour créer une "carte produit" plus compacte.
     */
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true)); // Bordure légère autour de la carte
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(50, 50)); // Taille réduite des cartes

        // Partie pour les petits détails (nom, prix)
        JPanel details = new JPanel();
        details.setLayout(new GridLayout(2, 1));
        details.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); // Marges plus fines

        JLabel nameLabel = new JLabel(produit.getNomProduit(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12)); // Police plus petite
        details.add(nameLabel);

        JLabel priceLabel = new JLabel(produit.getPrix() + "€", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(Color.RED);
        details.add(priceLabel);

        card.add(details, BorderLayout.CENTER);

        // Bouton "Panier" plus petit
        JButton panierButton = new JButton("Panier");
        panierButton.setFont(new Font("Arial", Font.PLAIN, 11)); // Police plus petite pour le bouton
        panierButton.setBackground(new Color(0, 123, 255));
        panierButton.setForeground(Color.WHITE);
        panierButton.setFocusPainted(false);
        panierButton.setBorderPainted(false);

        // Événement du clic sur le bouton
        panierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VentesFlashPage.this,
                        "Article ajouté : " + produit.getNomProduit(),
                        "Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        card.add(panierButton, BorderLayout.SOUTH);

        return card;
    }
}