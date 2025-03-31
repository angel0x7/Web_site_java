import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcDataSource {

    private static Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/shopping";
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

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie à la base de données.");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
            return null;
        }
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
    public static void insertAdmin(String adminName, String email, String motDePasse) {
        String query = "INSERT INTO admin (adminName, email, password, date_inscription) VALUES (?, ?, ?, ?)";
        LocalDate myObj = LocalDate.now();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, adminName);
            pstmt.setString(2, email);
            pstmt.setString(3, motDePasse); // Idéalement hashé
            pstmt.setString(4, myObj.toString());
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

        Connection conn = null;
        PreparedStatement pstmtClient = null;
        PreparedStatement pstmtAdmin = null;
        ResultSet rsClient = null;
        ResultSet rsAdmin = null;

        try {
            conn = JdbcDataSource.getConnection();

            if (conn == null || conn.isClosed()) {  // ✅ Vérification de la connexion
                System.err.println("❌ Connexion à la base de données indisponible !");
                return null;
            }

            // Vérification dans la table "tab_client"
            pstmtClient = conn.prepareStatement(queryClient);
            pstmtClient.setString(1, email);
            pstmtClient.setString(2, password);
            rsClient = pstmtClient.executeQuery();

            if (rsClient.next()) {
                return new User(rsClient.getInt("id"), rsClient.getString("nom"), rsClient.getString("prenom"),
                        rsClient.getString("email"), "CLIENT");
            }

            // Vérification dans la table "tab_admin"
            pstmtAdmin = conn.prepareStatement(queryAdmin);
            pstmtAdmin.setString(1, email);
            pstmtAdmin.setString(2, password);
            rsAdmin = pstmtAdmin.executeQuery();

            if (rsAdmin.next()) {
                return new User(rsAdmin.getInt("id"), rsAdmin.getString("nom"), rsAdmin.getString("prenom"),
                        rsAdmin.getString("email"), "ADMIN");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rsClient != null) rsClient.close();
                if (rsAdmin != null) rsAdmin.close();
                if (pstmtClient != null) pstmtClient.close();
                if (pstmtAdmin != null) pstmtAdmin.close();
                if (conn != null) conn.close();  // ✅ Fermeture de la connexion après utilisation
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;  // Aucun utilisateur trouvé
    }






}
