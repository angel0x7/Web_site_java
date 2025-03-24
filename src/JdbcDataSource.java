import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcDataSource {

    private static Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/user";
    private static final String USER = "root";  // Remplacez par votre utilisateur MySQL
    private static final String PASSWORD = "";  // Remplacez par votre mot de passe MySQL

    private JdbcDataSource() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connexion à la BDD
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie à la base de données.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trouvé !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données !");
            e.printStackTrace();
        }
    }

    public static synchronized Connection getConnection() {
        if (connection == null) {
            new JdbcDataSource();
        }
        return connection;
    }


    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion !");
            e.printStackTrace();
        }
    }

    public static void insertClient(int id, String nom, String prenom, String email, String motDePasse) {
        String query = "INSERT INTO tab_client (id, nom, prenom, email, MotDePasse, date_inscription) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, nom);
            pstmt.setString(3, prenom);
            pstmt.setString(4, email);
            pstmt.setString(5, motDePasse); // Ajout du mot de passe
            pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // Date actuelle

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Client ajouté avec succès !");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion !");
            e.printStackTrace();
        }
    }

    public static void deleteClient(int id) {
        String query = "DELETE FROM tab_client WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Client supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun client trouvé avec l'ID " + id);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du client !");
            e.printStackTrace();
        }
    }


    public static void getClients() {
        String query = "SELECT * FROM tab_client";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Nom: " + rs.getString("nom") +
                        ", Prénom: " + rs.getString("prenom") +
                        ", Email: " + rs.getString("email") +
                        ", MoDePasse: " + rs.getString("MotDePasse") +
                        ", Inscription: " + rs.getTimestamp("date_inscription"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des clients !");
            e.printStackTrace();
        }
    }
    public static void insertAdmin(String nom, String prenom, String email, String motDePasse, String role) {
        String query = "INSERT INTO tab_admin (nom, prenom, email, MotDePasse, role) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setString(4, motDePasse); // Idéalement hashé
            pstmt.setString(5, role);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Administrateur ajouté avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'administrateur !");
            e.printStackTrace();
        }
    }
    public static void getAdmin() {
        String query = "SELECT * FROM tab_admin";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Nom: " + rs.getString("nom") +
                        ", Prénom: " + rs.getString("prenom") +
                        ", Email: " + rs.getString("email") +
                        ", MoDePasse: " + rs.getString("MotDePasse") +
                                ", Role: " + rs.getString("role") +
                        ", Inscription: " + rs.getTimestamp("date_inscription"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des administrateurs !");
            e.printStackTrace();
        }
    }
    public static boolean authenticateAdmin(String email, String password) {
        String query = "SELECT * FROM tab_admin WHERE email = ? AND MotDePasse = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Retourne true si un admin est trouvé

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'authentification de l'administrateur !");
            e.printStackTrace();
            return false;
        }
    }
    public static User authenticateUser(String email, String password) {
        String queryClient = "SELECT id, nom, prenom, email FROM tab_client WHERE email = ? AND MotDePasse = ?";
        String queryAdmin = "SELECT id, nom, prenom, email FROM tab_admin WHERE email = ? AND MotDePasse = ?";

        try (Connection conn = JdbcDataSource.getConnection();
             PreparedStatement pstmtClient = conn.prepareStatement(queryClient);
             PreparedStatement pstmtAdmin = conn.prepareStatement(queryAdmin)) {

            pstmtClient.setString(1, email);
            pstmtClient.setString(2, password);
            ResultSet rsClient = pstmtClient.executeQuery();

            if (rsClient.next()) {
                return new User(rsClient.getInt("id"), rsClient.getString("nom"), rsClient.getString("prenom"), rsClient.getString("email"), "CLIENT");
            }

            pstmtAdmin.setString(1, email);
            pstmtAdmin.setString(2, password);
            ResultSet rsAdmin = pstmtAdmin.executeQuery();

            if (rsAdmin.next()) {
                return new User(rsAdmin.getInt("id"), rsAdmin.getString("nom"), rsAdmin.getString("prenom"), rsAdmin.getString("email"), "ADMIN");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }






}
