package com.formation.bibliotheque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Entité JPA représentant un emprunt et ses dates de suivi.
 * livreId est conservé comme identifiant simple, sans relation JPA vers Livre.
 */
@Entity // Déclare cette classe comme entité persistée par JPA.
@Table(name = "emprunts") // Associe l'entité à la table emprunts.
public class Emprunt {

    /** Identifiant attribué automatiquement par la base de données. */
    @Id // Marque la clé primaire de l'entité.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Confie la génération de l'identifiant à la base.
    private Long id; // Identifiant unique de l'emprunt.

    /** Identifiant du livre concerné par l'emprunt. */
    @Column(nullable = false) // Rend l'identifiant du livre obligatoire en base.
    private Long livreId; // Identifiant simple du livre concerné.

    /** Nom de la personne qui emprunte le livre. */
    @Column(nullable = false) // Rend le nom obligatoire en base.
    private String emprunteur; // Nom de la personne qui emprunte.

    /** Email conservé dans l'entité mais non exposé dans la réponse publique. */
    @Column(nullable = false) // Rend l'email obligatoire en base.
    private String emailEmprunteur; // Email conservé pour les besoins internes.

    /** Date à laquelle l'emprunt a commencé. */
    @Column(nullable = false) // Rend la date de début obligatoire en base.
    private LocalDate dateEmprunt; // Date à laquelle l'emprunt commence.

    /** Date limite prévue pour le retour du livre. */
    @Column(nullable = false) // Rend l'échéance obligatoire en base.
    private LocalDate dateRetourPrevue; // Date limite prévue pour rendre le livre.

    /** Date réelle du retour ; reste null tant que le livre n'est pas rendu. */
    private LocalDate dateRetourEffective; // Date du retour réel, nulle tant que le prêt est en cours.

    /** Indique si le livre a été rendu. */
    @Column(nullable = false) // Rend l'état de retour obligatoire en base.
    private Boolean rendu; // Indique si le livre a été rendu.

    /** Constructeur vide requis par JPA pour instancier l'entité. */
    public Emprunt() { // Permet à JPA de créer une instance lors de la lecture en base.
    }

    /** Retourne l'identifiant généré par la base. */
    public Long getId() { return id; } // Retourne l'identifiant de l'emprunt.
    /** Définit l'identifiant de l'emprunt. */
    public void setId(Long id) { this.id = id; } // Définit l'identifiant de l'emprunt.

    /** Retourne l'identifiant du livre emprunté. */
    public Long getLivreId() { return livreId; } // Retourne l'identifiant du livre emprunté.
    /** Définit l'identifiant du livre emprunté. */
    public void setLivreId(Long livreId) { this.livreId = livreId; } // Définit le livre lié à l'emprunt.

    /** Retourne le nom de l'emprunteur. */
    public String getEmprunteur() { return emprunteur; } // Retourne le nom de l'emprunteur.
    /** Définit le nom de l'emprunteur. */
    public void setEmprunteur(String emprunteur) { this.emprunteur = emprunteur; } // Définit le nom de l'emprunteur.

    /** Retourne l'email conservé pour le traitement interne de l'emprunt. */
    public String getEmailEmprunteur() { return emailEmprunteur; } // Retourne l'email interne.
    /** Définit l'email de l'emprunteur. */
    public void setEmailEmprunteur(String emailEmprunteur) { this.emailEmprunteur = emailEmprunteur; } // Définit l'email de l'emprunteur.

    /** Retourne la date de début de l'emprunt. */
    public LocalDate getDateEmprunt() { return dateEmprunt; } // Retourne la date de début.
    /** Définit la date de début de l'emprunt. */
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; } // Définit la date de début.

    /** Retourne la date de retour prévue. */
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; } // Retourne la date d'échéance.
    /** Définit la date de retour prévue. */
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; } // Définit l'échéance prévue.

    /** Retourne la date de retour effective, ou null si le livre est encore emprunté. */
    public LocalDate getDateRetourEffective() { return dateRetourEffective; } // Retourne la date réelle du retour.
    /** Définit la date effective à laquelle le livre a été rendu. */
    public void setDateRetourEffective(LocalDate dateRetourEffective) { this.dateRetourEffective = dateRetourEffective; } // Définit la date du retour.

    /** Indique si le livre est rendu. */
    public Boolean getRendu() { return rendu; } // Retourne l'état de retour.
    /** Définit l'état de retour du livre. */
    public void setRendu(Boolean rendu) { this.rendu = rendu; } // Définit l'état de retour.
}