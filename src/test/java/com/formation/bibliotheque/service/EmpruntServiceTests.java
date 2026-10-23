package com.formation.bibliotheque.service;

import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.repository.EmpruntRepository;
import com.formation.bibliotheque.repository.LivreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpruntServiceTests {

    @Mock
    private EmpruntRepository empruntRepository;

    @Mock
    private LivreRepository livreRepository;

    @InjectMocks
    private EmpruntService empruntService;

    @Test
    void createsLoanAndDecrementsBookStock() {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(99L);
        emprunt.setLivreId(3L);

        Livre livre = new Livre();
        livre.setId(3L);
        livre.setStock(4);
        when(livreRepository.findById(3L)).thenReturn(Optional.of(livre));
        when(empruntRepository.save(emprunt)).thenReturn(emprunt);

        Emprunt resultat = empruntService.creer(emprunt);

        assertThat(resultat.getId()).isNull();
        assertThat(resultat.getDateEmprunt()).isEqualTo(LocalDate.now());
        assertThat(resultat.getDateRetourPrevue()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(resultat.getDateRetourEffective()).isNull();
        assertThat(resultat.getRendu()).isFalse();
        assertThat(livre.getStock()).isEqualTo(3);
        verify(livreRepository).save(livre);
        verify(empruntRepository).save(emprunt);
    }

    @Test
    void returningLoanSetsReturnDateAndRestoresStockWhenBookExists() {
        Emprunt emprunt = new Emprunt();
        emprunt.setLivreId(3L);

        Livre livre = new Livre();
        livre.setId(3L);
        livre.setStock(2);
        when(livreRepository.findById(3L)).thenReturn(Optional.of(livre));
        when(empruntRepository.save(emprunt)).thenReturn(emprunt);

        empruntService.retourner(emprunt);

        assertThat(emprunt.getRendu()).isTrue();
        assertThat(emprunt.getDateRetourEffective()).isEqualTo(LocalDate.now());
        assertThat(livre.getStock()).isEqualTo(3);
        verify(livreRepository).save(livre);
        verify(empruntRepository).save(emprunt);
    }

    @Test
    void returningLoanStillSavesWhenBookNoLongerExists() {
        Emprunt emprunt = new Emprunt();
        emprunt.setLivreId(404L);
        when(livreRepository.findById(404L)).thenReturn(Optional.empty());
        when(empruntRepository.save(emprunt)).thenReturn(emprunt);

        empruntService.retourner(emprunt);

        assertThat(emprunt.getRendu()).isTrue();
        verify(empruntRepository).save(emprunt);
    }
}
