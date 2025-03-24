import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class SampleDB {
    public static void main(String[] args) {
        Connection conn = JdbcDataSource.getConnection();

        try {
            // 1️⃣ Insérer un client
            //JdbcDataSource.insertClient(77, "test2", "test2", "test2@gmail.com","test2");
            //JdbcDataSource.deleteClient(3); //sup de id 3
           // JdbcDataSource.insertClient(7, "Cristiano", "Ronaldo", "cristiano.ronaldo@gmail.com","the2ndgoat");
            // 2️⃣ Lire les clients
            //JdbcDataSource.insertAdmin("admin_user", "admin_user", "", "12345admin","ADMIN");
            JdbcDataSource.getAdmin();  // 🔥 La connexion doit être ouverte ici
            JdbcDataSource.getClients();

        } catch (Exception e) {
            System.err.println("❌ Erreur !");
            e.printStackTrace();
        } finally {

            JdbcDataSource.closeConnection();
        }
    }
}

