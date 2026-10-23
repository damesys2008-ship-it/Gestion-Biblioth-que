package com.formation.bibliotheque.controller;

import com.formation.bibliotheque.dto.LivreRequest;
import com.formation.bibliotheque.dto.LivreResponse;
import com.formation.bibliotheque.mapper.LivreMapper;
import com.formation.bibliotheque.model.Livre;
import com.formation.bibliotheque.service.LivreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/livres")
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<LivreResponse>> listerTous(
            @PageableDefault(size = 10, sort = "titre") Pageable pageable) {
        Page<Livre> page = livreService.recupererTous(pageable);
        return ResponseEntity.ok(LivreMapper.versPage(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivreResponse> obtenirParId(@PathVariable Long id) {
        Optional<Livre> resultat = livreService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(LivreMapper.versResponse(resultat.get()));
    }

    @PostMapping
    public ResponseEntity<LivreResponse> creer(@Valid @RequestBody LivreRequest request) {
        if (!livreService.auteurExiste(request.getAuteurId())) {
            return ResponseEntity.badRequest().build();
        }
        if (livreService.isbnExiste(request.getIsbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Livre livreCree = livreService.creer(LivreMapper.versEntite(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(LivreMapper.versResponse(livreCree));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivreResponse> mettreAJour(
            @PathVariable Long id,
            @Valid @RequestBody LivreRequest request) {
        Optional<Livre> resultat = livreService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!livreService.auteurExiste(request.getAuteurId())) {
            return ResponseEntity.badRequest().build();
        }
        if (livreService.isbnUtiliseParUnAutreLivre(request.getIsbn(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Livre livreModifie = livreService.mettreAJour(
                resultat.get(),
                LivreMapper.versEntite(request));
        return ResponseEntity.ok(LivreMapper.versResponse(livreModifie));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!livreService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        livreService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recherche")
    public ResponseEntity<PagedModel<LivreResponse>> rechercher(
            @RequestParam String titre,
            @PageableDefault(size = 10, sort = "titre") Pageable pageable) {
        Page<Livre> page = livreService.rechercherParTitre(titre, pageable);
        return ResponseEntity.ok(LivreMapper.versPage(page));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<PagedModel<LivreResponse>> listerDisponibles(
            @PageableDefault(size = 10, sort = "titre") Pageable pageable) {
        Page<Livre> page = livreService.recupererDisponibles(pageable);
        return ResponseEntity.ok(LivreMapper.versPage(page));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<LivreResponse> ajouterStock(
            @PathVariable Long id,
            @RequestParam Integer quantite) {
        if (quantite <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Livre> resultat = livreService.recupererParId(id);
        if (resultat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Livre livreModifie = livreService.ajouterStock(resultat.get(), quantite);
        return ResponseEntity.ok(LivreMapper.versResponse(livreModifie));
    }
}
