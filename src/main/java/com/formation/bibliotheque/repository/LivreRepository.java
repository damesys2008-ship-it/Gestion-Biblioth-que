package com.formation.bibliotheque.repository;

import com.formation.bibliotheque.model.Livre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Accès aux livres enregistrés en base de données.
 * Spring Data JPA implémente les opérations CRUD et les recherches dérivées.
 */
@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {

    /** Recherche un livre à partir de son ISBN unique. */
    Optional<Livre> findByIsbn(String isbn);

    /** Vérifie si l'auteur possède au moins un livre. */
    boolean existsByAuteurId(Long auteurId);

    /** Recherche les livres d'un auteur dans une page triable. */
    Page<Livre> findByAuteurId(Long auteurId, Pageable pageable);

    /** Recherche les titres contenant le texte fourni sans tenir compte de la casse. */
    Page<Livre> findByTitreContainingIgnoreCase(String titre, Pageable pageable);

    /** Recherche les livres dont le stock est strictement supérieur à la valeur fournie. */
    Page<Livre> findByStockGreaterThan(Integer stock, Pageable pageable);
}
