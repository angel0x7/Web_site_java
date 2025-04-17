package Dao;


// import des packages


import Modele.Avis;


import java.sql.*;
import java.util.ArrayList;


/**
 * implémentation MySQL du stockage dans la base de données des méthodes définies dans l'interface
 * ProduitDao.
 */
public class AvisDaoImpl implements AvisDao {
    private DaoFactory daoFactory;


    public AvisDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }


    @Override
    /**
     * Récupérer de la base de données tous les objets des produits dans une liste
     * @return : liste retournée des objets des produits récupérés
     */
    public ArrayList<Avis> getAll() {
        ArrayList<Avis> listeAvis = new  ArrayList<Avis>();




        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();


            ResultSet resultats = statement.executeQuery("select * from avis");


            while (resultats.next()) {
                int id = resultats.getInt(1);
                String titre = resultats.getString(2);
                int note = resultats.getInt(3);
                String description = resultats.getString(4);
                int produitId = resultats.getInt(5);
                int userId = resultats.getInt(6);
                Avis avis = new Avis(id,titre,note,description,produitId,userId);
                listeAvis.add(avis);
            }
        }
        catch (SQLException e) {
            //traitement de l'exception
            e.printStackTrace();
            System.out.println("Extraction de la liste des avis impossible");
        }


        return listeAvis;
    }


    @Override
    public void ajouter(Avis avis) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "insert into avis(titre,note,description,produit_id,user_id) values('" + avis.getTitre() + "'," +
                            avis.getNote() + ",'" + avis.getDescription() +"',"+avis.getProduitId()+","+avis.getClientId() +")");
            preparedStatement.executeUpdate();


        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ajout de l'avis impossible");
        }
    }


    @Override
    public Avis getById(int id) {
        Avis avis = null;
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();


            ResultSet resultats = statement.executeQuery("select * from avis where id="+id);


            //  Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                int idAvis = resultats.getInt(1);
                String titre = resultats.getString(2);
                int note = resultats.getInt(3);
                String description = resultats.getString(4);
                int produitId = resultats.getInt(5);
                int userId = resultats.getInt(6);
                if (id == idAvis) {
                    // instanciation de l'objet de Produit avec ces 3 champs
                    avis = new Avis(id,titre,note,description,produitId,userId);
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Avis non trouvé dans la base de données");
        }
        return avis;
    }


    @Override
    public ArrayList<Avis> getByUser(int idUser) {
        ArrayList<Avis> listeAvis = new  ArrayList<Avis>();
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();


            ResultSet resultats = statement.executeQuery("select * from avis where user_id="+idUser);


            while (resultats.next()) {
                int id = resultats.getInt(1);
                String titre = resultats.getString(2);
                int note = resultats.getInt(3);
                String description = resultats.getString(4);
                int produitId = resultats.getInt(5);
                int userId = resultats.getInt(6);
                Avis avis = new Avis(id,titre,note,description,produitId,userId);
                listeAvis.add(avis);
            }
        }
        catch (SQLException e) {
            //traitement de l'exception
            e.printStackTrace();
            System.out.println("Extraction de la liste des avis impossible");
        }
        return listeAvis;
    }


    @Override
    public ArrayList<Avis> getByProduit(int idProduit) {
        ArrayList<Avis> listeAvis = new  ArrayList<Avis>();
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();
            ResultSet resultats = statement.executeQuery("select * from avis  where produit_id="+idProduit);


            while (resultats.next()) {
                int id = resultats.getInt(1);
                String titre = resultats.getString(2);
                int note = resultats.getInt(3);
                String description = resultats.getString(4);
                int produitId = resultats.getInt(5);
                int userId = resultats.getInt(6);
                Avis avis = new Avis(id,titre,note,description,produitId,userId);
                listeAvis.add(avis);
            }
        }
        catch (SQLException e) {
            //traitement de l'exception
            e.printStackTrace();
            System.out.println("Extraction de la liste des avis impossible");
        }


        return listeAvis;
    }


    @Override
    public void supprimer(int idAvis) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();


            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "DELETE FROM avis WHERE id="+idAvis);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Suppression de l'avis impossible");
        }
    }
}
