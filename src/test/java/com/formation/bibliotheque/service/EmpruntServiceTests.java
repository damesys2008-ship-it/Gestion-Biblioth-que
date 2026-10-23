package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.repository.EmpruntRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Vérifie l'application par le service des règles métier de création et de retour. */
@ExtendWith(MockitoExtension.class)
class EmpruntServiceTests {

    /** Remplace le repository réel pour vérifier les appels de persistance. */
    @Mock
    private EmpruntRepository empruntRepository;

    /** Injecte le faux repository dans le service testé. */
    @InjectMocks
    private EmpruntService empruntService;

    /** Le service initialise les dates et l'état avant de sauvegarder un emprunt. */
    @Test // Marque le test de création pour JUnit.
    void createsLoanWithServiceManagedDatesAndStatus() {
        Emprunt emprunt = new Emprunt(); // Prépare les données d'un nouvel emprunt.
        emprunt.setId(99L); // Vérifie que le service ignore un identifiant fourni par le client.
        emprunt.setLivreId(3L); // Associe l'emprunt au livre de test.

        when(empruntRepository.save(emprunt)).thenReturn(emprunt); // Configure le faux repository pour renvoyer l'entité.

        Emprunt resultat = empruntService.creer(emprunt); // Exécute la création métier.

        assertThat(resultat.getId()).isNull(); // Vérifie que l'identifiant client a été effacé.
        assertThat(resultat.getDateEmprunt()).isEqualTo(LocalDate.now()); // Vérifie la date de départ.
        assertThat(resultat.getDateRetourPrevue()).isEqualTo(LocalDate.now().plusDays(14)); // Vérifie la durée maximale.
        assertThat(resultat.getDateRetourEffective()).isNull(); // Vérifie que le retour n'est pas encore renseigné.
        assertThat(resultat.getRendu()).isFalse(); // Vérifie l'état initial non rendu.
        verify(empruntRepository).save(emprunt); // Vérifie que l'emprunt a été sauvegardé.
    }

    /** Le retour marque l'emprunt rendu et renseigne la date effective. */
    @Test // Marque le test de retour pour JUnit.
    void returningLoanSetsReturnDateAndStatus() {
        Emprunt emprunt = new Emprunt(); // Prépare l'emprunt à retourner.
        emprunt.setLivreId(3L); // Associe l'emprunt au livre de test.

        when(empruntRepository.save(emprunt)).thenReturn(emprunt); // Configure le faux repository.

        empruntService.retourner(emprunt); // Exécute l'enregistrement du retour.

        assertThat(emprunt.getRendu()).isTrue(); // Vérifie que le livre est indiqué comme rendu.
        assertThat(emprunt.getDateRetourEffective()).isEqualTo(LocalDate.now()); // Vérifie la date effective.
        verify(empruntRepository).save(emprunt); // Vérifie la sauvegarde de l'état modifié.
    }
}
