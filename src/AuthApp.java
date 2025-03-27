import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AuthApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;

    public AuthApp() {
        setTitle("Authentification");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Écrans
        mainPanel.add(createHomeScreen(), "home");
        mainPanel.add(createLoginScreen(), "login");
        mainPanel.add(createSignUpScreen(), "signup");

        add(mainPanel);
    }

    private JPanel createHomeScreen() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");

        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        signUpButton.addActionListener(e -> cardLayout.show(mainPanel, "signup"));

        panel.add(loginButton);
        panel.add(signUpButton);
        return panel;
    }


    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Connexion");

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            currentUser = JdbcDataSource.authenticateUser(email, password);

            if (currentUser != null) {
                JOptionPane.showMessageDialog(this, "✅ Connexion réussie ! Bienvenue " + currentUser.getNom());

                // 🔥 Ouvrir la nouvelle fenêtre de la boutique
                new MainShopWindow(currentUser).setVisible(true);
                dispose(); // Fermer la fenêtre de connexion

            } else {
                JOptionPane.showMessageDialog(this, "❌ Email ou mot de passe incorrect !");
            }
        });


        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Espace
        panel.add(loginButton);

        return panel;
    }



    // Écran Sign Up (Amélioré)
    private JPanel createSignUpScreen() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JLabel nomLabel = new JLabel("Nom:");
        JTextField nomField = new JTextField();
        JLabel prenomLabel = new JLabel("Prénom:");
        JTextField prenomField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField();
        JButton signUpButton = new JButton("Créer un compte");

        signUpButton.addActionListener(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Vérification des champs
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Tous les champs doivent être remplis !");
                return;
            }

            /*if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "❌ Le mot de passe doit contenir au moins 6 caractères !");
                return;
            }*/

            // Vérifier si l'email est déjà utilisé
            if (emailExiste(email)) {
                JOptionPane.showMessageDialog(this, "❌ Cet email est déjà utilisé !");
                return;
            }

            // Enregistrement en base
            if (registerUser(nom, prenom, email, password)) {
                JOptionPane.showMessageDialog(this, "✅ Inscription réussie !");

                // Récupérer l'utilisateur depuis la base pour l'ouvrir dans MainShopWindow
                currentUser = JdbcDataSource.authenticateUser(email, password);

                if (currentUser != null) {
                    new MainShopWindow(currentUser).setVisible(true);
                    dispose(); // Fermer la fenêtre d'inscription
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Erreur lors de la récupération de l'utilisateur !");
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ Une erreur est survenue lors de l'inscription !");
            }
        });

        panel.add(nomLabel);
        panel.add(nomField);
        panel.add(prenomLabel);
        panel.add(prenomField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Espace vide
        panel.add(signUpButton);

        return panel;
    }



    private void showLogoutScreen() {
        String message = "Voulez-vous vous déconnecter ?";
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            message = "Voulez-vous vous déconnecter, administrateur " + currentUser.getNom() + " ?";
        }

        int response = JOptionPane.showConfirmDialog(this, message, "Déconnexion", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            currentUser = null; // Déconnexion
            cardLayout.show(mainPanel, "home");
        }
    }


    private boolean authenticateUser(String email, String password) {
        String query = "SELECT * FROM tab_client WHERE email = ? AND MotDePasse = ?";

        try (Connection conn = JdbcDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Si un utilisateur est trouvé, connexion réussie

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



        private boolean registerUser(String nom, String prenom, String email, String password) {
            String query = "INSERT INTO tab_client (nom, prenom, email, MotDePasse, date_inscription) VALUES (?, ?, ?, ?, ?)";

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = JdbcDataSource.getConnection();

                if (conn == null || conn.isClosed()) {  // ✅ Vérification de la connexion
                    System.err.println("❌ Connexion à la base de données fermée ou indisponible !");
                    return false;
                }

                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nom);
                pstmt.setString(2, prenom);
                pstmt.setString(3, email);
                pstmt.setString(4, password);
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (pstmt != null) pstmt.close();  // ✅ Fermer le PreparedStatement après exécution
                    if (conn != null) conn.close();   // ✅ Fermer la connexion après usage
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }



    private boolean emailExiste(String email) {
        String query = "SELECT COUNT(*) FROM tab_client WHERE email = ?";

        try (Connection conn = JdbcDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            AuthApp app = new AuthApp();
            app.setVisible(true);

        });
    }
}
