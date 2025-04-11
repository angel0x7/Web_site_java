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
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VentesFlashPage extends JPanel {
    private User currentUser;

    public VentesFlashPage(User user) {
        this.currentUser = user;

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
        productGrid.setLayout(new GridLayout(0, 4, 5, 5));

        // Ajouter chaque produit sous forme de carte
        for (Produit produit : produits) {
            JPanel productCard = createProductCard(produit);
            productGrid.add(productCard);
        }

        // Ajouter le conteneur dans un JScrollPane pour le défilement
        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Méthode pour créer une "carte produit".
     */
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(50, 50));

        // Partie pour les petits détails (nom, prix)
        JPanel details = new JPanel();
        details.setLayout(new GridLayout(2, 1));

        JLabel nameLabel = new JLabel(produit.getNomProduit(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        details.add(nameLabel);

        JLabel priceLabel = new JLabel(produit.getPrix() + "€", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(Color.RED);
        details.add(priceLabel);

        card.add(details, BorderLayout.CENTER);

        // Bouton "Panier"
        JButton panierButton = new JButton("Panier");
        panierButton.setFont(new Font("Arial", Font.PLAIN, 11));
        panierButton.setBackground(new Color(0, 123, 255));
        panierButton.setForeground(Color.WHITE);

        // Événement du clic sur le bouton
        panierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Vérification que l'utilisateur est connecté
                    if (currentUser == null) {
                        JOptionPane.showMessageDialog(VentesFlashPage.this,
                                "Veuillez vous connecter pour ajouter un article au panier.",
                                "Non connecté",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int utilisateurId = currentUser.getId();  // Obtenir l'utilisateur connecté
                    Connection connection = JdbcDataSource.getConnection();
                    PanierDAO panierDAO = new PanierDAO(connection);

                    // Vérifier ou créer un panier pour l'utilisateur
                    int panierId = panierDAO.getOrCreatePanier(utilisateurId);

                    // Ajouter ou mettre à jour le produit dans le panier
                    panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), 1);

                    // Mettre à jour la taille du panier
                    panierDAO.updatePanierTaille(panierId);

                    // Confirmer l'ajout
                    JOptionPane.showMessageDialog(VentesFlashPage.this,
                            "Article ajouté : " + produit.getNomProduit(),
                            "Confirmation",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(VentesFlashPage.this,
                            "Erreur lors de l'ajout au panier.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Fermer la connexion
                    JdbcDataSource.closeConnection();
                }
            }
        });

        card.add(panierButton, BorderLayout.SOUTH);

        return card;
    }
}