package com.formation.bibliotheque.mapper;

import com.formation.bibliotheque.dto.EmpruntRequest;
import com.formation.bibliotheque.dto.EmpruntResponse;
import com.formation.bibliotheque.model.Emprunt;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Vérifie les conversions entre DTOs et entités, y compris le calcul du retard. */
class EmpruntMapperTests {

    /** Les dates et l'état de retour doivent rester à null dans le mapper de requête. */
    @Test
    void mapsRequestWithoutServiceManagedFields() {
        EmpruntRequest request = new EmpruntRequest();
        request.setLivreId(12L);
        request.setEmprunteur("Marie Dupont");
        request.setEmailEmprunteur("marie@example.com");

        Emprunt emprunt = EmpruntMapper.versEntite(request);

        assertThat(emprunt.getId()).isNull();
        assertThat(emprunt.getLivreId()).isEqualTo(12L);
        assertThat(emprunt.getEmprunteur()).isEqualTo("Marie Dupont");
        assertThat(emprunt.getEmailEmprunteur()).isEqualTo("marie@example.com");
        assertThat(emprunt.getDateEmprunt()).isNull();
        assertThat(emprunt.getDateRetourPrevue()).isNull();
        assertThat(emprunt.getDateRetourEffective()).isNull();
        assertThat(emprunt.getRendu()).isNull();
    }

    /** Seul un emprunt non rendu dont la date limite est passée est en retard. */
    @Test
    void marksOnlyUnreturnedPastDueLoansAsLate() {
        Emprunt overdueEmprunt = new Emprunt();
        overdueEmprunt.setRendu(false);
        overdueEmprunt.setDateRetourPrevue(LocalDate.now().minusDays(1));

        EmpruntResponse overdueResponse = EmpruntMapper.versResponse(overdueEmprunt);

        assertThat(overdueResponse.getEnRetard()).isTrue();

        overdueEmprunt.setRendu(true);
        assertThat(EmpruntMapper.versResponse(overdueEmprunt).getEnRetard()).isFalse();

        overdueEmprunt.setRendu(false);
        overdueEmprunt.setDateRetourPrevue(LocalDate.now());
        assertThat(EmpruntMapper.versResponse(overdueEmprunt).getEnRetard()).isFalse();
    }
}
