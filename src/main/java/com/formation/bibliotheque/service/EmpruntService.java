package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.repository.EmpruntRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Applique les règles métier liées à la création et au retour des emprunts.
 * Cette couche ne dépend pas de HTTP : elle travaille avec les repositories et entités.
 */
@Service
@Transactional
public class EmpruntService {

    /** Durée maximale d'un emprunt, en jours (RM5). */
    public static final int DUREE_MAX_JOURS = 14; // Nombre de jours accordés avant la date prévue de retour.

    /** Repository responsable de la persistance des emprunts. */
    private final EmpruntRepository empruntRepository;

    /** Injecte le repository utilisé par les opérations métier. */
    public EmpruntService(EmpruntRepository empruntRepository) {
        this.empruntRepository = empruntRepository; // Conserve le repository injecté par Spring.
    }

    /** Renvoie les emprunts de la page demandée. */
    @Transactional(readOnly = true)
    public Page<Emprunt> recupererTous(Pageable pageable) {
        return empruntRepository.findAll(pageable); // Délègue la recherche paginée au repository.
    }

    /** Renvoie l'emprunt correspondant à l'identifiant, s'il existe. */
    @Transactional(readOnly = true)
    public Optional<Emprunt> recupererParId(Long id) {
        return empruntRepository.findById(id); // Renvoie le résultat facultatif de la recherche.
    }

    /**
     * Initialise les dates et l'état (RM5), puis sauvegarde l'emprunt.
     */
    public Emprunt creer(Emprunt emprunt) {
        LocalDate aujourdHui = LocalDate.now(); // Utilise la date du jour comme date de départ.
        emprunt.setId(null); // Empêche le client d'imposer un identifiant existant.
        emprunt.setDateEmprunt(aujourdHui); // Initialise la date de début.
        emprunt.setDateRetourPrevue(aujourdHui.plusDays(DUREE_MAX_JOURS)); // Calcule l'échéance autorisée.
        emprunt.setDateRetourEffective(null); // Aucun retour n'a encore été enregistré.
        emprunt.setRendu(false); // Un nouvel emprunt est forcément en cours.

        return empruntRepository.save(emprunt); // Persiste l'emprunt initialisé.
    }

    /**
     * Enregistre le retour de l'emprunt (RM6).
     */
    public Emprunt retourner(Emprunt emprunt) {
        emprunt.setRendu(true); // Marque le livre comme rendu.
        emprunt.setDateRetourEffective(LocalDate.now()); // Enregistre la date réelle du retour.

        return empruntRepository.save(emprunt); // Persiste le nouvel état de l'emprunt.
    }

    /** Renvoie les emprunts non rendus. */
    @Transactional(readOnly = true)
    public Page<Emprunt> recupererEnCours(Pageable pageable) {
        return empruntRepository.findByRenduFalse(pageable); // Recherche uniquement les prêts non rendus.
    }

    /** Renvoie les emprunts non rendus dont la date prévue est dépassée (RM7). */
    @Transactional(readOnly = true)
    public Page<Emprunt> recupererEnRetard(Pageable pageable) {
        return empruntRepository.findByRenduFalseAndDateRetourPrevueBefore(LocalDate.now(), pageable); // Filtre les prêts échus et non rendus.
    }
}
