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
@Entity
@Table(name = "emprunts")
public class Emprunt {

    /** Identifiant attribué automatiquement par la base de données. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant du livre concerné par l'emprunt. */
    @Column(nullable = false)
    private Long livreId;

    /** Nom de la personne qui emprunte le livre. */
    @Column(nullable = false)
    private String emprunteur;

    /** Email conservé dans l'entité mais non exposé dans la réponse publique. */
    @Column(nullable = false)
    private String emailEmprunteur;

    /** Date à laquelle l'emprunt a commencé. */
    @Column(nullable = false)
    private LocalDate dateEmprunt;

    /** Date limite prévue pour le retour du livre. */
    @Column(nullable = false)
    private LocalDate dateRetourPrevue;

    /** Date réelle du retour ; reste null tant que le livre n'est pas rendu. */
    private LocalDate dateRetourEffective;

    /** Indique si le livre a été rendu. */
    @Column(nullable = false)
    private Boolean rendu;

    /** Constructeur vide requis par JPA pour instancier l'entité. */
    public Emprunt() {
    }

    /** Retourne l'identifiant généré par la base. */
    public Long getId() { return id; }
    /** Définit l'identifiant de l'emprunt. */
    public void setId(Long id) { this.id = id; }

    /** Retourne l'identifiant du livre emprunté. */
    public Long getLivreId() { return livreId; }
    /** Définit l'identifiant du livre emprunté. */
    public void setLivreId(Long livreId) { this.livreId = livreId; }

    /** Retourne le nom de l'emprunteur. */
    public String getEmprunteur() { return emprunteur; }
    /** Définit le nom de l'emprunteur. */
    public void setEmprunteur(String emprunteur) { this.emprunteur = emprunteur; }

    /** Retourne l'email conservé pour le traitement interne de l'emprunt. */
    public String getEmailEmprunteur() { return emailEmprunteur; }
    /** Définit l'email de l'emprunteur. */
    public void setEmailEmprunteur(String emailEmprunteur) { this.emailEmprunteur = emailEmprunteur; }

    /** Retourne la date de début de l'emprunt. */
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    /** Définit la date de début de l'emprunt. */
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }

    /** Retourne la date de retour prévue. */
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    /** Définit la date de retour prévue. */
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }

    /** Retourne la date de retour effective, ou null si le livre est encore emprunté. */
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    /** Définit la date effective à laquelle le livre a été rendu. */
    public void setDateRetourEffective(LocalDate dateRetourEffective) { this.dateRetourEffective = dateRetourEffective; }

    /** Indique si le livre est rendu. */
    public Boolean getRendu() { return rendu; }
    /** Définit l'état de retour du livre. */
    public void setRendu(Boolean rendu) { this.rendu = rendu; }
}