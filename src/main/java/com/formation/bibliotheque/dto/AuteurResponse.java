package com.formation.bibliotheque.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO renvoyé par l'API pour exposer les informations d'un auteur.
 * L'ordre des propriétés JSON est fixé pour garder une réponse stable.
 */
@JsonPropertyOrder({"id", "nom", "prenom", "nationalite"})
public class AuteurResponse {

    // Les champs finaux rendent cette réponse immuable après sa création.
    private final Long id;
    private final String nom;
    private final String prenom;
    private final String nationalite;

    /** Construit la réponse à partir des données de l'auteur. */
    public AuteurResponse(Long id, String nom, String prenom, String nationalite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.nationalite = nationalite;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNationalite() { return nationalite; }
}
