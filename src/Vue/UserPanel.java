package Vue;

import Controleur.UserPanelController;
import Modele.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserPanel extends JPanel {
    private User currentUser;
    private JTable historiqueTable;
    private DefaultTableModel tableModel;
    private JButton avisButton;
    private JButton detailsCommandeButton;
    private JButton refreshButton;
    private UserPanelController controller;

    public UserPanel(User user) {
        this.currentUser = user;
        this.controller = new UserPanelController(user, this);

        this.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Commande ID", "Produit", "Prix Total"}, 0);
        historiqueTable = new JTable(tableModel);

        historiqueTable.setFillsViewportHeight(true);
        historiqueTable.setRowHeight(25);
        historiqueTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < historiqueTable.getColumnCount(); i++) {
            historiqueTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(historiqueTable);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));



// Création et stylisation
        detailsCommandeButton = new JButton("Détails de la commande");
        styleActionButton(detailsCommandeButton, new Color(0, 123, 255));
        detailsCommandeButton.addActionListener(e -> controller.afficherDetailsCommande());
        actionsPanel.add(detailsCommandeButton);

        avisButton = new JButton("Ajouter un avis");
        styleActionButton(avisButton, new Color(255, 213, 14));
        avisButton.addActionListener(e -> controller.ajouterAvis());
        actionsPanel.add(avisButton);

        refreshButton = new JButton("Rafraîchir");
        styleActionButton(refreshButton, new Color(119, 234, 30));
        refreshButton.addActionListener(e -> controller.refreshPage());
        actionsPanel.add(refreshButton);

// On ajoute enfin le panel
        this.add(actionsPanel, BorderLayout.SOUTH);

        controller.refreshPage();
    }
    private void styleActionButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
    }

    public JTable getHistoriqueTable() {
        return historiqueTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JFrame getParentFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(this);
    }
    public void refreshPage() {
        controller.refreshPage();
    }
}
