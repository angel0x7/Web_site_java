package Vue;

import Dao.AdminMarqueDaoImpl;
import Modele.Marque;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class AdminMarqueVue extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private AdminMarqueDaoImpl marqueDao;

    public AdminMarqueVue(AdminMarqueDaoImpl marqueDao) {
        this.marqueDao = marqueDao;
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom", "Image", "Description", "Actions"};
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
        JButton btnNouveau = new JButton("Nouvelle Marque");

        styleBoutonsPrincipaux(btnRefresh);
        styleBoutonsPrincipaux(btnNouveau);

        panelButtons.add(btnRefresh);
        panelButtons.add(btnNouveau);
        add(panelButtons, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> chargerMarques());
        btnNouveau.addActionListener(e -> ouvrirFenetreMarque(null));

        chargerMarques();
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

    private void chargerMarques() {
        model.setRowCount(0);
        ArrayList<Marque> marques = marqueDao.getAll();

        for (Marque m : marques) {
            JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panelActions.setBackground(Color.WHITE);

            JButton btnModifier = new JButton("Modifier");
            JButton btnSupprimer = new JButton("Supprimer");

            styleBoutonsAction(btnModifier);
            styleBoutonsAction(btnSupprimer);

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
        JDialog fenetre = new JDialog(SwingUtilities.getWindowAncestor(this),
                marque == null ? "Ajouter une Marque" : "Modifier une Marque",
                Dialog.ModalityType.APPLICATION_MODAL);

        fenetre.setLayout(new GridLayout(4, 2, 10, 10));
        fenetre.getContentPane().setBackground(Color.WHITE);
        fenetre.setLocationRelativeTo(this);
        fenetre.setPreferredSize(new Dimension(400, 200));

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        JLabel lblNom = new JLabel("Nom");
        JLabel lblImage = new JLabel("Image");
        JLabel lblDescription = new JLabel("Description");
        for (JLabel lbl : new JLabel[]{lblNom, lblImage, lblDescription}) {
            lbl.setForeground(Color.BLACK);
            lbl.setFont(labelFont);
        }

        JTextField txtNom = new JTextField(marque != null ? marque.getNom() : "");
        JTextField txtImage = new JTextField(marque != null ? marque.getImage() : "");
        JTextField txtDescription = new JTextField(marque != null ? marque.getDescription() : "");

        for (JTextField txt : new JTextField[]{txtNom, txtImage, txtDescription}) {
            txt.setFont(fieldFont);
        }

        JButton btnEnregistrer = new JButton(marque == null ? "Ajouter" : "Modifier");
        btnEnregistrer.setBackground(new Color(100, 150, 255));
        btnEnregistrer.setForeground(Color.WHITE);
        btnEnregistrer.setFont(new Font("Arial", Font.BOLD, 13));
        btnEnregistrer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        fenetre.add(lblNom);
        fenetre.add(txtNom);
        fenetre.add(lblImage);
        fenetre.add(txtImage);
        fenetre.add(lblDescription);
        fenetre.add(txtDescription);
        fenetre.add(new JLabel());
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

        fenetre.pack();
        fenetre.setVisible(true);
    }

    private void supprimerMarque(int id) {
        int confirmation = JOptionPane.showConfirmDialog(this, "Supprimer cette marque ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            marqueDao.supprimer(id);
            chargerMarques();
        }
    }

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
            panel.setBackground(Color.WHITE);
            btnModifier = new JButton("Modifier");
            btnSupprimer = new JButton("Supprimer");

            for (JButton btn : new JButton[]{btnModifier, btnSupprimer}) {
                btn.setFocusPainted(false);
                btn.setBackground(new Color(180, 180, 180));
                btn.setForeground(Color.BLACK);
                btn.setFont(new Font("Arial", Font.PLAIN, 12));
                btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

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
            setBackground(Color.WHITE);

            for (JButton btn : new JButton[]{btnModifier, btnSupprimer}) {
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(20, 20));
                btn.setBackground(new Color(180, 180, 180));
                btn.setForeground(Color.BLACK);
                btn.setFont(new Font("Arial", Font.PLAIN, 12));
                btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(150, 60));

            }

            add(btnModifier);
            add(btnSupprimer);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
}