import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class SampleDB {
    public static void main(String[] args) {
        Connection conn = JdbcDataSource.getConnection();

        try {
            // 1️⃣ Insérer un client
            //JdbcDataSource.insertClient(10, "Leo", "Messi", "leo.messi@gmail.com","thegoat");
            //JdbcDataSource.deleteClient(3); //sup de id 3
           // JdbcDataSource.insertClient(7, "Cristiano", "Ronaldo", "cristiano.ronaldo@gmail.com","the2ndgoat");
            // 2️⃣ Lire les clients
            JdbcDataSource.insertAdmin("admin_user", "admin_user", "", "12345admin","ADMIN");
            JdbcDataSource.getAdmin();  // 🔥 La connexion doit être ouverte ici

        } catch (Exception e) {
            System.err.println("❌ Erreur !");
            e.printStackTrace();
        } finally {

            JdbcDataSource.closeConnection();
        }
    }
}

