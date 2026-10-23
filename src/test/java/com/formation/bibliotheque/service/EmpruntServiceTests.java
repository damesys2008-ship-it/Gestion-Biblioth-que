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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Vérifie l'application par le service des règles métier de création et de retour. */
@ExtendWith(MockitoExtension.class)
class EmpruntServiceTests {

    /** Remplace le repository réel pour vérifier les appels de persistance. */
    @Mock
    private EmpruntRepository empruntRepository;

    /** Remplace le repository des livres pour simuler leur présence et leur stock. */
    @Mock
    private LivreRepository livreRepository;

    /** Injecte le faux repository dans le service testé. */
    @InjectMocks
    private EmpruntService empruntService;

    /** Le service initialise les dates et l'état avant de sauvegarder un emprunt. */
    @Test // Marque le test de création pour JUnit.
    void createsLoanWithServiceManagedDatesAndStatus() {
        Emprunt emprunt = new Emprunt(); // Prépare les données d'un nouvel emprunt.
        emprunt.setId(99L); // Vérifie que le service ignore un identifiant fourni par le client.
        emprunt.setLivreId(3L); // Associe l'emprunt au livre de test.
        Livre livre = new Livre(); // Prépare le livre dont le stock sera réservé.
        livre.setId(3L); // Définit l'identifiant du livre.
        livre.setStock(2); // Rend deux exemplaires disponibles avant l'emprunt.

        when(livreRepository.findById(3L)).thenReturn(Optional.of(livre)); // Simule la présence du livre.
        when(livreRepository.save(livre)).thenReturn(livre); // Simule la sauvegarde du stock modifié.
        when(empruntRepository.save(emprunt)).thenReturn(emprunt); // Configure le faux repository pour renvoyer l'entité.

        Emprunt resultat = empruntService.creer(emprunt); // Exécute la création métier.

        assertThat(resultat.getId()).isNull(); // Vérifie que l'identifiant client a été effacé.
        assertThat(resultat.getDateEmprunt()).isEqualTo(LocalDate.now()); // Vérifie la date de départ.
        assertThat(resultat.getDateRetourPrevue()).isEqualTo(LocalDate.now().plusDays(14)); // Vérifie la durée maximale.
        assertThat(resultat.getDateRetourEffective()).isNull(); // Vérifie que le retour n'est pas encore renseigné.
        assertThat(resultat.getRendu()).isFalse(); // Vérifie l'état initial non rendu.
        assertThat(livre.getStock()).isEqualTo(1); // Vérifie qu'un exemplaire a été réservé.
        verify(livreRepository).save(livre); // Vérifie que le nouveau stock a été sauvegardé.
        verify(empruntRepository).save(emprunt); // Vérifie que l'emprunt a été sauvegardé.
    }

    /** La création échoue si le livre demandé n'existe pas. */
    @Test
    void refusesLoanForUnknownBook() {
        Emprunt emprunt = new Emprunt(); // Prépare une demande d'emprunt.
        emprunt.setLivreId(99L); // Référence un livre absent du catalogue.
        when(livreRepository.findById(99L)).thenReturn(Optional.empty()); // Simule le livre absent.

        assertThatThrownBy(() -> empruntService.creer(emprunt)) // Tente de créer l'emprunt.
                .isInstanceOf(EmpruntService.LivreIntrouvableException.class); // Vérifie que l'absence est signalée.
    }

    /** La création échoue si le livre existe mais que son stock est épuisé. */
    @Test
    void refusesLoanWhenBookHasNoStock() {
        Emprunt emprunt = new Emprunt(); // Prépare une demande d'emprunt.
        emprunt.setLivreId(8L); // Référence le livre sans exemplaire disponible.
        Livre livre = new Livre(); // Prépare le livre indisponible.
        livre.setId(8L); // Définit son identifiant.
        livre.setStock(0); // Indique que le stock est épuisé.
        when(livreRepository.findById(8L)).thenReturn(Optional.of(livre)); // Simule la présence du livre.

        assertThatThrownBy(() -> empruntService.creer(emprunt)) // Tente de créer l'emprunt.
                .isInstanceOf(EmpruntService.StockIndisponibleException.class); // Vérifie que l'indisponibilité est signalée.
    }

    /** Le retour marque l'emprunt rendu et renseigne la date effective. */
    @Test // Marque le test de retour pour JUnit.
    void returningLoanSetsReturnDateAndStatus() {
        Emprunt emprunt = new Emprunt(); // Prépare l'emprunt à retourner.
        emprunt.setLivreId(3L); // Associe l'emprunt au livre de test.
        Livre livre = new Livre(); // Prépare le livre à réintégrer dans le stock.
        livre.setId(3L); // Définit l'identifiant du livre.
        livre.setStock(1); // Indique un exemplaire avant le retour.

        when(livreRepository.findById(3L)).thenReturn(Optional.of(livre)); // Simule la présence du livre.
        when(livreRepository.save(livre)).thenReturn(livre); // Simule la sauvegarde du stock restauré.
        when(empruntRepository.save(emprunt)).thenReturn(emprunt); // Configure le faux repository.

        empruntService.retourner(emprunt); // Exécute l'enregistrement du retour.

        assertThat(emprunt.getRendu()).isTrue(); // Vérifie que le livre est indiqué comme rendu.
        assertThat(emprunt.getDateRetourEffective()).isEqualTo(LocalDate.now()); // Vérifie la date effective.
        assertThat(livre.getStock()).isEqualTo(2); // Vérifie que l'exemplaire rendu réintègre le stock.
        verify(livreRepository).save(livre); // Vérifie que le stock restauré est sauvegardé.
        verify(empruntRepository).save(emprunt); // Vérifie la sauvegarde de l'état modifié.
    }
}
