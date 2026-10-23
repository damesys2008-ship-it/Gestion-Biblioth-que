package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.repository.EmpruntRepository;
import com.formation.bibliotheque.repository.LivreRepository;
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
    private final LivreRepository livreRepository;

    public EmpruntService(EmpruntRepository empruntRepository, LivreRepository livreRepository) {
        this.empruntRepository = empruntRepository;
        this.livreRepository = livreRepository;
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

    /** Vérifie qu'un livre existe avec cet identifiant. */
    @Transactional(readOnly = true)
    public boolean livreExiste(Long livreId) {
        return livreRepository.existsById(livreId);
    }

    /** Vérifie qu'un livre existe et possède un stock strictement positif (RM4). */
    @Transactional(readOnly = true)
    public boolean livreDisponible(Long livreId) {
        return livreRepository.findById(livreId)
                .map(livre -> livre.getStock() != null && livre.getStock() > 0)
                .orElse(false);
    }

    /**
     * Initialise les dates et l'état (RM5), décrémente le stock disponible,
     * puis sauvegarde le livre et l'emprunt dans la même transaction.
     */
    public Emprunt creer(Emprunt emprunt) {
        LocalDate aujourdHui = LocalDate.now();
        emprunt.setId(null);
        emprunt.setDateEmprunt(aujourdHui);
        emprunt.setDateRetourPrevue(aujourdHui.plusDays(DUREE_MAX_JOURS));
        emprunt.setDateRetourEffective(null);
        emprunt.setRendu(false);

        Optional<Livre> livreOptionnel = livreRepository.findById(emprunt.getLivreId());
        if (livreOptionnel.isPresent()) {
            Livre livre = livreOptionnel.get();
            livre.setStock(livre.getStock() - 1);
            livreRepository.save(livre);
        }

        return empruntRepository.save(emprunt);
    }

    /**
     * Enregistre le retour (RM6) et réapprovisionne le livre s'il existe encore.
     */
    public Emprunt retourner(Emprunt emprunt) {
        emprunt.setRendu(true);
        emprunt.setDateRetourEffective(LocalDate.now());

        Optional<Livre> livreOptionnel = livreRepository.findById(emprunt.getLivreId());
        if (livreOptionnel.isPresent()) {
            Livre livre = livreOptionnel.get();
            livre.setStock(livre.getStock() + 1);
            livreRepository.save(livre);
        }

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
