package com.formation.bibliotheque.repository;

import com.formation.bibliotheque.model.Emprunt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Accès aux emprunts enregistrés en base de données.
 * Spring Data JPA fournit automatiquement les opérations CRUD héritées
 * et crée les requêtes correspondant aux noms des méthodes ci-dessous.
 */
@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {

    /**
     * Recherche les emprunts dont le livre n'a pas encore été rendu.
     *
     * @param pageable paramètres de pagination et de tri
     * @return une page d'emprunts non rendus
     */
    Page<Emprunt> findByRenduFalse(Pageable pageable);

    /**
     * Recherche les emprunts non rendus dont la date de retour prévue
     * est strictement antérieure à la date fournie.
     *
     * @param date date limite de comparaison
     * @param pageable paramètres de pagination et de tri
     * @return une page d'emprunts en retard à cette date
     */
    Page<Emprunt> findByRenduFalseAndDateRetourPrevueBefore(LocalDate date, Pageable pageable);
}
