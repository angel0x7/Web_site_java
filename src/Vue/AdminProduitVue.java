package Vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import Dao.AdminProduitDaoImpl;
import Dao.DaoFactory;
import Modele.Produit;
import javax.swing.table.TableCellRenderer;




public class AdminProduitVue extends JFrame {
    private DaoFactory daoFactory;
    private JTable table;
    private DefaultTableModel model;
    private AdminProduitDaoImpl produitDao;
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
                0 // ID Marque non stocké dans la table
        );
    }

    public AdminProduitVue(AdminProduitDaoImpl produitDao) {
        this.produitDao = produitDao;

        setTitle("Gestion des Produits");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Création du tableau
        String[] columnNames = {"ID", "Nom", "Description", "Quantité", "Prix", "Image", "Catégorie", "Actions"};
        model = new DefaultTableModel(columnNames, 0);

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setRowHeight(70);

        // Ajout du tableau dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel panelButtons = new JPanel();
        JButton btnRefresh = new JButton("Recharger");
        JButton btnNouveau = new JButton("Nouveau Produit");

        panelButtons.add(btnRefresh);
        panelButtons.add(btnNouveau);
        add(panelButtons, BorderLayout.SOUTH);

        // Actions des boutons
        btnRefresh.addActionListener(e -> chargerProduits());
        btnNouveau.addActionListener(e -> ouvrirFenetreProduit(null));

        // Chargement initial des produits
        chargerProduits();

        setVisible(true);
    }

    private void chargerProduits() {
        model.setRowCount(0);
        int rowCounter=0;
        ArrayList<Produit> produits = produitDao.getAll();
        for (Produit p : produits) {
            JPanel panelActions = new JPanel();
            JButton btnModifier = new JButton("Modifier");
            JButton btnSupprimer = new JButton("Supprimer");
            panelActions.add(btnModifier);
            panelActions.add(btnSupprimer);
            model.addRow(new Object[]{p.getIdProduit(), p.getNomProduit(), p.getDescription(), p.getQuantite(),
                    p.getPrix(), p.getImage(), p.getCategorie(), panelActions});

            btnModifier.addActionListener(e -> ouvrirFenetreProduit(p));
            btnSupprimer.addActionListener(e -> supprimerProduit(p.getIdProduit()));
            rowCounter++;
        }
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(this,table));
    }

    private void ouvrirFenetreProduit(Produit produit) {
        JFrame fenetreProduit = new JFrame(produit == null ? "Ajouter un Produit" : "Modifier un Produit");
        fenetreProduit.setSize(400, 300);
        fenetreProduit.setLayout(new GridLayout(8, 2));
        fenetreProduit.setLocationRelativeTo(this);

        JTextField txtNom = new JTextField(produit != null ? produit.getNomProduit() : "");
        JTextField txtDescription = new JTextField(produit != null ? produit.getDescription() : "");
        JTextField txtQuantite = new JTextField(produit != null ? String.valueOf(produit.getQuantite()) : "");
        JTextField txtPrix = new JTextField(produit != null ? String.valueOf(produit.getPrix()) : "");
        JTextField txtImage = new JTextField(produit != null ? produit.getImage() : "");
        JTextField txtCategorie = new JTextField(produit != null ? produit.getCategorie() : "");
        JTextField txtMarqueId = new JTextField(produit != null ? String.valueOf(produit.getIdMarque()) : "");

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
        fenetreProduit.add(new JLabel("ID Marque:"));
        fenetreProduit.add(txtMarqueId);

        JButton btnEnregistrer = new JButton(produit == null ? "Ajouter" : "Modifier");
        fenetreProduit.add(btnEnregistrer);

        btnEnregistrer.addActionListener(e -> {
            try {
                Produit newProduit = new Produit(
                        produit != null ? produit.getIdProduit() : 0,
                        txtNom.getText(),
                        txtDescription.getText(),
                        Integer.parseInt(txtQuantite.getText()),
                        Double.parseDouble(txtPrix.getText()),
                        txtImage.getText(),
                        txtCategorie.getText(),
                        Integer.parseInt(txtMarqueId.getText())
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

        fenetreProduit.setVisible(true);
    }

    private void supprimerProduit(int idProduit) {
        int confirmation = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce produit ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            produitDao.supprimer(idProduit);
            chargerProduits();
        }
    }

    public static void main(String[] args) {
        DaoFactory daoFactory = new DaoFactory("jdbc:mysql://localhost:3306/shopping", "root", "");
        AdminProduitDaoImpl produitDao = new AdminProduitDaoImpl(daoFactory);
        SwingUtilities.invokeLater(() -> new AdminProduitVue(produitDao));
    }
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
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
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnModifier = new JButton("Modifier");
        private JButton btnSupprimer = new JButton("Supprimer");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(btnModifier);
            add(btnSupprimer);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }


}