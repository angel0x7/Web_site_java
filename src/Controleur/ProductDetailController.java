package Controleur;

import Dao.JdbcDataSource;
import Dao.PanierDAO;
import Modele.Produit;
import Modele.User;
import Vue.ProductDetailPanel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class ProductDetailController {
    private final Produit produit;
    private final User user;
    private final ProductDetailPanel view;
    private final Runnable onClose;

    public ProductDetailController(Produit produit, User user, ProductDetailPanel view, Runnable onClose) {
        this.produit = produit;
        this.user = user;
        this.view = view;
        this.onClose = onClose;
    }

    public ActionListener getAddToCartListener() {
        return e -> {
            int selectedQuantity = view.getSelectedQuantity();
            try (Connection connection = JdbcDataSource.getConnection()) {
                // Vérifier si l'utilisateur est connecté
                if (user == null) {
                    JOptionPane.showMessageDialog(view, "Veuillez vous connecter.", "Non connecté", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Vérifier si l'utilisateur est un administrateur
                if ("ADMIN".equals(user.getRole())) {
                    JOptionPane.showMessageDialog(view, "Les administrateurs ne peuvent pas ajouter de produits au panier.", "Accès refusé", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Vérification du panierId existant
                if (user.getPanierId() == -1) {
                    JOptionPane.showMessageDialog(view, "Erreur : Panier non initialisé pour l'utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Ajouter l'élément au panier
                PanierDAO panierDAO = new PanierDAO(connection);
                int panierId = user.getPanierId();

                panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), selectedQuantity);
                panierDAO.updatePanierTaille(panierId);

                // Message de succès
                JOptionPane.showMessageDialog(view, selectedQuantity + " x " + produit.getNomProduit() + " ajouté(s) au panier !");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Erreur lors de l'ajout au panier", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        };
    }
}
