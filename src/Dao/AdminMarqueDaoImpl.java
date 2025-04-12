package Dao;

// import des packages

import Modele.Marque;
import Modele.Produit;

import java.sql.*;
import java.util.ArrayList;

/**
 * implémentation MySQL du stockage dans la base de données des méthodes définies dans l'interface
 * ProduitDao.
 */
public class AdminMarqueDaoImpl implements AdminMarqueDao {
    private DaoFactory daoFactory;

    public AdminMarqueDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    /**
     * Récupérer de la base de données tous les objets des produits dans une liste
     * @return : liste retournée des objets des produits récupérés
     */
    public ArrayList<Marque> getAll() {
        ArrayList<Marque> listeMarques = new  ArrayList<Marque>();

        /*
            Récupérer la liste des produits de la base de données dans listeProduits
        */
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            // récupération des produits de la base de données avec la requete SELECT
            ResultSet resultats = statement.executeQuery("select * from marque");

            // 	Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                // récupérer les 3 champs de la table produits dans la base de données
                int idMarque = resultats.getInt(1);
                String nom = resultats.getString(2);
                String image = resultats.getString(3);
                String description = resultats.getString(4);


                // instancier un objet de Produit avec ces 3 champs en paramètres
                Marque marque = new Marque(idMarque,nom,image,description);

                // ajouter ce produit à listeProduits
                listeMarques.add(marque);
            }
        }
        catch (SQLException e) {
            //traitement de l'exception
            e.printStackTrace();
            System.out.println("Extraction de la liste de produits impossible");
        }

        return listeMarques;
    }

    @Override
    /**
     Ajouter un nouveau produit en paramètre dans la base de données
     @params : product = objet du Produit en paramètre à insérer dans la base de données
     */
    public void ajouter(Marque marque) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();
            // Exécution de la requête INSERT INTO de l'objet product en paramètre
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "insert into marque(nom,image,description) values('" + marque.getNom() + "','" +
                            marque.getImage() + "','" + marque.getDescription() + "')");
            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ajout du produit impossible");
        }
    }

    @Override
    public Marque getById(int id) {
        Marque marque = null;
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            // Exécution de la requête SELECT pour récupérer le produit de l'id dans la base de données
            ResultSet resultats = statement.executeQuery("select * from marque where id="+id);

            // 	Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                // récupérer les 3 champs de la table produits dans la base de données
                // récupération des 3 champs du produit de la base de données
                int idMarque = resultats.getInt(1);
                String nom = resultats.getString(2);
                String image = resultats.getString(3);
                String description = resultats.getString(4);

                // Si l'id du produit est trouvé, l'instancier et sortir de la boucle
                if (id == idMarque) {
                    // instanciation de l'objet de Produit avec ces 3 champs
                     marque = new Marque(idMarque,nom,image,description);
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Produit non trouvé dans la base de données");
        }
        return marque;
    }

    @Override
    public Marque modifier(Marque marque) {
        Marque old_marque = getById(marque.getId());
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();

            // Exécution de la requête INSERT INTO de l'objet product en paramètre
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "UPDATE marque SET nom='"+marque.getNom()+
                            "',image='"+marque.getImage()+"',description='"+marque.getDescription()+"'");
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Modification du produit impossible");
        }

        return marque;
    }

    @Override
    public void supprimer(int idMarque) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();

            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "DELETE FROM marque WHERE id="+idMarque);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Suppression du produit impossible");
        }

    }
}
