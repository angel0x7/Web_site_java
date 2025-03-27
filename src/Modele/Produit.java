package Modele;

public class Produit {

    private int idProduit;
    private String nomProduit;
    private String description;
    private int quantite;
    private double prix;
    private String image;
    private String categorie;
    private int idMarque;

    public Produit(int idProduit,String nomProduit, String description, int quantite, double prix, String image, String categorie, int idMarque) {
        this.idProduit = idProduit;
        this.nomProduit = nomProduit;
        this.description = description;
        this.quantite = quantite;
        this.prix = prix;
        this.image = image;
        this.categorie = categorie;
        this.idMarque = idMarque;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIdMarque() {
        return idMarque;
    }

    public void setIdMarque(int idMarque) {
        this.idMarque = idMarque;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
}
