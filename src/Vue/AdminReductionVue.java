package Vue;

import Dao.AdminProduitDaoImpl;
import Dao.AdminReductionDaoImpl;
import Dao.DaoFactory;
import Modele.Produit;
import Modele.Reduction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class AdminReductionVue extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private AdminReductionDaoImpl reductionDao;
    private AdminProduitDaoImpl produitDao;

    public AdminReductionVue(AdminReductionDaoImpl reductionDao) {
        this.reductionDao = reductionDao;
        this.produitDao = new AdminProduitDaoImpl();

        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom", "Produit", "Prix", "Quantité", "Actions"};
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
        JButton btnNouveau = new JButton("Nouvelle Réduction");
        styleBoutonsPrincipaux(btnRefresh);
        styleBoutonsPrincipaux(btnNouveau);
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
            JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panelActions.setBackground(Color.WHITE);

            JButton btnModifier = new JButton("Modifier");
            JButton btnSupprimer = new JButton("Supprimer");

            styleBoutonsAction(btnModifier);
            styleBoutonsAction(btnSupprimer);

            panelActions.add(btnModifier);
            panelActions.add(btnSupprimer);
            model.addRow(new Object[]{
                    r.getId(),
                    r.getNom(),
                    nomProduit,
                    r.getPrix_vrac(),
                    r.getQuantite_vrac(),
                    panelActions
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
        JDialog fenetreReduction = new JDialog(SwingUtilities.getWindowAncestor(this),
                reduction == null ? "Ajouter une Réduction" : "Modifier une Réduction",
                Dialog.ModalityType.APPLICATION_MODAL);
        fenetreReduction.setSize(400, 300);
        fenetreReduction.setLayout(new GridLayout(5, 2));
        fenetreReduction.setLocationRelativeTo(this);

        JTextField txtNom = new JTextField(reduction != null ? reduction.getNom() : "");
        JTextField txtPrix = new JTextField(reduction != null ? String.valueOf(reduction.getPrix_vrac()) : "");
        JTextField txtQuantite = new JTextField(reduction != null ? String.valueOf(reduction.getQuantite_vrac()) : "");

        // Récupère tous les produits pour le menu déroulant
        AdminProduitDaoImpl produitDao = new AdminProduitDaoImpl();
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


    static class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
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

