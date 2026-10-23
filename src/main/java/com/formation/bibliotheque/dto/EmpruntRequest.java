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

    @NotNull(message = "L'identifiant du livre est obligatoire")
    private Long livreId;

    @NotBlank(message = "Le nom de l'emprunteur est obligatoire")
    @Size(max = 100, message = "Le nom de l'emprunteur ne doit pas dépasser 100 caractères")
    private String emprunteur;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String emailEmprunteur;

    /** Constructeur vide requis pour la désérialisation de la requête JSON. */
    public EmpruntRequest() {
    }

    public Long getLivreId() { return livreId; }
    public void setLivreId(Long livreId) { this.livreId = livreId; }

    public String getEmprunteur() { return emprunteur; }
    public void setEmprunteur(String emprunteur) { this.emprunteur = emprunteur; }

    public String getEmailEmprunteur() { return emailEmprunteur; }
    public void setEmailEmprunteur(String emailEmprunteur) { this.emailEmprunteur = emailEmprunteur; }
}
