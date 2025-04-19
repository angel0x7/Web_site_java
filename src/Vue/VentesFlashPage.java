package Vue;

import Modele.Produit;
import Modele.User;
import Controleur.VentesFlashController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class VentesFlashPage extends JPanel {

    private final User currentUser;
    private final JPanel productsPanel;
    private final VentesFlashController controller;

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

        controller.loadAndDisplayProducts();
    }

    public void afficherProduits(List<JPanel> productCards) {
        productsPanel.removeAll();
        for (JPanel card : productCards) {
            productsPanel.add(card);
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }
}