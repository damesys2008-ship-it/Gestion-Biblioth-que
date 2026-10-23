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

    private EmpruntMapper() {
    }

    /**
     * Crée un emprunt à partir des seules données fournies par le client.
     * Les dates, l'identifiant et l'état de retour seront définis par le service.
     */
    public static Emprunt versEntite(EmpruntRequest request) {
        Emprunt emprunt = new Emprunt();
        emprunt.setLivreId(request.getLivreId());
        emprunt.setEmprunteur(request.getEmprunteur());
        emprunt.setEmailEmprunteur(request.getEmailEmprunteur());
        return emprunt;
    }

    /**
     * Prépare les informations publiques d'un emprunt pour une réponse API.
     * L'email n'est pas exposé et le retard est calculé à la date du jour.
     */
    public static EmpruntResponse versResponse(Emprunt emprunt) {
        boolean enRetard = Boolean.FALSE.equals(emprunt.getRendu())
                && emprunt.getDateRetourPrevue() != null
                && emprunt.getDateRetourPrevue().isBefore(LocalDate.now());

        return new EmpruntResponse(
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
        List<EmpruntResponse> responses = new ArrayList<>();
        for (Emprunt emprunt : emprunts) {
            responses.add(versResponse(emprunt));
        }
        return responses;
    }

    /**
     * Convertit le contenu d'une page en DTOs tout en conservant sa pagination.
     */
    public static PagedModel<EmpruntResponse> versPage(Page<Emprunt> page) {
        List<EmpruntResponse> contenu = versResponses(page.getContent());
        Page<EmpruntResponse> pageDto =
                new PageImpl<>(contenu, page.getPageable(), page.getTotalElements());
        return new PagedModel<>(pageDto);
    }
}
