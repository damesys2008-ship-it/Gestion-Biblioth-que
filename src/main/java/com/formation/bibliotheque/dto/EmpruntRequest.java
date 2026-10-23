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
    @NotNull(message = "L'identifiant du livre est obligatoire") // Refuse une requête sans identifiant de livre.
    private Long livreId; // Stocke l'identifiant du livre demandé.

    /** Nom du lecteur, obligatoire et limité à 100 caractères. */
    @NotBlank(message = "Le nom de l'emprunteur est obligatoire") // Exige un nom non vide.
    @Size(max = 100, message = "Le nom de l'emprunteur ne doit pas dépasser 100 caractères") // Limite la longueur du nom.
    private String emprunteur; // Stocke le nom communiqué par le client.

    /** Adresse email obligatoire et contrôlée au format email. */
    @NotBlank(message = "L'email est obligatoire") // Exige une adresse email renseignée.
    @Email(message = "L'email doit être valide") // Vérifie le format de l'adresse email.
    private String emailEmprunteur; // Stocke l'email utilisé en interne.

    /** Constructeur vide requis pour la désérialisation de la requête JSON. */
    public EmpruntRequest() { // Construit un objet vide que Jackson pourra remplir.
    }

    /** Retourne l'identifiant du livre demandé. */
    public Long getLivreId() { return livreId; } // Expose l'identifiant du livre.
    /** Reçoit l'identifiant du livre depuis le JSON de la requête. */
    public void setLivreId(Long livreId) { this.livreId = livreId; } // Enregistre l'identifiant reçu.

    /** Retourne le nom fourni pour l'emprunteur. */
    public String getEmprunteur() { return emprunteur; } // Expose le nom de l'emprunteur.
    /** Reçoit le nom de l'emprunteur depuis le JSON. */
    public void setEmprunteur(String emprunteur) { this.emprunteur = emprunteur; } // Enregistre le nom reçu.

    /** Retourne l'email fourni pour l'emprunteur. */
    public String getEmailEmprunteur() { return emailEmprunteur; } // Expose l'email au traitement interne.
    /** Reçoit l'email de l'emprunteur depuis le JSON. */
    public void setEmailEmprunteur(String emailEmprunteur) { this.emailEmprunteur = emailEmprunteur; } // Enregistre l'email reçu.
}
