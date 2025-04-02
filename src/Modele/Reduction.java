package Modele;

public class Reduction {



    private int id;
   private String nom;
   private int quantite_vrac;
   private double prix_vrac;
   private int produit_id;

   public Reduction() {};

   public Reduction(int id,String nom, int quantite_vrac, double prix_vrac, int produit_id) {
       this.nom = nom;
       this.quantite_vrac = quantite_vrac;
       this.prix_vrac = prix_vrac;
       this.produit_id = produit_id;
   }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantite_vrac() {
        return quantite_vrac;
    }

    public void setQuantite_vrac(int quantite_vrac) {
        this.quantite_vrac = quantite_vrac;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public double getPrix_vrac() {
        return prix_vrac;
    }

    public void setPrix_vrac(double prix_vrac) {
        this.prix_vrac = prix_vrac;
    }
}
