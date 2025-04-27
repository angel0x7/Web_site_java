package Vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

import Dao.AdminMarqueDaoImpl;
import Dao.AdminProduitDaoImpl;
import Dao.DaoFactory;
import Modele.Marque;
import Modele.Produit;

public class AdminProduitVue extends JPanel {

    private DaoFactory daoFactory;
    private JTable table;
    private DefaultTableModel model;
    private AdminProduitDaoImpl produitDao;

    public AdminProduitVue(AdminProduitDaoImpl produitDao) {
        this.produitDao = produitDao;
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom", "Description", "Quantité", "Prix", "Image", "Catégorie","Marque", "Actions"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(180, 200, 255));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(220, 220, 220));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelButtons.setBackground(Color.WHITE);

        JButton btnRefresh = new JButton("Recharger");
        JButton btnNouveau = new JButton("Nouveau Produit");
        styleBoutonsPrincipaux(btnRefresh);
        styleBoutonsPrincipaux(btnNouveau);
        panelButtons.add(btnRefresh);
        panelButtons.add(btnNouveau);
        add(panelButtons, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> chargerProduits());
        btnNouveau.addActionListener(e -> ouvrirFenetreProduit(null));

        chargerProduits();
        setVisible(true);
    }

    public Produit getProduitAt(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }

        return new Produit(
                (int) model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (int) model.getValueAt(row, 3),
                (double) model.getValueAt(row, 4),
                (String) model.getValueAt(row, 5),
                (String) model.getValueAt(row, 6),
                (int) model.getValueAt(row, 7)
        );
    }

    private void chargerProduits() {
        model.setRowCount(0);
        ArrayList<Produit> produits = produitDao.getAll();

        for (Produit p : produits) {
            JPanel panelActions = new JPanel();
            JButton btnModifier = new JButton("Modifier");
            JButton btnSupprimer = new JButton("Supprimer");
            styleBoutonsAction(btnModifier);
            styleBoutonsAction(btnSupprimer);
            panelActions.add(btnModifier);
            panelActions.add(btnSupprimer);

            model.addRow(new Object[]{
                    p.getIdProduit(),
                    p.getNomProduit(),
                    p.getDescription(),
                    p.getQuantite(),
                    p.getPrix(),
                    p.getImage(),
                    p.getCategorie(),
                    p.getIdMarque(),
                    panelActions
            });

            btnModifier.addActionListener(e -> ouvrirFenetreProduit(p));
            btnSupprimer.addActionListener(e -> supprimerProduit(p.getIdProduit()));
        }

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(this, table));
    }

    private void ouvrirFenetreProduit(Produit produit) {
        JDialog fenetreProduit = new JDialog(SwingUtilities.getWindowAncestor(this),
                produit == null ? "Ajouter un Produit" : "Modifier un Produit",
                Dialog.ModalityType.APPLICATION_MODAL);

        fenetreProduit.setLayout(new GridLayout(8, 2));
        fenetreProduit.setMinimumSize(new Dimension(500, 500));
        fenetreProduit.setLocationRelativeTo(this);

        JTextField txtNom = new JTextField(produit != null ? produit.getNomProduit() : "");
        JTextField txtDescription = new JTextField(produit != null ? produit.getDescription() : "");
        JTextField txtQuantite = new JTextField(produit != null ? String.valueOf(produit.getQuantite()) : "");
        JTextField txtPrix = new JTextField(produit != null ? String.valueOf(produit.getPrix()) : "");
        JTextField txtImage = new JTextField(produit != null ? produit.getImage() : "");
        JTextField txtCategorie = new JTextField(produit != null ? produit.getCategorie() : "");

        AdminMarqueDaoImpl marqueDao = new AdminMarqueDaoImpl(daoFactory);
        ArrayList<Marque> marques = marqueDao.getAll();
        JComboBox<String> comboMarques = new JComboBox<>();

        for (Marque m : marques) {
            comboMarques.addItem(m.getNom());
        }

        if (produit != null) {
            Marque marqueActuel = marqueDao.getById(produit.getIdMarque());
            if (marqueActuel != null) {
                comboMarques.setSelectedItem(marqueActuel.getNom());
            }
        }
        if(produit!=null){
            Marque marque = marqueDao.getById(produit.getIdMarque());
            comboMarques.setSelectedItem(marque.getNom());
        }

        fenetreProduit.add(new JLabel("Nom:"));
        fenetreProduit.add(txtNom);
        fenetreProduit.add(new JLabel("Description:"));
        fenetreProduit.add(txtDescription);
        fenetreProduit.add(new JLabel("Quantité:"));
        fenetreProduit.add(txtQuantite);
        fenetreProduit.add(new JLabel("Prix:"));
        fenetreProduit.add(txtPrix);
        fenetreProduit.add(new JLabel("Image:"));
        fenetreProduit.add(txtImage);
        fenetreProduit.add(new JLabel("Catégorie:"));
        fenetreProduit.add(txtCategorie);
        fenetreProduit.add(new JLabel("Marque:"));
        fenetreProduit.add(comboMarques);

        JButton btnEnregistrer = new JButton(produit == null ? "Ajouter" : "Modifier");
        fenetreProduit.add(new JLabel());
        fenetreProduit.add(btnEnregistrer);

        btnEnregistrer.addActionListener(e -> {
            try {
                String nomMarqueSelectionne = (String) comboMarques.getSelectedItem();
                int idMarque = marqueDao.getIdByNom(nomMarqueSelectionne);
                Produit newProduit = new Produit(
                        produit != null ? produit.getIdProduit() : 0,
                        txtNom.getText(),
                        txtDescription.getText(),
                        Integer.parseInt(txtQuantite.getText()),
                        Double.parseDouble(txtPrix.getText()),
                        txtImage.getText(),
                        txtCategorie.getText(),
                        idMarque
                );
                if (produit == null) {
                    produitDao.ajouter(newProduit);
                } else {
                    produitDao.modifier(newProduit);
                }
                chargerProduits();
                fenetreProduit.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(fenetreProduit, "Erreur lors de l'opération !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        fenetreProduit.pack();
        fenetreProduit.setLocationRelativeTo(this);
        fenetreProduit.setVisible(true);
    }

    private void supprimerProduit(int idProduit) {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce produit ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            produitDao.supprimer(idProduit);
            chargerProduits();
        }
    }
    private void styleBoutonsPrincipaux(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(100, 150, 255));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleBoutonsAction(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(180, 180, 180));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Editor de cellule pour les boutons
    static class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnModifier;
        private JButton btnSupprimer;
        private int currentRow;
        private AdminProduitVue adminProduitVue;
        private JTable table;

        public ButtonEditor(AdminProduitVue adminProduitVue, JTable table) {
            this.adminProduitVue = adminProduitVue;
            this.table = table;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnModifier = new JButton("Modifier");
            btnSupprimer = new JButton("Supprimer");
            for (JButton btn : new JButton[]{btnModifier, btnSupprimer}) {
                btn.setFocusPainted(false);
                btn.setBackground(new Color(180, 180, 180));
                btn.setForeground(Color.BLACK);
                btn.setFont(new Font("Arial", Font.PLAIN, 12));
                btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(150, 60));
            }
            panel.add(btnModifier);
            panel.add(btnSupprimer);

            btnModifier.addActionListener(e -> {
                Produit produit = adminProduitVue.getProduitAt(currentRow);
                if (produit != null) {
                    adminProduitVue.ouvrirFenetreProduit(produit);
                }
                fireEditingStopped();
            });

            btnSupprimer.addActionListener(e -> {
                Produit produit = adminProduitVue.getProduitAt(currentRow);
                if (produit != null) {
                    adminProduitVue.supprimerProduit(produit.getIdProduit());
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return panel;
        }
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof JPanel) {
                return (JPanel) value;
            }
            return this;
        }
    }
}
