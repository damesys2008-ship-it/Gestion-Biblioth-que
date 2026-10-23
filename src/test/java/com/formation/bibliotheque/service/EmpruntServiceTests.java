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

@ExtendWith(MockitoExtension.class)
class EmpruntServiceTests {

    @Mock
    private EmpruntRepository empruntRepository;

    @InjectMocks
    private EmpruntService empruntService;

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
