package Vue;

import Dao.ProduitDAO;
import Modele.Produit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentesFlashPage extends JPanel {
    public VentesFlashPage() {
        setLayout(new BorderLayout());

        // récupérer les produits depuis la base de données
        ProduitDAO produitDAO = new ProduitDAO();
        List<Produit> produits = produitDAO.getAllProduits();

        // Définition des colonnes pour le tableau
        String[] columnNames = {"Nom", "Description", "Quantité", "Prix", "Catégorie", "Image"};

        // modèle du tableau avec les produits
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        // remplir le modèle avec les produits récupérés
        for (Produit produit : produits) {
            model.addRow(new Object[]{
                    produit.getNomProduit(),
                    produit.getDescription(),
                    produit.getQuantite(),
                    produit.getPrix() + "€",
                    produit.getCategorie(),
                    produit.getImage()
            });
        }

        //  le tableau et pour scroll
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        // Titre
        JLabel title = new JLabel("Produits en Vente", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);
    }
    }

