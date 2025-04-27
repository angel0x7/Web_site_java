package Dao;

// import des packages

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


 //La DAO Factory  permet d'initialiser le DAO en chargeant notamment les drivers nécessaires et se connecte à la base de données

public class DaoFactory {

     //Attributs private pour la connexion JDBC

    private static String url ;
    private static String username="root";
    private static String password="";

    public DaoFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


     //Méthode qui retourne 1 objet de DaoFactory

    public static DaoFactory getInstance(String database, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Erreur de connexion à la base de données");
        }

        url = "jdbc:mysql://localhost:3306/" + database;

        // Instancier une instance l'objet de DaoFactory
        DaoFactory instance = new DaoFactory(url, username,password );

        return instance;
    }


    public static Connection getConnection() throws SQLException {
        // Retourner la connection du driver de la base de données
        return DriverManager.getConnection(url, username, password);
    }


    public AdminProduitDao getAdminProduitDAO() {
        // Retourner un objet de ProduitDAOImpl qui implémente ProduitDAO
        return new AdminProduitDaoImpl();
    }


    public void disconnect() {
        Connection connexion = null;

        try {
            // création d'un ordre SQL
            connexion = this.getConnection();
            connexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur de déconnexion à la base de données");
        }
    }
}