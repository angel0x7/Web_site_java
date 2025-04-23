package Vue;

import Dao.JdbcDataSource;
import Modele.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class AccountPage extends JPanel {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private User currentUser;

    public interface LoginSuccessListener {
        void onLoginSuccess(User user);
    }

    private LoginSuccessListener loginSuccessListener;

    public void setLoginSuccessListener(LoginSuccessListener listener) {
        this.loginSuccessListener = listener;
    }

    public AccountPage() {
        this(null);
    }

    public AccountPage(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel(" Votre Compte", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createHomeScreen(), "home");
        mainPanel.add(createLoginScreen(), "Se connecter");
        mainPanel.add(createSignUpScreen(), "Créer un compte");

        add(mainPanel, BorderLayout.CENTER);

        // Si l'utilisateur est connecté, affiche son profil directement
        if (currentUser != null) {
            mainPanel.add(createProfileScreen(), "Profil");
            cardLayout.show(mainPanel, "Profil");
        } else {
            cardLayout.show(mainPanel, "home");
        }
    }

    private JPanel createHomeScreen() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JButton loginButton = createStyledButton("Se connecter");
        JButton signUpButton = createStyledButton("Créer un compte");

        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "Se connecter"));
        signUpButton.addActionListener(e -> cardLayout.show(mainPanel, "Créer un compte"));

        panel.add(loginButton);
        panel.add(signUpButton);
        return panel;
    }

    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = createStyledButton("Connexion");

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            currentUser = JdbcDataSource.authenticateUser(email, password);

            if (currentUser != null) {
                JOptionPane.showMessageDialog(this, "Connexion réussie ! Bienvenue " + currentUser.getNom());

                if (loginSuccessListener != null) {
                    loginSuccessListener.onLoginSuccess(currentUser);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect !");
            }
        });

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        return panel;
    }

    private JPanel createSignUpScreen() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 20));

        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom:"));
        panel.add(prenomField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Mot de passe:"));
        panel.add(passwordField);

        JButton signUpButton = createStyledButton("Créer un compte");

        signUpButton.addActionListener(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis !");
                return;
            }

            if (emailExiste(email)) {
                JOptionPane.showMessageDialog(this, "Cet email est déjà utilisé !");
                return;
            }

            if (registerUser(nom, prenom, email, password)) {
                JOptionPane.showMessageDialog(this, "Inscription réussie !");
                currentUser = JdbcDataSource.authenticateUser(email, password);

                if (loginSuccessListener != null) {
                    loginSuccessListener.onLoginSuccess(currentUser);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription !");
            }
        });

        panel.add(new JLabel());
        panel.add(signUpButton);

        return panel;
    }

    private JPanel createProfileScreen() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel nameLabel = new JLabel(" Nom : " + currentUser.getNom(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JLabel emailLabel = new JLabel(" Email : " + currentUser.getEmail(), SwingConstants.CENTER);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton logoutButton = createStyledButton("Déconnexion");

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Vous avez été déconnecté.");
            if (loginSuccessListener != null) {
                loginSuccessListener.onLoginSuccess(null); // Déconnecte
            }
        });

        panel.add(nameLabel);
        panel.add(emailLabel);
        panel.add(new JLabel()); // Espace vide
        panel.add(logoutButton);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private boolean registerUser(String nom, String prenom, String email, String password) {
        String query = "INSERT INTO tab_client (nom, prenom, email, MotDePasse, date_inscription) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JdbcDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    public void showHomeScreen() {
        if (currentUser == null) {
            cardLayout.show(mainPanel, "home");
        }
    }
}
