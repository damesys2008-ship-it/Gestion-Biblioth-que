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
@ExtendWith(MockitoExtension.class)
class EmpruntControllerTests {

    /** Faux service permettant de contrôler les résultats vus par le contrôleur. */
    @Mock
    private EmpruntService empruntService;

    /** Client MVC de test configuré pour résoudre les paramètres de pagination. */
    private MockMvc mockMvc;

    /** Prépare le contrôleur isolé et son résolveur de Pageable. */
    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new EmpruntController(empruntService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    /** Les listes répondent 200 et la lecture d'un identifiant inconnu répond 404. */
    @Test
    void listsLoansAndReturnsNotFoundForUnknownId() throws Exception {
        when(empruntService.recupererTous(any())).thenReturn(new PageImpl<>(List.of()));
        when(empruntService.recupererEnCours(any())).thenReturn(new PageImpl<>(List.of()));
        when(empruntService.recupererEnRetard(any())).thenReturn(new PageImpl<>(List.of()));
        when(empruntService.recupererParId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/emprunts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        mockMvc.perform(get("/api/v1/emprunts/99"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/emprunts/en-cours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        mockMvc.perform(get("/api/v1/emprunts/en-retard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    /** Une requête valide est créée ; les champs requis et le format email sont validés. */
    @Test
    void createsLoanAndValidatesTheRequest() throws Exception {
        when(empruntService.creer(any(Emprunt.class))).thenAnswer(invocation -> {
            Emprunt emprunt = invocation.getArgument(0);
            emprunt.setId(4L);
            emprunt.setDateEmprunt(LocalDate.now());
            emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14));
            emprunt.setRendu(false);
            return emprunt;
        });

        mockMvc.perform(post("/api/v1/emprunts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livreId": 2,
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "alice.durand@email.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.rendu").value(false))
                .andExpect(jsonPath("$.enRetard").value(false))
                .andExpect(jsonPath("$.emailEmprunteur").doesNotExist());

        mockMvc.perform(post("/api/v1/emprunts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livreId": 2,
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "invalid-email"
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/emprunts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emprunteur": "Alice Durand",
                                  "emailEmprunteur": "alice.durand@email.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /** Un retour réussi ne peut pas être répété et un identifiant inconnu répond 404. */
    @Test
    void returnsLoanOnlyOnceAndReportsUnknownLoan() throws Exception {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(2L);
        emprunt.setLivreId(3L);
        emprunt.setEmprunteur("Jean Martin");
        emprunt.setDateRetourPrevue(LocalDate.now().minusDays(1));
        emprunt.setRendu(false);
        when(empruntService.recupererParId(2L)).thenReturn(Optional.of(emprunt));
        when(empruntService.retourner(emprunt)).thenAnswer(invocation -> {
            emprunt.setRendu(true);
            emprunt.setDateRetourEffective(LocalDate.now());
            return emprunt;
        });
        when(empruntService.recupererParId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/v1/emprunts/2/retour"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rendu").value(true));

        mockMvc.perform(patch("/api/v1/emprunts/2/retour"))
                .andExpect(status().isConflict());

        mockMvc.perform(patch("/api/v1/emprunts/99/retour"))
                .andExpect(status().isNotFound());
    }
}
