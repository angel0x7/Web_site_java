package Dao;

// import des packages

import Modele.Produit;
import Modele.Reduction;

import java.sql.*;
import java.util.ArrayList;


 //implémentation MySQL du stockage dans la base de données des méthodes définies dans l'interface

public class AdminReductionDaoImpl implements AdminReductionDao {
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private DaoFactory daoFactory;

    public AdminReductionDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override

     //Récupérer de la base de données tous les objets des produits dans une liste

    public ArrayList<Reduction> getAll() {
        ArrayList<Reduction> listeReductions = new  ArrayList<Reduction>();

        /*
            Récupérer la liste des reductions de la base de données dans listeProduits
        */
        try {
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            ResultSet resultats = statement.executeQuery("select * from reduction");

            while (resultats.next()) {
                int idReduction = resultats.getInt(1);
                String nomReduction = resultats.getString(2);
                double prix_vrac = resultats.getDouble(4);
                int quantite_vrac = resultats.getInt(3);
                int produit_id = resultats.getInt(5);

                // instancier un objet de Produit avec ces 3 champs en paramètres
                Reduction reduction = new Reduction(idReduction,nomReduction,quantite_vrac,prix_vrac,produit_id);

                listeReductions.add(reduction);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Extraction de la liste de reductions impossible");
        }

        return listeReductions;
    }

    @Override

     //Ajouter un nouveau produit en paramètre dans la base de données

    public void ajouter(Reduction reduction) {
        try {
            Connection connexion = daoFactory.getConnection();
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
            Connection connexion = daoFactory.getConnection();;
            Statement statement = connexion.createStatement();

            ResultSet resultats = statement.executeQuery("select * from reduction where id="+id);

            while (resultats.next()) {

                int idReduction = resultats.getInt(1);
                String nomReduction = resultats.getString(2);
                double prix_vrac = resultats.getDouble(4);
                int quantite_vrac = resultats.getInt(3);
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
            Connection connexion = daoFactory.getConnection();

            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "UPDATE reduction SET nom='"+reduction.getNom()+
                            "',prix_vrac="+reduction.getPrix_vrac()+",produit_id="+reduction.getProduit_id()+
                            ",quantite_vrac="+reduction.getQuantite_vrac()+" where id ="+old_reduction.getId());
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
