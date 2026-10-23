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
    public static final int DUREE_MAX_JOURS = 14; // Nombre de jours accordés avant la date prévue de retour.

    /** Repository responsable de la persistance des emprunts. */
    private final EmpruntRepository empruntRepository;

    /** Repository du catalogue, utilisé pour vérifier et ajuster le stock des livres. */
    private final LivreRepository livreRepository;

    /** Injecte le repository utilisé par les opérations métier. */
    public EmpruntService(EmpruntRepository empruntRepository, LivreRepository livreRepository) {
        this.empruntRepository = empruntRepository; // Conserve le repository injecté par Spring.
        this.livreRepository = livreRepository; // Conserve le repository des livres injecté par Spring.
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
        Livre livre = livreRepository.findById(emprunt.getLivreId())
                .orElseThrow(() -> new LivreIntrouvableException(emprunt.getLivreId())); // Vérifie que le livre existe.
        if (livre.getStock() == null || livre.getStock() <= 0) { // Empêche d'emprunter un livre indisponible.
            throw new StockIndisponibleException(emprunt.getLivreId()); // Signale l'absence d'exemplaire disponible.
        }

        LocalDate aujourdHui = LocalDate.now(); // Utilise la date du jour comme date de départ.
        emprunt.setId(null); // Empêche le client d'imposer un identifiant existant.
        emprunt.setDateEmprunt(aujourdHui); // Initialise la date de début.
        emprunt.setDateRetourPrevue(aujourdHui.plusDays(DUREE_MAX_JOURS)); // Calcule l'échéance autorisée.
        emprunt.setDateRetourEffective(null); // Aucun retour n'a encore été enregistré.
        emprunt.setRendu(false); // Un nouvel emprunt est forcément en cours.

        livre.setStock(livre.getStock() - 1); // Réserve un exemplaire pour ce nouvel emprunt.
        livreRepository.save(livre); // Persiste le stock diminué dans la même transaction.
        return empruntRepository.save(emprunt); // Persiste l'emprunt initialisé.
    }

    /**
     * Enregistre le retour de l'emprunt (RM6).
     */
    public Emprunt retourner(Emprunt emprunt) {
        Livre livre = livreRepository.findById(emprunt.getLivreId())
                .orElseThrow(() -> new LivreIntrouvableException(emprunt.getLivreId())); // Vérifie que le livre existe encore.
        emprunt.setRendu(true); // Marque le livre comme rendu.
        emprunt.setDateRetourEffective(LocalDate.now()); // Enregistre la date réelle du retour.

        livre.setStock(livre.getStock() + 1); // Rend un exemplaire disponible à nouveau.
        livreRepository.save(livre); // Persiste le stock augmenté dans la même transaction.
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

    /** Signale qu'une demande référence un livre absent du catalogue. */
    public static class LivreIntrouvableException extends RuntimeException {
        /** Construit une erreur qui indique l'identifiant de livre manquant. */
        public LivreIntrouvableException(Long livreId) {
            super("Le livre " + livreId + " est introuvable."); // Fournit un message utile au diagnostic.
        }
    }

    /** Signale qu'aucun exemplaire n'est disponible pour un nouvel emprunt. */
    public static class StockIndisponibleException extends RuntimeException {
        /** Construit une erreur qui indique le livre sans stock disponible. */
        public StockIndisponibleException(Long livreId) {
            super("Le livre " + livreId + " n'est pas disponible."); // Fournit un message utile au diagnostic.
        }
    }
}
