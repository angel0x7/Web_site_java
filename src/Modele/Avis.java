
package Modele;


public class Avis {


    private int id;
    private String titre;
    private int note;
    private String description;
    private int clientId ;
    private int produitId;


    public Avis() {}


    public Avis(int id, String titre, int note, String description, int produitId, int clientId) {
        this.id = id;
        this.titre = titre;
        this.note = note;
        this.description = description;
        this.produitId = produitId;
        this.clientId = clientId;


    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getTitre() {
        return titre;
    }


    public void setTitre(String titre) {
        this.titre = titre;
    }


    public int getNote() {
        return note;
    }


    public void setNote(int note) {
        this.note = note;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
    public int getClientId() {
        return clientId;
    }


    public void setClientId(int clientId) {
        this.clientId = clientId;
    }


    public int getProduitId() {
        return produitId;
    }


    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }




}
