package Dao;

// import des packages

import Modele.Marque;

import java.util.ArrayList;

public interface AdminMarqueDao {

    public ArrayList<Marque> getAll();

    public Marque getById(int id);

    public void ajouter(Marque marque);

    public Marque modifier(Marque marque);

    public void supprimer(int idMarque);
}
