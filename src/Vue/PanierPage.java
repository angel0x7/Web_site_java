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
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Votre panier", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(33, 37, 41));
        title.setBorder(new EmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        produitsPanel = new JPanel();
        produitsPanel.setLayout(new BoxLayout(produitsPanel, BoxLayout.Y_AXIS));
        produitsPanel.setBackground(Color.WHITE);
        produitsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(produitsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        footerPanel.setBackground(new Color(245, 245, 245));

        totalLabel = new JLabel("Total : 0€", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(new Color(0, 102, 51));
        totalLabel.setBorder(new EmptyBorder(5, 0, 10, 10));
        footerPanel.add(totalLabel, BorderLayout.NORTH);

        JButton commanderButton = new JButton("Commander");
        commanderButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commanderButton.setBackground(new Color(0, 123, 255));
        commanderButton.setForeground(Color.WHITE);
        commanderButton.setFocusPainted(false);
        commanderButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        commanderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        commanderButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        commanderButton.addActionListener(e -> controller.passerCommande());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(commanderButton);

        footerPanel.add(buttonPanel, BorderLayout.SOUTH);
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
