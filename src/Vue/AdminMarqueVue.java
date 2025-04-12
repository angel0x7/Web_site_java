package Vue;

import Dao.AdminMarqueDaoImpl;
import Dao.DaoFactory;
import Modele.Marque;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class AdminMarqueVue extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private AdminMarqueDaoImpl marqueDao;

    public AdminMarqueVue(AdminMarqueDaoImpl marqueDao) {
        this.marqueDao = marqueDao;

        setTitle("Gestion des Marques");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom", "Image", "Description", "Actions"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(70);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        JButton btnRefresh = new JButton("Recharger");
        JButton btnNouveau = new JButton("Nouvelle Marque");

        panelButtons.add(btnRefresh);
        panelButtons.add(btnNouveau);
        add(panelButtons, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> chargerMarques());
        btnNouveau.addActionListener(e -> ouvrirFenetreMarque(null));

        chargerMarques();
        setVisible(true);
    }

    private void chargerMarques() {
        model.setRowCount(0);
        ArrayList<Marque> marques = marqueDao.getAll();
        for (Marque m : marques) {
            JPanel panelActions = new JPanel();
            JButton btnModifier = new JButton("Modifier");
            JButton btnSupprimer = new JButton("Supprimer");
            panelActions.add(btnModifier);
            panelActions.add(btnSupprimer);
            model.addRow(new Object[]{m.getId(), m.getNom(), m.getImage(), m.getDescription(), panelActions});

            btnModifier.addActionListener(e -> ouvrirFenetreMarque(m));
            btnSupprimer.addActionListener(e -> supprimerMarque(m.getId()));
        }
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(this, table));
    }

    private void ouvrirFenetreMarque(Marque marque) {
        JFrame fenetre = new JFrame(marque == null ? "Ajouter une Marque" : "Modifier une Marque");
        fenetre.setSize(400, 250);
        fenetre.setLayout(new GridLayout(4, 2));
        fenetre.setLocationRelativeTo(this);

        JTextField txtNom = new JTextField(marque != null ? marque.getNom() : "");
        JTextField txtImage = new JTextField(marque != null ? marque.getImage() : "");
        JTextField txtDescription = new JTextField(marque != null ? marque.getDescription() : "");

        fenetre.add(new JLabel("Nom :"));
        fenetre.add(txtNom);
        fenetre.add(new JLabel("Image :"));
        fenetre.add(txtImage);
        fenetre.add(new JLabel("Description :"));
        fenetre.add(txtDescription);

        JButton btnEnregistrer = new JButton(marque == null ? "Ajouter" : "Modifier");
        fenetre.add(new JLabel()); // vide pour alignement
        fenetre.add(btnEnregistrer);

        btnEnregistrer.addActionListener(e -> {
            try {
                Marque nouvelleMarque = new Marque(
                        marque != null ? marque.getId() : 0,
                        txtNom.getText(),
                        txtImage.getText(),
                        txtDescription.getText()
                );
                if (marque == null) {
                    marqueDao.ajouter(nouvelleMarque);
                } else {
                    marqueDao.modifier(nouvelleMarque);
                }
                chargerMarques();
                fenetre.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(fenetre, "Erreur lors de l'opération !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        fenetre.setVisible(true);
    }

    private void supprimerMarque(int id) {
        int confirmation = JOptionPane.showConfirmDialog(this, "Supprimer cette marque ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            marqueDao.supprimer(id);
            chargerMarques();
        }
    }

    public static void main(String[] args) {
        DaoFactory daoFactory = new DaoFactory("jdbc:mysql://localhost:3306/shopping", "root", "");
        AdminMarqueDaoImpl marqueDao = new AdminMarqueDaoImpl(daoFactory);
        SwingUtilities.invokeLater(() -> new AdminMarqueVue(marqueDao));
    }

    // Classes internes pour les boutons dans le tableau
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnModifier;
        private JButton btnSupprimer;
        private int currentRow;
        private JTable table;
        private AdminMarqueVue vue;

        public ButtonEditor(AdminMarqueVue vue, JTable table) {
            this.vue = vue;
            this.table = table;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnModifier = new JButton("Modifier");
            btnSupprimer = new JButton("Supprimer");

            panel.add(btnModifier);
            panel.add(btnSupprimer);

            btnModifier.addActionListener(e -> {
                int id = (int) model.getValueAt(currentRow, 0);
                Marque marque = marqueDao.getById(id);
                vue.ouvrirFenetreMarque(marque);
                fireEditingStopped();
            });

            btnSupprimer.addActionListener(e -> {
                int id = (int) model.getValueAt(currentRow, 0);
                vue.supprimerMarque(id);
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
