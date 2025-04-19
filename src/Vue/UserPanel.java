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

        detailsCommandeButton = new JButton("Détails de la commande");
        detailsCommandeButton.addActionListener(e -> controller.afficherDetailsCommande());
        actionsPanel.add(detailsCommandeButton);

        avisButton = new JButton("Ajouter un avis");
        avisButton.addActionListener(e -> controller.ajouterAvis());
        actionsPanel.add(avisButton);

        refreshButton = new JButton("Rafraîchir");
        refreshButton.addActionListener(e -> controller.refreshPage());
        actionsPanel.add(refreshButton);

        this.add(actionsPanel, BorderLayout.SOUTH);

        controller.refreshPage();
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
