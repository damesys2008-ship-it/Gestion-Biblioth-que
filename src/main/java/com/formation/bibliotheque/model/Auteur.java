package com.formation.bibliotheque.model;

import java.util.Objects;

public class Auteur {

    private int id;
    private String nom;
    private String prenom;
    private String nationalite;
    private String remarque;

public Auteur() {
    }
public Auteur(String nom, String prenom, String nationalite, String remarque) {
        this.nom = nom;
        this.prenom = prenom;
        this.nationalite = nationalite;
        this.remarque = remarque;
    }
    public Auteur(int id, String nom, String prenom, String nationalite, String remarque) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.nationalite = nationalite;
        this.remarque = remarque;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    // ===== equals / hashCode (basés sur l'id) =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Auteur)) return false;
        Auteur auteur = (Auteur) o;
        return id == auteur.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ===== toString =====

    @Override
    public String toString() {
        return "Auteur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", nationalite='" + nationalite + '\'' +
                ", remarque='" + remarque + '\'' +
                '}';
    }
}