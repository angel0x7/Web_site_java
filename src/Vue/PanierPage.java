package Vue;

import Modele.Produit;
import Modele.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import Controleur.PanierController;

public class PanierPage extends JPanel {
    private User currentUser;
    private List<Produit> produitsPanier;
    private List<Integer> quantitesPanier;
    private JLabel totalLabel;
    private JPanel produitsPanel;
    private UserPanel userPanel;

    private PanierController controller;

    public PanierPage(User user) {
        this.currentUser = user;
        this.produitsPanier = new ArrayList<>();
        this.quantitesPanier = new ArrayList<>();
        this.controller = new PanierController(this, user);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("🛒 Votre panier", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        produitsPanel = new JPanel();
        produitsPanel.setLayout(new BoxLayout(produitsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(produitsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total : 0€", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        footerPanel.add(totalLabel, BorderLayout.NORTH);

        JButton commanderButton = new JButton("Commander");
        commanderButton.setBackground(new Color(0, 123, 255));
        commanderButton.setForeground(Color.WHITE);
        commanderButton.addActionListener(e -> controller.passerCommande());
        footerPanel.add(commanderButton, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);

        if (currentUser != null) {
            controller.chargerProduitsDuPanier();
        } else {
            controller.afficherMessageUtilisateurNonConnecte();
        }
    }

    public void refreshPage() {
        if (currentUser != null) {
            controller.chargerProduitsDuPanier();
        } else {
            controller.afficherMessageUtilisateurNonConnecte();
        }
    }

    public JPanel getProduitsPanel() {
        return produitsPanel;
    }

    public JLabel getTotalLabel() {
        return totalLabel;
    }

    public List<Produit> getProduitsPanier() {
        return produitsPanier;
    }

    public List<Integer> getQuantitesPanier() {
        return quantitesPanier;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setUserPanel(UserPanel panel) {
        this.userPanel = panel;
    }

    public UserPanel getUserPanel() {
        return userPanel;
    }
}
