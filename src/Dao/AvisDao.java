package Dao;


// import des packages


import Modele.Avis;


import java.util.ArrayList;


public interface AvisDao {


    public ArrayList<Avis> getAll();


    public Avis getById(int id);

    public ArrayList<Avis> getByUser(int idUser);

    public ArrayList<Avis> getByProduit(int idProduit);




    public void ajouter(Avis avis);

    public void supprimer(int isAvis);
}
