package com.formation.bibliotheque.controller;

import com.formation.bibliotheque.dto.EmpruntRequest;
import com.formation.bibliotheque.dto.EmpruntResponse;
import com.formation.bibliotheque.mapper.EmpruntMapper;
import com.formation.bibliotheque.model.Emprunt;
import com.formation.bibliotheque.service.EmpruntService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Expose les opérations HTTP de consultation, création et retour des emprunts.
 * Le contrôleur traduit les résultats du service en codes HTTP et DTOs.
 */
@RestController
@RequestMapping("/api/v1/emprunts")
@Tag(name = "Emprunts", description = "Emprunts et retours des livres")
public class EmpruntController {

    /** Service qui applique les règles métier des emprunts. */
    private final EmpruntService empruntService;

    /** Injecte le service utilisé par les endpoints HTTP. */
    public EmpruntController(EmpruntService empruntService) {
        this.empruntService = empruntService;
    }

    /** Endpoint 15 : renvoie une page d'emprunts, triée par date d'emprunt par défaut. */
    @Operation(summary = "Lister les emprunts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EmpruntResponse>> listerTous(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererTous(pageable);
        return ResponseEntity.ok(EmpruntMapper.versPage(page));
    }

    /** Endpoint 16 : renvoie un emprunt, ou 404 si son identifiant est inconnu. */
    @Operation(summary = "Obtenir un emprunt par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt trouvé"),
            @ApiResponse(responseCode = "404", description = "Emprunt inexistant")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmpruntResponse> obtenirParId(@PathVariable Long id) {
        Optional<Emprunt> resultat = empruntService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EmpruntMapper.versResponse(resultat.get()));
    }

    /** Endpoint 17 : valide la requête puis crée un emprunt. */
    @Operation(summary = "Créer un emprunt")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Emprunt créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<EmpruntResponse> creer(@Valid @RequestBody EmpruntRequest request) {
        Emprunt emprunt = EmpruntMapper.versEntite(request);
        Emprunt empruntCree = empruntService.creer(emprunt);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EmpruntMapper.versResponse(empruntCree));
    }

    /**
     * Endpoint 18 : marque un emprunt comme rendu.
     * Un emprunt déjà rendu ne peut pas être retourné une seconde fois.
     */
    @Operation(summary = "Enregistrer le retour d'un emprunt")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt marqué comme rendu"),
            @ApiResponse(responseCode = "404", description = "Emprunt inexistant"),
            @ApiResponse(responseCode = "409", description = "Emprunt déjà rendu")
    })
    @PatchMapping("/{id}/retour")
    public ResponseEntity<EmpruntResponse> retourner(@PathVariable Long id) {
        Optional<Emprunt> resultat = empruntService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Emprunt emprunt = resultat.get();
        if (Boolean.TRUE.equals(emprunt.getRendu())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Emprunt empruntRetourne = empruntService.retourner(emprunt);
        return ResponseEntity.ok(EmpruntMapper.versResponse(empruntRetourne));
    }

    /** Endpoint 19 : renvoie la page des emprunts dont le livre n'est pas encore rendu. */
    @Operation(summary = "Lister les emprunts en cours")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts non rendus")
    })
    @GetMapping("/en-cours")
    public ResponseEntity<PagedModel<EmpruntResponse>> listerEnCours(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererEnCours(pageable);
        return ResponseEntity.ok(EmpruntMapper.versPage(page));
    }

    /** Endpoint 20 : renvoie la page des emprunts dont la date de retour est dépassée. */
    @Operation(summary = "Lister les emprunts en retard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts en retard")
    })
    @GetMapping("/en-retard")
    public ResponseEntity<PagedModel<EmpruntResponse>> listerEnRetard(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererEnRetard(pageable);
        return ResponseEntity.ok(EmpruntMapper.versPage(page));
    }
}
