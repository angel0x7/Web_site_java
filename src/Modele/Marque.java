package Modele;

public class Marque {


    private int id;
    private String nom;
    private String image;
    private String description;

    public Marque() {}

    public Marque(int id, String nom, String image, String description) {
        this.id = id;
        this.nom = nom;
        this.image = image;
        this.description = description;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
