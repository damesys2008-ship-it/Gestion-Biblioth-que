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
    @Test
    void createsLoanWithServiceManagedDatesAndStatus() {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(99L);
        emprunt.setLivreId(3L);

        when(empruntRepository.save(emprunt)).thenReturn(emprunt);

        Emprunt resultat = empruntService.creer(emprunt);

        assertThat(resultat.getId()).isNull();
        assertThat(resultat.getDateEmprunt()).isEqualTo(LocalDate.now());
        assertThat(resultat.getDateRetourPrevue()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(resultat.getDateRetourEffective()).isNull();
        assertThat(resultat.getRendu()).isFalse();
        verify(empruntRepository).save(emprunt);
    }

    /** Le retour marque l'emprunt rendu et renseigne la date effective. */
    @Test
    void returningLoanSetsReturnDateAndStatus() {
        Emprunt emprunt = new Emprunt();
        emprunt.setLivreId(3L);

        when(empruntRepository.save(emprunt)).thenReturn(emprunt);

        empruntService.retourner(emprunt);

        assertThat(emprunt.getRendu()).isTrue();
        assertThat(emprunt.getDateRetourEffective()).isEqualTo(LocalDate.now());
        verify(empruntRepository).save(emprunt);
    }
}
