package com.formation.bibliotheque.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

/**
 * Données publiques renvoyées par l'API au sujet d'un emprunt.
 * L'adresse email est volontairement absente de la réponse.
 */
@JsonPropertyOrder({ // Fixe l'ordre des propriétés dans la réponse JSON.
        "id", "livreId", "emprunteur", "dateEmprunt", "dateRetourPrevue",
        "dateRetourEffective", "rendu", "enRetard"
})
public class EmpruntResponse {

    // Les propriétés finales rendent le DTO de réponse immuable.
    private final Long id; // Identifiant unique de l'emprunt.
    private final Long livreId; // Identifiant du livre emprunté.
    private final String emprunteur; // Nom public de la personne qui emprunte.
    private final LocalDate dateEmprunt; // Date de début de l'emprunt.
    private final LocalDate dateRetourPrevue; // Date limite prévue du retour.
    private final LocalDate dateRetourEffective; // Date réelle du retour, absente si non rendu.
    private final Boolean rendu; // État de retour du livre.
    private final Boolean enRetard; // Indique si un emprunt non rendu a dépassé sa date limite.

    /** Construit une réponse contenant les informations visibles de l'emprunt. */
    public EmpruntResponse(
            Long id,
            Long livreId,
            String emprunteur,
            LocalDate dateEmprunt,
            LocalDate dateRetourPrevue,
            LocalDate dateRetourEffective,
            Boolean rendu,
            Boolean enRetard) {
        this.id = id; // Copie l'identifiant dans le DTO immuable.
        this.livreId = livreId; // Copie le lien vers le livre.
        this.emprunteur = emprunteur; // Copie le nom visible dans l'API.
        this.dateEmprunt = dateEmprunt; // Copie la date de départ.
        this.dateRetourPrevue = dateRetourPrevue; // Copie la date d'échéance.
        this.dateRetourEffective = dateRetourEffective; // Copie la date effective, éventuellement absente.
        this.rendu = rendu; // Copie l'état de retour.
        this.enRetard = enRetard; // Copie l'indicateur de retard calculé.
    }

    /** Retourne l'identifiant de l'emprunt. */
    public Long getId() { return id; } // Retourne l'identifiant de l'emprunt.
    /** Retourne l'identifiant du livre concerné. */
    public Long getLivreId() { return livreId; } // Retourne l'identifiant du livre.
    /** Retourne le nom de l'emprunteur. */
    public String getEmprunteur() { return emprunteur; } // Retourne le nom public de l'emprunteur.
    /** Retourne la date à laquelle l'emprunt a commencé. */
    public LocalDate getDateEmprunt() { return dateEmprunt; } // Retourne la date de début.
    /** Retourne la date prévue pour le retour du livre. */
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; } // Retourne l'échéance prévue.
    /** Retourne la date effective du retour, ou null si le prêt est en cours. */
    public LocalDate getDateRetourEffective() { return dateRetourEffective; } // Retourne la date réelle de retour.
    /** Indique si le livre a été rendu. */
    public Boolean getRendu() { return rendu; } // Retourne l'état de retour.
    /** Indique si l'emprunt non rendu a dépassé sa date limite. */
    public Boolean getEnRetard() { return enRetard; } // Retourne l'indicateur de retard.
}
