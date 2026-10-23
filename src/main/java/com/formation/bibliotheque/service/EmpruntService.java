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
    public static final int DUREE_MAX_JOURS = 14;

    private final EmpruntRepository empruntRepository;

    public EmpruntService(EmpruntRepository empruntRepository) {
        this.empruntRepository = empruntRepository;
    }

    /** Renvoie les emprunts de la page demandée. */
    @Transactional(readOnly = true)
    public Page<Emprunt> recupererTous(Pageable pageable) {
        return empruntRepository.findAll(pageable);
    }

    /** Renvoie l'emprunt correspondant à l'identifiant, s'il existe. */
    @Transactional(readOnly = true)
    public Optional<Emprunt> recupererParId(Long id) {
        return empruntRepository.findById(id);
    }

    /**
     * Initialise les dates et l'état (RM5), puis sauvegarde l'emprunt.
     */
    public Emprunt creer(Emprunt emprunt) {
        LocalDate aujourdHui = LocalDate.now();
        emprunt.setId(null);
        emprunt.setDateEmprunt(aujourdHui);
        emprunt.setDateRetourPrevue(aujourdHui.plusDays(DUREE_MAX_JOURS));
        emprunt.setDateRetourEffective(null);
        emprunt.setRendu(false);

        return empruntRepository.save(emprunt);
    }

    /**
     * Enregistre le retour de l'emprunt (RM6).
     */
    public Emprunt retourner(Emprunt emprunt) {
        emprunt.setRendu(true);
        emprunt.setDateRetourEffective(LocalDate.now());

        return empruntRepository.save(emprunt);
    }

    /** Renvoie les emprunts non rendus. */
    @Transactional(readOnly = true)
    public Page<Emprunt> recupererEnCours(Pageable pageable) {
        return empruntRepository.findByRenduFalse(pageable);
    }

    /** Renvoie les emprunts non rendus dont la date prévue est dépassée (RM7). */
    @Transactional(readOnly = true)
    public Page<Emprunt> recupererEnRetard(Pageable pageable) {
        return empruntRepository.findByRenduFalseAndDateRetourPrevueBefore(LocalDate.now(), pageable);
    }
}
