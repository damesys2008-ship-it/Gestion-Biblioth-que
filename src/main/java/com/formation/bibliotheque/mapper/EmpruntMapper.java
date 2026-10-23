package com.formation.bibliotheque.mapper;

import com.formation.bibliotheque.dto.EmpruntRequest;
import com.formation.bibliotheque.dto.EmpruntResponse;
import com.formation.bibliotheque.model.Emprunt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Convertit les emprunts entre les entités JPA et les DTOs de l'API.
 * Cette classe utilitaire ne nécessite pas d'être instanciée.
 */
public final class EmpruntMapper {

    /** Constructeur privé : les conversions sont toutes des méthodes statiques. */
    private EmpruntMapper() {
    }

    /**
     * Crée un emprunt à partir des seules données fournies par le client.
     * Les dates, l'identifiant et l'état de retour seront définis par le service.
     */
    public static Emprunt versEntite(EmpruntRequest request) {
        Emprunt emprunt = new Emprunt(); // Crée l'entité à partir des données de la requête.
        emprunt.setLivreId(request.getLivreId()); // Copie l'identifiant du livre demandé.
        emprunt.setEmprunteur(request.getEmprunteur()); // Copie le nom de l'emprunteur.
        emprunt.setEmailEmprunteur(request.getEmailEmprunteur()); // Conserve l'email pour la base.
        return emprunt; // Laisse le service initialiser les dates et l'état.
    }

    /**
     * Prépare les informations publiques d'un emprunt pour une réponse API.
     * L'email n'est pas exposé et le retard est calculé à la date du jour.
     */
    public static EmpruntResponse versResponse(Emprunt emprunt) {
        boolean enRetard = Boolean.FALSE.equals(emprunt.getRendu()) // Un emprunt rendu n'est jamais en retard.
                && emprunt.getDateRetourPrevue() != null
                && emprunt.getDateRetourPrevue().isBefore(LocalDate.now());

        return new EmpruntResponse( // Construit une réponse sans divulguer l'email.
                emprunt.getId(),
                emprunt.getLivreId(),
                emprunt.getEmprunteur(),
                emprunt.getDateEmprunt(),
                emprunt.getDateRetourPrevue(),
                emprunt.getDateRetourEffective(),
                emprunt.getRendu(),
                enRetard);
    }

    /**
     * Convertit chaque emprunt d'une liste en DTO de réponse.
     */
    public static List<EmpruntResponse> versResponses(List<Emprunt> emprunts) {
        List<EmpruntResponse> responses = new ArrayList<>(); // Prépare la liste de réponses à remplir.
        // Convertit les éléments un par un en réutilisant la conversion unitaire.
        for (Emprunt emprunt : emprunts) {
            responses.add(versResponse(emprunt)); // Convertit et ajoute l'emprunt courant.
        }
        return responses;
    }

    /**
     * Convertit le contenu d'une page en DTOs tout en conservant sa pagination.
     */
    public static PagedModel<EmpruntResponse> versPage(Page<Emprunt> page) {
        List<EmpruntResponse> contenu = versResponses(page.getContent()); // Convertit le contenu de la page.
        // Recrée la page avec les mêmes informations de pagination que la page source.
        Page<EmpruntResponse> pageDto = // Conserve les paramètres de page et le nombre total de résultats.
                new PageImpl<>(contenu, page.getPageable(), page.getTotalElements());
        return new PagedModel<>(pageDto); // Expose la page convertie au format Spring.
    }
}
