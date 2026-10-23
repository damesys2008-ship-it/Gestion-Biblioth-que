package com.formation.bibliotheque.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Données qu'un client doit fournir pour demander un emprunt.
 * Les dates et l'état de retour sont définis par le service, pas par le client.
 */
public class EmpruntRequest {

    /** Livre concerné ; obligatoire pour pouvoir enregistrer la demande. */
    @NotNull(message = "L'identifiant du livre est obligatoire")
    private Long livreId;

    /** Nom du lecteur, obligatoire et limité à 100 caractères. */
    @NotBlank(message = "Le nom de l'emprunteur est obligatoire")
    @Size(max = 100, message = "Le nom de l'emprunteur ne doit pas dépasser 100 caractères")
    private String emprunteur;

    /** Adresse email obligatoire et contrôlée au format email. */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String emailEmprunteur;

    /** Constructeur vide requis pour la désérialisation de la requête JSON. */
    public EmpruntRequest() {
    }

    /** Retourne l'identifiant du livre demandé. */
    public Long getLivreId() { return livreId; }
    /** Reçoit l'identifiant du livre depuis le JSON de la requête. */
    public void setLivreId(Long livreId) { this.livreId = livreId; }

    /** Retourne le nom fourni pour l'emprunteur. */
    public String getEmprunteur() { return emprunteur; }
    /** Reçoit le nom de l'emprunteur depuis le JSON. */
    public void setEmprunteur(String emprunteur) { this.emprunteur = emprunteur; }

    /** Retourne l'email fourni pour l'emprunteur. */
    public String getEmailEmprunteur() { return emailEmprunteur; }
    /** Reçoit l'email de l'emprunteur depuis le JSON. */
    public void setEmailEmprunteur(String emailEmprunteur) { this.emailEmprunteur = emailEmprunteur; }
}
