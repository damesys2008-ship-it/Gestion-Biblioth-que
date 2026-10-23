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
@RestController // Expose cette classe comme contrôleur REST Spring.
@RequestMapping("/api/v1/emprunts") // Définit le préfixe commun des routes d'emprunt.
@Tag(name = "Emprunts", description = "Emprunts et retours des livres") // Regroupe les endpoints dans Swagger.
public class EmpruntController {

    /** Service qui applique les règles métier des emprunts. */
    private final EmpruntService empruntService;

    /** Injecte le service utilisé par les endpoints HTTP. */
    public EmpruntController(EmpruntService empruntService) {
        this.empruntService = empruntService; // Conserve le service injecté par Spring.
    }

    /** Endpoint 15 : renvoie une page d'emprunts, triée par date d'emprunt par défaut. */
    @Operation(summary = "Lister les emprunts") // Documente l'opération dans Swagger.
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts")
    })
    @GetMapping // Associe la méthode à GET /api/v1/emprunts.
    public ResponseEntity<PagedModel<EmpruntResponse>> listerTous(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererTous(pageable); // Demande la page d'emprunts au service.
        return ResponseEntity.ok(EmpruntMapper.versPage(page)); // Convertit la page et répond avec HTTP 200.
    }

    /** Endpoint 16 : renvoie un emprunt, ou 404 si son identifiant est inconnu. */
    @Operation(summary = "Obtenir un emprunt par son identifiant") // Décrit la lecture d'un emprunt dans Swagger.
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt trouvé"),
            @ApiResponse(responseCode = "404", description = "Emprunt inexistant")
    })
    @GetMapping("/{id}") // Associe la méthode à GET /api/v1/emprunts/{id}.
    public ResponseEntity<EmpruntResponse> obtenirParId(@PathVariable Long id) {
        Optional<Emprunt> resultat = empruntService.recupererParId(id); // Cherche l'emprunt demandé.
        if (resultat.isEmpty()) { // Vérifie si l'identifiant existe.
            return ResponseEntity.notFound().build(); // Répond 404 si aucun emprunt ne correspond.
        }
        return ResponseEntity.ok(EmpruntMapper.versResponse(resultat.get())); // Convertit l'entité trouvée en réponse 200.
    }

    /** Endpoint 17 : valide la requête puis crée un emprunt. */
    @Operation(summary = "Créer un emprunt") // Décrit la création dans Swagger.
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Emprunt créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping // Associe la méthode à POST /api/v1/emprunts.
    public ResponseEntity<EmpruntResponse> creer(@Valid @RequestBody EmpruntRequest request) {
        Emprunt emprunt = EmpruntMapper.versEntite(request); // Convertit les champs reçus en entité.
        Emprunt empruntCree = empruntService.creer(emprunt); // Applique les règles métier et persiste l'emprunt.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EmpruntMapper.versResponse(empruntCree)); // Renvoie le DTO créé avec HTTP 201.
    }

    /**
     * Endpoint 18 : marque un emprunt comme rendu.
     * Un emprunt déjà rendu ne peut pas être retourné une seconde fois.
     */
    @Operation(summary = "Enregistrer le retour d'un emprunt") // Décrit l'enregistrement d'un retour.
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt marqué comme rendu"),
            @ApiResponse(responseCode = "404", description = "Emprunt inexistant"),
            @ApiResponse(responseCode = "409", description = "Emprunt déjà rendu")
    })
    @PatchMapping("/{id}/retour") // Associe la méthode à PATCH /api/v1/emprunts/{id}/retour.
    public ResponseEntity<EmpruntResponse> retourner(@PathVariable Long id) {
        Optional<Emprunt> resultat = empruntService.recupererParId(id); // Cherche l'emprunt à retourner.
        if (resultat.isEmpty()) { // Vérifie si l'identifiant correspond à un emprunt.
            return ResponseEntity.notFound().build(); // Répond 404 lorsque l'emprunt est absent.
        }

        Emprunt emprunt = resultat.get(); // Récupère l'entité après vérification de présence.
        if (Boolean.TRUE.equals(emprunt.getRendu())) { // Évite d'enregistrer un second retour.
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Répond 409 si le livre est déjà rendu.
        }

        Emprunt empruntRetourne = empruntService.retourner(emprunt); // Enregistre l'état rendu et sa date.
        return ResponseEntity.ok(EmpruntMapper.versResponse(empruntRetourne)); // Retourne le résultat avec HTTP 200.
    }

    /** Endpoint 19 : renvoie la page des emprunts dont le livre n'est pas encore rendu. */
    @Operation(summary = "Lister les emprunts en cours") // Décrit le filtre des emprunts non rendus.
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts non rendus")
    })
    @GetMapping("/en-cours") // Associe la méthode à GET /api/v1/emprunts/en-cours.
    public ResponseEntity<PagedModel<EmpruntResponse>> listerEnCours(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererEnCours(pageable); // Demande les emprunts non rendus au service.
        return ResponseEntity.ok(EmpruntMapper.versPage(page)); // Convertit la page et répond avec HTTP 200.
    }

    /** Endpoint 20 : renvoie la page des emprunts dont la date de retour est dépassée. */
    @Operation(summary = "Lister les emprunts en retard") // Décrit le filtre des emprunts échus.
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page des emprunts en retard")
    })
    @GetMapping("/en-retard") // Associe la méthode à GET /api/v1/emprunts/en-retard.
    public ResponseEntity<PagedModel<EmpruntResponse>> listerEnRetard(
            @PageableDefault(size = 10, sort = "dateEmprunt") Pageable pageable) {
        Page<Emprunt> page = empruntService.recupererEnRetard(pageable); // Demande les emprunts en retard au service.
        return ResponseEntity.ok(EmpruntMapper.versPage(page)); // Convertit la page et répond avec HTTP 200.
    }
}
