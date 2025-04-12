package Vue;

import Dao.AdminProduitDaoImpl;
import Dao.AdminReductionDaoImpl;
import Dao.DaoFactory;
import Modele.Produit;
import Modele.Reduction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class AdminReductionVue extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private AdminReductionDaoImpl reductionDao;
    private AdminProduitDaoImpl produitDao;

    public AdminReductionVue(AdminReductionDaoImpl reductionDao) {
        this.reductionDao = reductionDao;
        this.produitDao = new AdminProduitDaoImpl(reductionDao.getDaoFactory());

        setTitle("Gestion des Réductions");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom", "Produit", "Prix", "Quantité", "Actions"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(70);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        JButton btnRefresh = new JButton("Recharger");
        JButton btnNouveau = new JButton("Nouvelle Réduction");

        panelButtons.add(btnRefresh);
        panelButtons.add(btnNouveau);
        add(panelButtons, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> chargerReductions());
        btnNouveau.addActionListener(e -> ouvrirFenetreReduction(null));

        chargerReductions();
        setVisible(true);
    }

    private void chargerReductions() {
        model.setRowCount(0);

        ArrayList<Reduction> reductions = reductionDao.getAll();

        for (Reduction r : reductions) {
            Produit prod = produitDao.getById(r.getProduit_id());
            String nomProduit = (prod != null) ? prod.getNomProduit() : "Inconnu";

            model.addRow(new Object[]{
                    r.getId(),
                    r.getNom(),
                    nomProduit,
                    r.getPrix_vrac(),
                    r.getQuantite_vrac(),
                    "Actions"
            });
        }

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(this, table));
    }

    public Reduction getReductionAt(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }

        String nomProduit = (String) model.getValueAt(row, 2);
        int idProduit = produitDao.getIdByNom(nomProduit); // à implémenter si nécessaire

        return new Reduction(
                (int) model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (int) model.getValueAt(row, 4),
                (double) model.getValueAt(row, 3),
                idProduit
        );
    }

    private void ouvrirFenetreReduction(Reduction reduction) {
        JFrame fenetreReduction = new JFrame(reduction == null ? "Ajouter une Réduction" : "Modifier une Réduction");
        fenetreReduction.setSize(400, 300);
        fenetreReduction.setLayout(new GridLayout(5, 2));
        fenetreReduction.setLocationRelativeTo(this);

        JTextField txtNom = new JTextField(reduction != null ? reduction.getNom() : "");
        JTextField txtPrix = new JTextField(reduction != null ? String.valueOf(reduction.getPrix_vrac()) : "");
        JTextField txtQuantite = new JTextField(reduction != null ? String.valueOf(reduction.getQuantite_vrac()) : "");

        // Récupère tous les produits pour le menu déroulant
        AdminProduitDaoImpl produitDao = new AdminProduitDaoImpl(reductionDao.getDaoFactory());
        ArrayList<Produit> produits = produitDao.getAll();

        JComboBox<String> comboProduits = new JComboBox<>();
        for (Produit p : produits) {
            comboProduits.addItem(p.getNomProduit());
        }

        // Si modification, on sélectionne le bon produit dans la combo
        if (reduction != null) {
            Produit produitActuel = produitDao.getById(reduction.getProduit_id());
            if (produitActuel != null) {
                comboProduits.setSelectedItem(produitActuel.getNomProduit());
            }
        }

        fenetreReduction.add(new JLabel("Nom:"));
        fenetreReduction.add(txtNom);
        fenetreReduction.add(new JLabel("Produit:"));
        fenetreReduction.add(comboProduits);
        fenetreReduction.add(new JLabel("Prix:"));
        fenetreReduction.add(txtPrix);
        fenetreReduction.add(new JLabel("Quantité:"));
        fenetreReduction.add(txtQuantite);

        JButton btnEnregistrer = new JButton(reduction == null ? "Ajouter" : "Modifier");
        fenetreReduction.add(btnEnregistrer);

        btnEnregistrer.addActionListener(e -> {
            try {
                String nomProduitSelectionne = (String) comboProduits.getSelectedItem();
                int idProduit = produitDao.getIdByNom(nomProduitSelectionne);

                Reduction newReduction = new Reduction(
                        (reduction != null ? reduction.getId() : 0),
                        txtNom.getText(),
                        Integer.parseInt(txtQuantite.getText()),
                        Double.parseDouble(txtPrix.getText()),
                        idProduit
                );

                if (reduction == null) {
                    reductionDao.ajouter(newReduction);
                } else {
                    reductionDao.modifier(newReduction);
                }

                chargerReductions();
                fenetreReduction.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(fenetreReduction, "Erreur lors de l'opération : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        fenetreReduction.setVisible(true);
    }

    private void supprimerReduction(int idReduction) {
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cette réduction ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            reductionDao.supprimer(idReduction);
            chargerReductions();
        }
    }

    public static void main(String[] args) {
        DaoFactory daoFactory = new DaoFactory("jdbc:mysql://localhost:3306/shopping", "root", "");
        AdminReductionDaoImpl reductionDao = new AdminReductionDaoImpl(daoFactory);
        SwingUtilities.invokeLater(() -> new AdminReductionVue(reductionDao));
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnModifier;
        private JButton btnSupprimer;
        private int currentRow;
        private AdminReductionVue vue;
        private JTable table;

        public ButtonEditor(AdminReductionVue vue, JTable table) {
            this.vue = vue;
            this.table = table;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnModifier = new JButton("Modifier");
            btnSupprimer = new JButton("Supprimer");
            panel.add(btnModifier);
            panel.add(btnSupprimer);

            btnModifier.addActionListener(e -> {
                Reduction r = vue.getReductionAt(currentRow);
                if (r != null) vue.ouvrirFenetreReduction(r);
                fireEditingStopped();
            });

            btnSupprimer.addActionListener(e -> {
                Reduction r = vue.getReductionAt(currentRow);
                if (r != null) vue.supprimerReduction(r.getId());
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