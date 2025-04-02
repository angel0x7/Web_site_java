package Dao;

// import des packages

import Modele.Produit;
import Modele.Reduction;

import java.sql.*;
import java.util.ArrayList;

/**
 * implémentation MySQL du stockage dans la base de données des méthodes définies dans l'interface
 * ProduitDao.
 */
public class AdminReductionDaoImpl implements AdminReductionDao {
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private DaoFactory daoFactory;

    public AdminReductionDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    /**
     * Récupérer de la base de données tous les objets des produits dans une liste
     * @return : liste retournée des objets des produits récupérés
     */
    public ArrayList<Reduction> getAll() {
        ArrayList<Reduction> listeReductions = new  ArrayList<Reduction>();

        /*
            Récupérer la liste des reductions de la base de données dans listeProduits
        */
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            // récupération des produits de la base de données avec la requete SELECT
            ResultSet resultats = statement.executeQuery("select * from reduction");

            // 	Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                // récupérer les 3 champs de la table produits dans la base de données
                int idReduction = resultats.getInt(1);
                String nomReduction = resultats.getString(2);
                double prix_vrac = resultats.getDouble(4);
                int quantite_vrac = resultats.getInt(3);
                int produit_id = resultats.getInt(5);

                // instancier un objet de Produit avec ces 3 champs en paramètres
                Reduction reduction = new Reduction(idReduction,nomReduction,quantite_vrac,prix_vrac,produit_id);

                // ajouter ce produit à listeProduits
                listeReductions.add(reduction);
            }
        }
        catch (SQLException e) {
            //traitement de l'exception
            e.printStackTrace();
            System.out.println("Extraction de la liste de reductions impossible");
        }

        return listeReductions;
    }

    @Override
    /**
     Ajouter un nouveau produit en paramètre dans la base de données
     @params : product = objet du Produit en paramètre à insérer dans la base de données
     */
    public void ajouter(Reduction reduction) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();
            // Exécution de la requête INSERT INTO de l'objet product en paramètre
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "insert into reduction(nom,prix_vrac,produit_id,quantite_vrac) values('" + reduction.getNom() + "'," +
                             reduction.getPrix_vrac() + "," + reduction.getProduit_id() + "," + reduction.getQuantite_vrac() + ")");
            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ajout de la réduction impossible");
        }
    }

    @Override
    public Reduction getById(int id) {
        Reduction reduction = null;
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            // Exécution de la requête SELECT pour récupérer le produit de l'id dans la base de données
            ResultSet resultats = statement.executeQuery("select * from reduction where id="+id);

            // 	Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                // récupérer les 3 champs de la table produits dans la base de données
                // récupération des 3 champs du produit de la base de données
                int idReduction = resultats.getInt(1);
                String nomReduction = resultats.getString(2);
                double prix_vrac = resultats.getDouble(3);
                int quantite_vrac = resultats.getInt(4);
                int produit_id = resultats.getInt(5);

                // Si l'id du produit est trouvé, l'instancier et sortir de la boucle
                if (id == idReduction) {
                    // instanciation de l'objet de Produit avec ces 3 champs
                     reduction = new Reduction(idReduction,nomReduction,quantite_vrac,prix_vrac,produit_id);
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Reduction non trouvé dans la base de données");
        }
        return reduction;
    }

    @Override
    public Reduction modifier(Reduction reduction) {
        Reduction old_reduction = getById(reduction.getId());
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();

            // Exécution de la requête INSERT INTO de l'objet product en paramètre
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "UPDATE reduction SET nom='"+reduction.getNom()+
                            "',prix_vrac="+reduction.getPrix_vrac()+",produit_id="+reduction.getProduit_id()+
                            ",quantite_vrac="+reduction.getQuantite_vrac());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Modification de la réduction impossible");
        }

        return reduction;
    }

    @Override
    public void supprimer(int idReduction) {
        try {
            // connexion
            Connection connexion = daoFactory.getConnection();

            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "DELETE FROM reduction WHERE id="+idReduction);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Suppression de la reduction impossible");
        }

    }
}
