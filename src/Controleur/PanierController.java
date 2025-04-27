package Controleur;

import Dao.AdminProduitDaoImpl;
import Dao.JdbcDataSource;
import Dao.ProduitDAO;
import Modele.Produit;
import Modele.Reduction;
import Modele.User;
import Vue.PanierPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PanierController {
    private PanierPage vue;
    private User currentUser;
    private List<Produit> produitsPanier;
    private List<Integer> quantitesPanier;

    public PanierController(PanierPage vue, User currentUser) {
        this.vue = vue;
        this.currentUser = currentUser;
        this.produitsPanier = vue.getProduitsPanier();
        this.quantitesPanier = vue.getQuantitesPanier();
    }

    public void chargerProduitsDuPanier() {
        produitsPanier.clear();
        quantitesPanier.clear();
        vue.getProduitsPanel().removeAll();

        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = """
            SELECT ep.produit_id, p.nom, p.prix, ep.quantite
            FROM element_panier ep
            JOIN produit p ON ep.produit_id = p.id
            JOIN panier pa ON ep.panier_id = pa.id
            WHERE ep.panier_id = ? AND pa.etat = 0
        """;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, currentUser.getPanierId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("produit_id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");

                Produit produit = new Produit(id, nom, "", 0, prix, "", "", 0); // quantite à 0 ici
                produitsPanier.add(produit);
                quantitesPanier.add(quantite);
            }

            afficherProduits();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue, "Erreur lors du chargement du panier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void afficherProduits() {
        JPanel produitsPanel = vue.getProduitsPanel();
        produitsPanel.removeAll();

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit produit = produitsPanier.get(i);
            int quantite = quantitesPanier.get(i);
            int finalI = i;

            JPanel produitPanel = new JPanel(new BorderLayout());
            produitPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
            ));
            produitPanel.setBackground(Color.WHITE);
            produitPanel.setPreferredSize(new Dimension(600, 120));
            produitPanel.setMaximumSize(new Dimension(600, 120));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel(produit.getNomProduit());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            nameLabel.setForeground(new Color(40, 40, 40));
            infoPanel.add(nameLabel);

            JLabel priceLabel = new JLabel("Prix unitaire : " + produit.getPrix() + "€");
            priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            priceLabel.setForeground(new Color(90, 90, 90));
            infoPanel.add(Box.createVerticalStrut(4));
            infoPanel.add(priceLabel);

            Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());
            if (reduction != null) {
                JLabel reductionLabel = new JLabel(String.format("Offre : %d pour %.2f €",
                        reduction.getQuantite_vrac(), reduction.getPrix_vrac()));
                reductionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                reductionLabel.setForeground(new Color(0, 128, 0));
                infoPanel.add(Box.createVerticalStrut(4));
                infoPanel.add(reductionLabel);
            }

            produitPanel.add(infoPanel, BorderLayout.WEST);

            JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            quantitePanel.setBackground(Color.WHITE);

            JButton decrementButton = new JButton("-");
            decrementButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            decrementButton.setBackground(new Color(83, 83, 83));
            decrementButton.setForeground(Color.WHITE);
            decrementButton.setFocusPainted(false);
            decrementButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            decrementButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            decrementButton.addActionListener(e -> {
                // Vérifier si la quantité est supérieure à 1 et si la quantité en stock est suffisante
                if (quantitesPanier.get(finalI) > 1) {
                    quantitesPanier.set(finalI, quantitesPanier.get(finalI) - 1);
                    modifierQuantiteDansDB(produitsPanier.get(finalI).getIdProduit(), quantitesPanier.get(finalI));
                    afficherProduits();
                }
            });

            JLabel quantiteLabel = new JLabel("Quantité : " + quantite);
            JButton incrementButton = new JButton("+");
            incrementButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            incrementButton.setBackground(new Color(83, 83, 83));
            incrementButton.setForeground(Color.WHITE);
            incrementButton.setFocusPainted(false);
            incrementButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            incrementButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            incrementButton.addActionListener(e -> {
                int idProduit = produit.getIdProduit();
                AdminProduitDaoImpl adminProduitDaoImpl = new AdminProduitDaoImpl();
                Produit produitBDD =adminProduitDaoImpl.getById(idProduit);

                int stockDisponible = produitBDD.getQuantite();  // Quantité en stock du produit
                int quantiteActuelle = quantitesPanier.get(finalI);
                if (quantiteActuelle < stockDisponible) {
                    quantitesPanier.set(finalI, quantiteActuelle + 1);
                    modifierQuantiteDansDB(produitsPanier.get(finalI).getIdProduit(), quantitesPanier.get(finalI));
                    afficherProduits();
                } else {
                    JOptionPane.showMessageDialog(vue, "Quantité en stock insuffisante.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });

            quantitePanel.add(decrementButton);
            quantitePanel.add(quantiteLabel);
            quantitePanel.add(incrementButton);
            produitPanel.add(quantitePanel, BorderLayout.CENTER);

            JPanel actionPanel = new JPanel();
            actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
            actionPanel.setBackground(Color.WHITE);
            actionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JButton removeButton = new JButton("Supprimer");
            removeButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            removeButton.setBackground(new Color(83, 83, 83));
            removeButton.setForeground(Color.WHITE);
            removeButton.setFocusPainted(false);
            removeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeButton.addActionListener(e -> {
                if (supprimerProduitDansDB(produit.getIdProduit())) {
                    produitsPanier.remove(finalI);
                    quantitesPanier.remove(finalI);
                    afficherProduits();
                } else {
                    JOptionPane.showMessageDialog(vue, "Erreur lors de la suppression du produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            actionPanel.add(removeButton);
            produitPanel.add(actionPanel, BorderLayout.EAST);

            double prixTotalParProduit;
            if (reduction != null && quantite >= reduction.getQuantite_vrac()) {
                int lotCount = quantite / reduction.getQuantite_vrac();
                int remainingItems = quantite % reduction.getQuantite_vrac();
                prixTotalParProduit = (lotCount * reduction.getPrix_vrac()) + (remainingItems * produit.getPrix());
            } else {
                prixTotalParProduit = quantite * produit.getPrix();
            }

            JLabel prixFinalLabel = new JLabel(String.format("Prix total pour ce produit : %.2f €", prixTotalParProduit));
            prixFinalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            prixFinalLabel.setForeground(new Color(34, 139, 34));
            produitPanel.add(prixFinalLabel, BorderLayout.SOUTH);

            produitsPanel.add(produitPanel);
        }

        produitsPanel.revalidate();
        produitsPanel.repaint();
        vue.getTotalLabel().setText("Total : " + calculerTotal() + "€");
    }


    private void modifierQuantiteDansDB(int produitId, int nouvelleQuantite) {
        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = "UPDATE element_panier SET quantite = ? WHERE produit_id = ? AND panier_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, nouvelleQuantite);
            statement.setInt(2, produitId);
            statement.setInt(3, currentUser.getPanierId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void passerCommande() {
        if (produitsPanier.isEmpty()) {
            JOptionPane.showMessageDialog(vue, "Votre panier est vide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (afficherFormulaireLivraison() && afficherFormulairePaiement()) {
            afficherConfirmationCommande();


            try (Connection connection = JdbcDataSource.getConnection()) {
                connection.setAutoCommit(false);
                String updatePanierQuery = "UPDATE panier SET etat = 1 WHERE etat = 0 AND utilisateur_id = ?";
                try (PreparedStatement updatePanierStmt = connection.prepareStatement(updatePanierQuery)) {
                    updatePanierStmt.setInt(1, currentUser.getId());
                    updatePanierStmt.executeUpdate();
                }

                //On crée le nouveau panier
                int nouveauPanierId = -1;
                String insertPanierQuery = "INSERT INTO panier (utilisateur_id, taille,etat) VALUES (?, ?,0)";
                try (PreparedStatement insertPanierStmt = connection.prepareStatement(insertPanierQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertPanierStmt.setInt(1, currentUser.getId());
                    insertPanierStmt.setInt(2, produitsPanier.size());
                    insertPanierStmt.executeUpdate();
                    try (ResultSet keys = insertPanierStmt.getGeneratedKeys()) {
                        if (keys.next()) nouveauPanierId = keys.getInt(1);
                    }
                }
                if (nouveauPanierId == -1) throw new SQLException("Impossible de récupérer l'ID du nouveau panier.");

                // On insère les éléments et on met à jour le stock
                String insertElemQuery = "INSERT INTO element_panier (panier_id, produit_id, quantite) VALUES (?, ?, ?)";
                String updateStockQuery = "UPDATE produit SET quantite = quantite - ? WHERE id = ?";
                try (PreparedStatement insertElemStmt = connection.prepareStatement(insertElemQuery);
                     PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {

                    for (int i = 0; i < produitsPanier.size(); i++) {
                        Produit produit = produitsPanier.get(i);
                        int qty = quantitesPanier.get(i);

                        // ajout au panier
                        insertElemStmt.setInt(1, nouveauPanierId);
                        insertElemStmt.setInt(2, produit.getIdProduit());
                        insertElemStmt.setInt(3, qty);
                        insertElemStmt.addBatch();

                        // mise à jour du stock
                        updateStockStmt.setInt(1, qty);
                        updateStockStmt.setInt(2, produit.getIdProduit());
                        updateStockStmt.addBatch();
                    }
                    insertElemStmt.executeBatch();
                    updateStockStmt.executeBatch();
                }
                connection.commit();


                produitsPanier.clear();
                quantitesPanier.clear();
                vue.getProduitsPanel().removeAll();
                vue.getTotalLabel().setText("Total : 0.0€");
                vue.getProduitsPanel().repaint();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(vue, "Erreur lors du passage de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

            if (vue.getUserPanel() != null) {
                vue.getUserPanel().refreshPage();
            }
        }
    }


    private double calculerTotal() {
        double total = 0;

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit produit = produitsPanier.get(i);
            int quantite = quantitesPanier.get(i);
            Reduction reduction = ProduitDAO.getReductionByProduitId(produit.getIdProduit());

            if (reduction != null && quantite >= reduction.getQuantite_vrac()) {
                int lotCount = quantite / reduction.getQuantite_vrac();
                total += lotCount * reduction.getPrix_vrac();
                int remainingItems = quantite % reduction.getQuantite_vrac();
                total += remainingItems * produit.getPrix();
            } else {
                total += quantite * produit.getPrix();
            }
        }

        return total;
    }

    private boolean supprimerProduitDansDB(int produitId) {
        try (Connection connection = JdbcDataSource.getConnection()) {
            String query = "DELETE FROM element_panier WHERE produit_id = ? AND panier_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, produitId);
            statement.setInt(2, currentUser.getPanierId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void afficherMessageUtilisateurNonConnecte() {
        JPanel produitsPanel = vue.getProduitsPanel();
        produitsPanel.removeAll();
        JLabel message = new JLabel("Veuillez vous connecter pour accéder à votre panier.", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.ITALIC, 18));
        message.setForeground(Color.RED);
        produitsPanel.add(message);
        produitsPanel.revalidate();
        produitsPanel.repaint();
    }


    private boolean afficherFormulaireLivraison() {
        JPanel livraisonPanel = new JPanel();
        livraisonPanel.setLayout(new BorderLayout(10, 10));
        livraisonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titre = new JLabel("Formulaire de livraison");
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));
        titre.setHorizontalAlignment(SwingConstants.CENTER);
        livraisonPanel.add(titre, BorderLayout.NORTH);

        JPanel adressePanel = new JPanel(new GridLayout(6, 2, 10, 10));

        adressePanel.add(new JLabel("Nom :"));
        JTextField champNom = new JTextField();
        adressePanel.add(champNom);

        adressePanel.add(new JLabel("Prénom :"));
        JTextField champPrenom = new JTextField();
        adressePanel.add(champPrenom);

        adressePanel.add(new JLabel("Adresse :"));
        JTextField champAdresse = new JTextField();
        adressePanel.add(champAdresse);

        adressePanel.add(new JLabel("Code postal :"));
        JTextField champCodePostal = new JTextField();
        adressePanel.add(champCodePostal);

        adressePanel.add(new JLabel("Ville :"));
        JTextField champVille = new JTextField();
        adressePanel.add(champVille);

        adressePanel.add(new JLabel("Pays :"));
        JComboBox<String> comboPays = new JComboBox<>();

// Remplissage des pays
        String[] countryCodes = Locale.getISOCountries();
        List<String> countryList = new ArrayList<>();
        for (String code : countryCodes) {
            Locale locale = new Locale("", code);
            countryList.add(locale.getDisplayCountry());
        }
        Collections.sort(countryList);
        for (String country : countryList) {
            comboPays.addItem(country);
        }

        adressePanel.add(comboPays);

        livraisonPanel.add(adressePanel, BorderLayout.CENTER);

        JButton valider = new JButton("Valider l'adresse de livraison");
        valider.setBackground(new Color(255, 102, 0));
        valider.setForeground(Color.WHITE);
        valider.setFont(new Font("SansSerif", Font.BOLD, 14));
        valider.setFocusPainted(false);

        int result = JOptionPane.showConfirmDialog(
                vue, livraisonPanel, "Informations de livraison", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        return result == JOptionPane.OK_OPTION;
    }

    private boolean afficherFormulairePaiement() {
        JPanel paiementPanel = new JPanel();
        paiementPanel.setLayout(new BorderLayout(10, 10));
        paiementPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titre = new JLabel("Formulaire de paiement");
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));
        titre.setHorizontalAlignment(SwingConstants.CENTER);
        paiementPanel.add(titre, BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new GridLayout(4, 2, 10, 10));

        formulaire.add(new JLabel("Nom du titulaire :"));
        JTextField champNom = new JTextField();
        formulaire.add(champNom);

        formulaire.add(new JLabel("Numéro de carte :"));
        JTextField champCarte = new JTextField();
        formulaire.add(champCarte);

        formulaire.add(new JLabel("Code de sécurité (CVV) :"));
        JPasswordField champCode = new JPasswordField();
        formulaire.add(champCode);

        formulaire.add(new JLabel("Montant total à payer :"));
        JLabel montantTotal = new JLabel(String.format("%.2f €", calculerTotal()));
        montantTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        formulaire.add(montantTotal);

        paiementPanel.add(formulaire, BorderLayout.CENTER);

        JButton valider = new JButton("Valider le paiement");
        valider.setBackground(new Color(255, 102, 0));
        valider.setForeground(Color.WHITE);
        valider.setFont(new Font("SansSerif", Font.BOLD, 14));
        valider.setFocusPainted(false);

        int result = JOptionPane.showConfirmDialog(
                vue, paiementPanel, "Informations de paiement", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        return result == JOptionPane.OK_OPTION;
    }
    private void afficherConfirmationCommande() {
        // Panel principal
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        confirmPanel.setBackground(Color.WHITE);

        // Titre
        JLabel titre = new JLabel("Confirmation de votre commande", SwingConstants.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(new Color(34, 139, 34));
        confirmPanel.add(titre, BorderLayout.NORTH);

        // Création du tableau de récapitulatif avec image
        String[] colonnes = { "Image", "Produit", "Quantité", "Prix unitaire", "Sous-total" };
        DefaultTableModel model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return ImageIcon.class;
                }
                return super.getColumnClass(column);
            }
        };

        for (int i = 0; i < produitsPanier.size(); i++) {
            Produit p = produitsPanier.get(i);
            int q = quantitesPanier.get(i);
            double prixUnitaire;
            double sousTotal;

            Reduction reduction = ProduitDAO.getReductionByProduitId(p.getIdProduit());

            if (reduction != null && q >= reduction.getQuantite_vrac()) {
                int lotCount = q / reduction.getQuantite_vrac();
                int reste = q % reduction.getQuantite_vrac();
                sousTotal = lotCount * reduction.getPrix_vrac() + reste * p.getPrix();
                prixUnitaire = sousTotal / q;
            } else {
                sousTotal = q * p.getPrix();
                prixUnitaire = p.getPrix();
            }

            // Charger et redimensionner l'image du produit
            ImageIcon productIcon = null;
            try {
                productIcon = new ImageIcon(
                        new ImageIcon(p.getImagePath()).getImage()
                                .getScaledInstance(80, 80, Image.SCALE_SMOOTH)
                );
            } catch (Exception e) {
                productIcon = new ImageIcon(); // Image vide si erreur
            }

            model.addRow(new Object[]{
                    productIcon,
                    p.getNomProduit(),
                    q,
                    String.format("%.2f €", prixUnitaire),
                    String.format("%.2f €", sousTotal)
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(80);
        table.getColumnModel().getColumn(0).setMaxWidth(100);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value);
                    label.setText("");
                }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        confirmPanel.add(scroll, BorderLayout.CENTER);

        // Total
        JLabel totalLabel = new JLabel(
                "Montant total : " + String.format("%.2f €", calculerTotal()),
                SwingConstants.RIGHT
        );
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(34, 139, 34));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(totalLabel, BorderLayout.CENTER);

        // Bouton Fermer
        JButton fermerBtn = new JButton("Fermer");
        fermerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fermerBtn.setBackground(new Color(83, 83, 83));
        fermerBtn.setForeground(Color.WHITE);
        fermerBtn.setFocusPainted(false);
        fermerBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(fermerBtn).dispose();
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(fermerBtn);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        confirmPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Création et affichage de la fenêtre
        JFrame frame = new JFrame("Confirmation de la commande");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(confirmPanel);
        frame.setVisible(true);
    }
}