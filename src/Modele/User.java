package Modele;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String role;

    public User(int id, String nom, String prenom, String email, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
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
    public void showUser(){
        System.out.println("User id : " + id);
        System.out.println("Nom : " + nom);
        System.out.println("Prenom : " + prenom);
        System.out.println("Email : " + email);
        System.out.println("Role : " + role);
    }
}
