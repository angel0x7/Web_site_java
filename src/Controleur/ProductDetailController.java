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
                if (user == null) {
                    JOptionPane.showMessageDialog(view, "Veuillez vous connecter.", "Non connecté", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                PanierDAO panierDAO = new PanierDAO(connection);
                int panierId = panierDAO.getOrCreatePanier(user.getId());
                panierDAO.addOrUpdateElementPanier(panierId, produit.getIdProduit(), selectedQuantity);
                panierDAO.updatePanierTaille(panierId);

                JOptionPane.showMessageDialog(view, selectedQuantity + " x " + produit.getNomProduit() + " ajouté(s) au panier !");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Erreur lors de l'ajout au panier", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        };
    }
}
