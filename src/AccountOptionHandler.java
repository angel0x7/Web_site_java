import Vue.*;

import Modele.User;

import javax.swing.*;

public class AccountOptionHandler {
    public static void handle(JFrame parentFrame, User currentUser) {
        if (currentUser == null) {
            new AuthApp().setVisible(true);
            parentFrame.dispose();
        } else {
            String[] options = {"Déconnexion", "Voir Profil"};
            int choice = JOptionPane.showOptionDialog(
                    parentFrame,
                    "Bonjour, " + currentUser.getNom() + "\nQue voulez-vous faire ?",
                    "Mon Compte",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                JOptionPane.showMessageDialog(parentFrame, currentUser.getNom() + ", vous avez été déconnecté avec succès.", "Déconnexion", JOptionPane.INFORMATION_MESSAGE);
                currentUser = null;
                new MainShopWindow().setVisible(true);
                parentFrame.dispose();
            } else if (choice == 1) {
                JOptionPane.showMessageDialog(parentFrame, "Nom : " + currentUser.getNom() + "\nEmail : " + currentUser.getEmail());
            }
        }
    }
}
