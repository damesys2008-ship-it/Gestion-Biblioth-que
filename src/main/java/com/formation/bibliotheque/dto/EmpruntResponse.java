package com.formation.bibliotheque.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

/**
 * Données publiques renvoyées par l'API au sujet d'un emprunt.
 * L'adresse email est volontairement absente de la réponse.
 */
@JsonPropertyOrder({
        "id", "livreId", "emprunteur", "dateEmprunt", "dateRetourPrevue",
        "dateRetourEffective", "rendu", "enRetard"
})
public class EmpruntResponse {

    // Les propriétés finales rendent le DTO de réponse immuable.
    private final Long id;
    private final Long livreId;
    private final String emprunteur;
    private final LocalDate dateEmprunt;
    private final LocalDate dateRetourPrevue;
    private final LocalDate dateRetourEffective;
    private final Boolean rendu;
    private final Boolean enRetard;

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
        this.id = id;
        this.livreId = livreId;
        this.emprunteur = emprunteur;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.rendu = rendu;
        this.enRetard = enRetard;
    }

    /** Retourne l'identifiant de l'emprunt. */
    public Long getId() { return id; }
    /** Retourne l'identifiant du livre concerné. */
    public Long getLivreId() { return livreId; }
    /** Retourne le nom de l'emprunteur. */
    public String getEmprunteur() { return emprunteur; }
    /** Retourne la date à laquelle l'emprunt a commencé. */
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    /** Retourne la date prévue pour le retour du livre. */
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    /** Retourne la date effective du retour, ou null si le prêt est en cours. */
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    /** Indique si le livre a été rendu. */
    public Boolean getRendu() { return rendu; }
    /** Indique si l'emprunt non rendu a dépassé sa date limite. */
    public Boolean getEnRetard() { return enRetard; }
}
