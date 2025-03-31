package Dao;

// import des packages
import Modele.Produit;
import java.util.ArrayList;

public interface AdminProduitDao {

    public ArrayList<Produit> getAll();

    public Produit getById(int id);

    public void ajouter(Produit product);

    public Produit modifier(Produit product);

    public void supprimer(int idProduit);
}
