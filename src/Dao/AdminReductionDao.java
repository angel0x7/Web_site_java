package Dao;

// import des packages

import Modele.Reduction;

import java.util.ArrayList;

public interface AdminReductionDao {

    public ArrayList<Reduction> getAll();

    public Reduction getById(int id);

    public void ajouter(Reduction reduction);

    public Reduction modifier(Reduction reduction);

    public void supprimer(int idReduction);
}
