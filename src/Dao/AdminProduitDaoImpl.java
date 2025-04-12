package Dao;

// import des packages
import Modele.Produit;
import java.sql.*;
import java.util.ArrayList;

/**
 * implémentation MySQL du stockage dans la base de données des méthodes définies dans l'interface
 * ProduitDao.
 */
public class AdminProduitDaoImpl implements AdminProduitDao {
    private DaoFactory daoFactory;

    public AdminProduitDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    /**
     * Récupérer de la base de données tous les objets des produits dans une liste
     * @return : liste retournée des objets des produits récupérés
     */
    public ArrayList<Produit> getAll() {
        ArrayList<Produit> listeProduits = new  ArrayList<Produit>();

        /*
            Récupérer la liste des produits de la base de données dans listeProduits
        */
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            // récupération des produits de la base de données avec la requete SELECT
            ResultSet resultats = statement.executeQuery("select * from produit");

            // 	Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                // récupérer les 3 champs de la table produits dans la base de données
                int idProduit = resultats.getInt(1);
                String produitNom = resultats.getString(2);
                String image = resultats.getString(3);
                int idImage = resultats.getInt(4);
                double prix = resultats.getDouble(5);
                int quantite = resultats.getInt(6);
                String description = resultats.getString(7);
                String categorie = resultats.getString(8);


                // instancier un objet de Produit avec ces 3 champs en paramètres
                Produit product = new Produit(idProduit,produitNom,description,quantite,prix,image,categorie,idImage);

                // ajouter ce produit à listeProduits
                listeProduits.add(product);
            }
        }
        catch (SQLException e) {
            //traitement de l'exception
            e.printStackTrace();
            System.out.println("Extraction de la liste de produits impossible");
        }

        return listeProduits;
    }

    @Override
    /**
     Ajouter un nouveau produit en paramètre dans la base de données
     @params : product = objet du Produit en paramètre à insérer dans la base de données
     */
    public void ajouter(Produit product) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();
            // Exécution de la requête INSERT INTO de l'objet product en paramètre
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "insert into produit(nom,image,marque_id,prix,quantite,description,category) values('" + product.getNomProduit() + "','" +
                            product.getImage() + "'," + product.getIdMarque() + "," + product.getPrix() + ",'" + product.getQuantite() + "','"
                            + product.getDescription()+"" + "','"+ product.getCategorie() +"')");
            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ajout du produit impossible");
        }
    }

    @Override
    public Produit getById(int id) {
        Produit product = null;
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            // Exécution de la requête SELECT pour récupérer le produit de l'id dans la base de données
            ResultSet resultats = statement.executeQuery("select * from produit where id="+id);

            // 	Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                // récupérer les 3 champs de la table produits dans la base de données
                // récupération des 3 champs du produit de la base de données
                int idProduit = resultats.getInt(1);
                String produitNom = resultats.getString(2);
                String image = resultats.getString(3);
                int idImage = resultats.getInt(4);
                double prix = resultats.getDouble(5);
                int quantite = resultats.getInt(6);
                String description = resultats.getString(7);
                String categorie = resultats.getString(8);

                // Si l'id du produit est trouvé, l'instancier et sortir de la boucle
                if (id == idProduit) {
                    // instanciation de l'objet de Produit avec ces 3 champs
                    product = new Produit(idProduit,produitNom,description,quantite,prix,image,categorie,idImage);
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Produit non trouvé dans la base de données");
        }
        return product;
    }

    @Override
    public Produit modifier(Produit product) {
        Produit old_produit = getById(product.getIdProduit());
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();

            // Exécution de la requête INSERT INTO de l'objet product en paramètre
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "UPDATE produit SET nom='"+product.getNomProduit()+
                            "',prix="+product.getPrix()+",image='"+product.getImage()+
                            "',marque_id="+product.getIdMarque()+",quantite="+product.getQuantite()+
                            ",descritpion='"+product.getDescription()+"',category='"+product.getCategorie()+
                            "' WHERE id="+product.getIdProduit());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Modification du produit impossible");
        }

        return product;
    }

    @Override
    public void supprimer(int idProduct) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();

            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "DELETE FROM produit WHERE id="+idProduct);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Suppression du produit impossible");
        }

    }
}