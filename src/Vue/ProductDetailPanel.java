package Vue;

import Dao.AvisDao;
import Dao.AvisDaoImpl;
import Modele.Avis;
import Modele.Produit;
import Modele.User;
import Controleur.ProductDetailController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class ProductDetailPanel extends JPanel {

    private final JComboBox<Integer> quantityCombo = new JComboBox<>();
    private final JButton addButton = createStyledButton("Ajouter au panier", new Color(0, 153, 76));
    private final JButton closeButton = createStyledButton("Fermer", new Color(200, 50, 50));
    private final AvisDao avisDao = new AvisDaoImpl();

    public ProductDetailPanel(Produit produit, User user, Runnable onClose) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 4, true));
        setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(produit.getImagePath()).getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            imageLabel.setText("Image indisponible");
        }
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel addAvisPanel = new JPanel();
        addAvisPanel.setLayout(new BoxLayout(addAvisPanel, BoxLayout.Y_AXIS));
        addAvisPanel.setBackground(new Color(245, 245, 245));
        addAvisPanel.setBorder(BorderFactory.createTitledBorder("Ajouter un avis"));
        addAvisPanel.setMaximumSize(new Dimension(600, 300));

        JTextField titreField = new JTextField();
        titreField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        titreField.setAlignmentX(Component.CENTER_ALIGNMENT);
        titreField.setBorder(BorderFactory.createTitledBorder("Titre"));

        JTextArea descriptionField = new JTextArea(5, 100);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBorder(BorderFactory.createTitledBorder("Commentaire"));

        JScrollPane descriptionScroll = new JScrollPane(descriptionField);
        descriptionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descriptionScroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<Integer> noteCombo = new JComboBox<>();
        for (int i = 1; i <= 5; i++) noteCombo.addItem(i);
        noteCombo.setMaximumSize(new Dimension(100, 30));
        noteCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton envoyerAvisButton = createStyledButton("Envoyer", new Color(0, 153, 255));
        envoyerAvisButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        addAvisPanel.add(titreField);
        addAvisPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        addAvisPanel.add(descriptionScroll);
        addAvisPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        addAvisPanel.add(new JLabel("Note :"));
        addAvisPanel.add(noteCombo);
        addAvisPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        addAvisPanel.add(envoyerAvisButton);

        JPanel avisPanel = new JPanel();
        avisPanel.setLayout(new BoxLayout(avisPanel, BoxLayout.Y_AXIS));
        avisPanel.setBackground(new Color(245, 245, 245));
        avisPanel.setBorder(BorderFactory.createTitledBorder("Avis clients"));

        List<Avis> avisList = avisDao.getByProduit(produit.getIdProduit());
        for (Avis avis : avisList) {
            JPanel avisCard = new JPanel(new BorderLayout(5, 5));
            avisCard.setBackground(new Color(245, 245, 245));
            avisCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel titreLabel = new JLabel(avis.getTitre() + "    Note : " + avis.getNote() + "/5");
            titreLabel.setFont(new Font("Arial", Font.BOLD, 14));

            JTextArea descriptionArea = new JTextArea(avis.getDescription());
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            descriptionArea.setEditable(false);
            descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
            descriptionArea.setBackground(new Color(245, 245, 245));
            descriptionArea.setBorder(null);

            avisCard.add(titreLabel, BorderLayout.NORTH);
            avisCard.add(descriptionArea, BorderLayout.CENTER);

            avisPanel.add(Box.createVerticalStrut(10));
            avisPanel.add(avisCard);
        }

        JScrollPane avisScroll = new JScrollPane(avisPanel);
        avisScroll.setPreferredSize(new Dimension(350, 250));
        avisScroll.setBorder(null);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        leftPanel.add(avisScroll);

        // Partie droite
        JPanel rightPanelWrapper = new JPanel(new GridBagLayout());
        rightPanelWrapper.setBackground(Color.WHITE);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(produit.getNomProduit());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 26));
        nameLabel.setForeground(new Color(30, 30, 30));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("Prix : " + produit.getPrix() + " €");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        priceLabel.setForeground(new Color(0, 123, 255));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(produit.getDescription());
        descArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(new TitledBorder("Description"));
        descArea.setMaximumSize(new Dimension(400, 120));

        JLabel quantityLabel = new JLabel("Quantité :");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 1; i <= 10; i++) quantityCombo.addItem(i);
        quantityCombo.setMaximumSize(new Dimension(100, 30));
        quantityCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityCombo.setBackground(Color.WHITE);
        quantityCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        rightPanel.add(nameLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(priceLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(descArea);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(quantityLabel);
        rightPanel.add(quantityCombo);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(addButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(closeButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 120)));
        rightPanel.add(addAvisPanel);

        rightPanelWrapper.add(rightPanel, new GridBagConstraints());

        // Ajout final au panneau principal
        add(leftPanel, BorderLayout.WEST);
        add(rightPanelWrapper, BorderLayout.CENTER);

        // ========= Contrôleurs =========
        ProductDetailController controller = new ProductDetailController(produit, user, this, onClose);
        addButton.addActionListener(controller.getAddToCartListener());
        closeButton.addActionListener(e -> onClose.run());

        envoyerAvisButton.addActionListener(e -> {
            String titre = titreField.getText().trim();
            String description = descriptionField.getText().trim();
            int note = (Integer) noteCombo.getSelectedItem();

            if (titre.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Veuillez vous connecter pour écrire un avis", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Avis nouvelAvis = new Avis();
            nouvelAvis.setProduitId(produit.getIdProduit());
            nouvelAvis.setTitre(titre);
            nouvelAvis.setDescription(description);
            nouvelAvis.setNote(note);

            avisDao.ajouter(nouvelAvis);

            JOptionPane.showMessageDialog(this, "Avis ajouté avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

            onClose.run();
        });
    }

    public int getSelectedQuantity() {
        return (Integer) quantityCombo.getSelectedItem();
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(180, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }
}
