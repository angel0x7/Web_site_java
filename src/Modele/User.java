package Modele;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private int panierId; // Nouvelle propriété pour stocker l'identifiant du panier

    public User(int id, String nom, String prenom, String email, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.panierId = -1; // Valeur par défaut si non récupérée
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public int getPanierId() {
        return panierId;
    }

    public void setPanierId(int panierId) { // Setter pour initialiser l'ID du panier
        this.panierId = panierId;
    }

    public void showUser() {
        System.out.println("User id : " + id);
        System.out.println("Nom : " + nom);
        System.out.println("Prenom : " + prenom);
        System.out.println("Email : " + email);
        System.out.println("Role : " + role);
        System.out.println("Panier ID : " + panierId); // Affiche l'identifiant du panier
    }
}