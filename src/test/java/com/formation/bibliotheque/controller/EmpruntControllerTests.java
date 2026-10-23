package com.formation.bibliotheque.controller;

import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.service.EmpruntService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/** Vérifie les réponses HTTP des endpoints d'emprunt sans démarrer tout Spring Boot. */
@ExtendWith(MockitoExtension.class) // Active l'initialisation des mocks Mockito pour JUnit.
class EmpruntControllerTests {

    /** Faux service permettant de contrôler les résultats vus par le contrôleur. */
    @Mock // Remplace le service réel pendant les tests du contrôleur.
    private EmpruntService empruntService;

    /** Client MVC de test configuré pour résoudre les paramètres de pagination. */
    private MockMvc mockMvc; // Simule des requêtes HTTP sans lancer de serveur.

    /** Prépare le contrôleur isolé et son résolveur de Pageable. */
    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new EmpruntController(empruntService)) // Construit le contrôleur avec son faux service.
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()) // Active la résolution des paramètres de page.
                .build(); // Finalise le client MVC de test.
    }

    /** Les listes répondent 200 et la lecture d'un identifiant inconnu répond 404. */
    @Test // Vérifie les routes de liste et la gestion d'un identifiant inconnu.
    void listsLoansAndReturnsNotFoundForUnknownId() throws Exception {
        when(empruntService.recupererTous(any())).thenReturn(new PageImpl<>(List.of())); // Prépare une page vide pour la liste complète.
        when(empruntService.recupererEnCours(any())).thenReturn(new PageImpl<>(List.of())); // Prépare une page vide des prêts en cours.
        when(empruntService.recupererEnRetard(any())).thenReturn(new PageImpl<>(List.of())); // Prépare une page vide des prêts en retard.
        when(empruntService.recupererParId(99L)).thenReturn(Optional.empty()); // Simule l'absence de l'emprunt 99.

        mockMvc.perform(get("/api/v1/emprunts")) // Appelle la route de liste.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        mockMvc.perform(get("/api/v1/emprunts/99")) // Appelle la route d'un emprunt inexistant.
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/emprunts/en-cours")) // Appelle la route des prêts en cours.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        mockMvc.perform(get("/api/v1/emprunts/en-retard")) // Appelle la route des prêts en retard.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    /** Une requête valide est créée ; les champs requis et le format email sont validés. */
    @Test // Vérifie la création valide et les erreurs de validation du DTO.
    void createsLoanAndValidatesTheRequest() throws Exception {
        when(empruntService.creer(any(Emprunt.class))).thenAnswer(invocation -> { // Simule la création et l'initialisation en base.
            Emprunt emprunt = invocation.getArgument(0); // Récupère l'entité fournie au service.
            emprunt.setId(4L); // Simule l'identifiant généré.
            emprunt.setDateEmprunt(LocalDate.now()); // Simule la date de début.
            emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14)); // Simule l'échéance.
            emprunt.setRendu(false); // Simule l'état initial du livre.
            return emprunt; // Renvoie l'entité créée au contrôleur.
        });

        mockMvc.perform(post("/api/v1/emprunts") // Envoie une demande JSON valide.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livreId": 2,
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "alice.durand@email.com"
                                }
                                """))
                .andExpect(status().isCreated()) // Vérifie le code HTTP de création.
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.rendu").value(false))
                .andExpect(jsonPath("$.enRetard").value(false))
                .andExpect(jsonPath("$.emailEmprunteur").doesNotExist());

        mockMvc.perform(post("/api/v1/emprunts") // Envoie une adresse email invalide.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livreId": 2,
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "invalid-email"
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/emprunts") // Envoie une demande sans identifiant de livre.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "alice.durand@email.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /** Les erreurs métier de création deviennent les codes HTTP prévus par l'API. */
    @Test
    void mapsUnknownAndUnavailableBooksToClientErrors() throws Exception {
        when(empruntService.creer(any(Emprunt.class)))
                .thenThrow(new EmpruntService.LivreIntrouvableException(99L))
                .thenThrow(new EmpruntService.StockIndisponibleException(8L));

        mockMvc.perform(post("/api/v1/emprunts") // Demande un emprunt d'un livre absent.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livreId": 99,
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "alice.durand@email.com"
                                }
                                """))
                .andExpect(status().isBadRequest()); // L'identifiant de livre absent est une requête invalide.

        mockMvc.perform(post("/api/v1/emprunts") // Demande un livre dont le stock est épuisé.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livreId": 8,
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "alice.durand@email.com"
                                }
                                """))
                .andExpect(status().isConflict()); // L'absence de stock est un conflit métier.
    }

    /** Un retour réussi ne peut pas être répété et un identifiant inconnu répond 404. */
    @Test // Vérifie le premier retour, le conflit au second et le 404 si l'emprunt manque.
    void returnsLoanOnlyOnceAndReportsUnknownLoan() throws Exception {
        Emprunt emprunt = new Emprunt(); // Prépare un emprunt encore en cours.
        emprunt.setId(2L); // Définit son identifiant.
        emprunt.setLivreId(3L); // Définit le livre associé.
        emprunt.setEmprunteur("Jean Martin"); // Définit le lecteur.
        emprunt.setDateRetourPrevue(LocalDate.now().minusDays(1)); // Place la date prévue dans le passé.
        emprunt.setRendu(false); // Indique que le retour n'a pas encore eu lieu.
        when(empruntService.recupererParId(2L)).thenReturn(Optional.of(emprunt)); // Rend cet emprunt disponible au contrôleur.
        when(empruntService.retourner(emprunt)).thenAnswer(invocation -> { // Simule l'enregistrement du retour.
            emprunt.setRendu(true); // Modifie l'état du livre.
            emprunt.setDateRetourEffective(LocalDate.now()); // Enregistre la date du retour.
            return emprunt; // Renvoie l'emprunt après mise à jour.
        });
        when(empruntService.recupererParId(99L)).thenReturn(Optional.empty()); // Simule un identifiant absent.

        mockMvc.perform(patch("/api/v1/emprunts/2/retour")) // Enregistre le premier retour.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rendu").value(true));

        mockMvc.perform(patch("/api/v1/emprunts/2/retour")) // Tente de retourner une seconde fois.
                .andExpect(status().isConflict());

        mockMvc.perform(patch("/api/v1/emprunts/99/retour")) // Tente le retour d'un emprunt inexistant.
                .andExpect(status().isNotFound());
    }
}
