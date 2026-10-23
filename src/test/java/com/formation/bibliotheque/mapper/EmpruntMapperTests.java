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
    @Test // Marque cette méthode comme un test unitaire JUnit.
    void mapsRequestWithoutServiceManagedFields() {
        EmpruntRequest request = new EmpruntRequest(); // Prépare une demande comme celle reçue par l'API.
        request.setLivreId(12L); // Définit le livre demandé.
        request.setEmprunteur("Marie Dupont"); // Définit le nom du lecteur.
        request.setEmailEmprunteur("marie@example.com"); // Définit son email.

        Emprunt emprunt = EmpruntMapper.versEntite(request); // Convertit la demande en entité.

        assertThat(emprunt.getId()).isNull(); // Vérifie que l'identifiant sera généré par la base.
        assertThat(emprunt.getLivreId()).isEqualTo(12L); // Vérifie la copie du livre.
        assertThat(emprunt.getEmprunteur()).isEqualTo("Marie Dupont"); // Vérifie la copie du nom.
        assertThat(emprunt.getEmailEmprunteur()).isEqualTo("marie@example.com"); // Vérifie la copie de l'email.
        assertThat(emprunt.getDateEmprunt()).isNull(); // Vérifie que le service définira la date de début.
        assertThat(emprunt.getDateRetourPrevue()).isNull(); // Vérifie que le service calculera l'échéance.
        assertThat(emprunt.getDateRetourEffective()).isNull(); // Vérifie qu'aucun retour n'est anticipé.
        assertThat(emprunt.getRendu()).isNull(); // Vérifie que le service définit l'état initial.
    }

    /** Seul un emprunt non rendu dont la date limite est passée est en retard. */
    @Test // Marque le calcul de retard comme test unitaire.
    void marksOnlyUnreturnedPastDueLoansAsLate() {
        Emprunt overdueEmprunt = new Emprunt(); // Crée un emprunt de test.
        overdueEmprunt.setRendu(false); // Le livre n'a pas été rendu.
        overdueEmprunt.setDateRetourPrevue(LocalDate.now().minusDays(1)); // L'échéance est passée.

        EmpruntResponse overdueResponse = EmpruntMapper.versResponse(overdueEmprunt); // Convertit et calcule le retard.

        assertThat(overdueResponse.getEnRetard()).isTrue(); // Vérifie que l'emprunt est en retard.

        overdueEmprunt.setRendu(true); // Simule le retour du livre.
        assertThat(EmpruntMapper.versResponse(overdueEmprunt).getEnRetard()).isFalse(); // Un livre rendu n'est pas en retard.

        overdueEmprunt.setRendu(false); // Simule à nouveau un emprunt en cours.
        overdueEmprunt.setDateRetourPrevue(LocalDate.now()); // Place l'échéance à aujourd'hui.
        assertThat(EmpruntMapper.versResponse(overdueEmprunt).getEnRetard()).isFalse(); // Une échéance aujourd'hui n'est pas encore dépassée.
    }
}
