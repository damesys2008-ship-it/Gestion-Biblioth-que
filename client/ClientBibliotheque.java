import java.net.URI; // Construit les URI complètes appelées par le client HTTP.
import java.net.http.HttpClient; // Fournit le client HTTP réutilisable.
import java.net.http.HttpRequest; // Représente les requêtes HTTP envoyées à l'API.
import java.net.http.HttpResponse; // Représente les réponses HTTP reçues.

/**
 * Petit client Java autonome utilisé pour exécuter le scénario de démonstration Q33.
 * Ce programme appelle l'API comme le ferait une application cliente externe.
 */
public class ClientBibliotheque {

    /** Adresse de base de l'API locale ; les méthodes ajoutent le chemin demandé. */
    private static final String BASE_URL = "http://localhost:8080/api/v1"; // Préfixe commun des routes de l'API locale.

    /** Client HTTP réutilisé pour envoyer toutes les requêtes du scénario. */
    private static final HttpClient client = HttpClient.newHttpClient(); // Réutilise le même client pour chaque étape.

    /** Nombre total de requêtes exécutées dans le scénario. */
    private static int nombreEtapes = 0; // Compte le nombre de requêtes exécutées.

    /** Nombre de réponses dont le statut correspond au statut attendu. */
    private static int nombreConformes = 0; // Compte les statuts égaux aux résultats attendus.

    /**
     * Envoie une requête GET et demande une réponse JSON.
     *
     * @param chemin chemin relatif à la base de l'API
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> get(String chemin) throws Exception { // Envoie une requête de lecture GET.
        HttpRequest requete = HttpRequest.newBuilder() // Commence la construction de la requête HTTP.
                .uri(URI.create(BASE_URL + chemin)) // Définit l'adresse de la ressource demandée.
                .header("Accept", "application/json") // Demande une réponse au format JSON.
                .GET() // Définit la méthode HTTP GET.
                .build(); // Construit la requête prête à être envoyée.
        return client.send(requete, HttpResponse.BodyHandlers.ofString()); // Envoie la requête et lit le corps texte.
    }

    /**
     * Envoie une requête POST contenant un document JSON.
     *
     * @param chemin chemin relatif à la base de l'API
     * @param corpsJson contenu JSON de la requête
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> post(String chemin, String corpsJson) throws Exception { // Envoie une requête de création POST.
        HttpRequest requete = HttpRequest.newBuilder() // Commence la construction de la requête HTTP.
                .uri(URI.create(BASE_URL + chemin)) // Définit l'adresse de la ressource à créer.
                .header("Accept", "application/json") // Demande une réponse au format JSON.
                .header("Content-Type", "application/json") // Indique que le corps envoyé est du JSON.
                .POST(HttpRequest.BodyPublishers.ofString(corpsJson)) // Ajoute le JSON au corps de la requête.
                .build(); // Construit la requête prête à être envoyée.
        return client.send(requete, HttpResponse.BodyHandlers.ofString()); // Envoie la requête et lit le corps texte.
    }

    /**
     * Envoie une requête PATCH sans corps, utilisée ici pour rendre un emprunt.
     *
     * @param chemin chemin relatif à la base de l'API
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> patch(String chemin) throws Exception { // Envoie une mise à jour partielle PATCH.
        HttpRequest requete = HttpRequest.newBuilder() // Commence la construction de la requête HTTP.
                .uri(URI.create(BASE_URL + chemin)) // Définit l'adresse de la ressource à mettre à jour.
                .header("Accept", "application/json") // Demande une réponse au format JSON.
                .method("PATCH", HttpRequest.BodyPublishers.noBody()) // Définit PATCH sans corps de requête.
                .build(); // Construit la requête prête à être envoyée.
        return client.send(requete, HttpResponse.BodyHandlers.ofString()); // Envoie la requête et lit le corps texte.
    }

    /**
     * Envoie une requête DELETE pour supprimer une ressource.
     *
     * @param chemin chemin relatif à la base de l'API
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> delete(String chemin) throws Exception { // Envoie une requête de suppression DELETE.
        HttpRequest requete = HttpRequest.newBuilder() // Commence la construction de la requête HTTP.
                .uri(URI.create(BASE_URL + chemin)) // Définit l'adresse de la ressource à supprimer.
                .header("Accept", "application/json") // Demande une réponse au format JSON.
                .DELETE() // Définit la méthode HTTP DELETE.
                .build(); // Construit la requête prête à être envoyée.
        return client.send(requete, HttpResponse.BodyHandlers.ofString()); // Envoie la requête et lit le corps texte.
    }

    /**
     * Affiche le résultat d'une étape au format demandé et met à jour le bilan.
     *
     * @param description résumé lisible de l'étape
     * @param methode méthode HTTP utilisée
     * @param chemin chemin appelé
     * @param reponse réponse reçue de l'API
     * @param codeAttendu code HTTP prévu par l'énoncé
     */
    private static void afficher(
            String description,
            String methode,
            String chemin,
            HttpResponse<String> reponse,
            int codeAttendu) {
        nombreEtapes++; // Avance le compteur avant d'afficher le résultat de l'étape.

        // Une étape est conforme uniquement si le statut reçu correspond au statut prévu.
        boolean conforme = reponse.statusCode() == codeAttendu; // Compare le statut reçu au statut attendu.
        if (conforme) { // Ne compte comme conforme qu'une réponse au statut prévu.
            nombreConformes++; // Ajoute cette étape au bilan des réponses conformes.
        }

        // Les quatre lignes et la ligne vide qui suit respectent le format de Q33.
        System.out.println("=== Étape " + nombreEtapes + " : " + description + " ==="); // Affiche le titre de l'étape.
        System.out.println(methode + " " + chemin + " → " + reponse.statusCode()
                + " (attendu : " + codeAttendu + ") "
                + (conforme ? "CONFORME" : "NON CONFORME"));
        System.out.println("Réponse : " + reponse.body()); // Affiche le corps renvoyé par le serveur.
        System.out.println(); // Sépare visuellement deux étapes.
    }

    /**
     * Exécute les dix requêtes du fil rouge dans l'ordre et affiche le bilan final.
     *
     * @param args arguments de ligne de commande (non utilisés)
     * @throws Exception si une requête HTTP échoue au niveau transport
     */
    public static void main(String[] args) throws Exception {
        // Étape 1 : vérifie la liste paginée des livres disponibles.
        afficher(
                "Lister les 3 premiers livres disponibles",
                "GET",
                "/livres/disponibles?size=3",
                get("/livres/disponibles?size=3"),
                200);

        // Étape 2 : vérifie le code renvoyé pour un livre inexistant.
        afficher(
                "Lire un livre inexistant",
                "GET",
                "/livres/99",
                get("/livres/99"),
                404);

        // Étape 3 : crée l'auteur nécessaire aux étapes suivantes du scénario.
        afficher(
                "Créer l'auteur Jules Verne",
                "POST",
                "/auteurs",
                post("/auteurs", """
                        {"nom": "Verne", "prenom": "Jules", "nationalite": "Française"}
                        """),
                201);

        // Étape 4 : crée un emprunt valide pour le livre 2.
        afficher(
                "Emprunter le livre 2",
                "POST",
                "/emprunts",
                post("/emprunts", """
                        {
                          "livreId": 2,
                          "emprunteur": "Alice Durand",
                          "emailEmprunteur": "alice.durand@email.com"
                        }
                        """),
                201);

        // Étape 5 : le stock nul du livre 8 doit empêcher sa réservation.
        afficher(
                "Emprunter le livre 8 (stock à 0)",
                "POST",
                "/emprunts",
                post("/emprunts", """
                        {
                          "livreId": 8,
                          "emprunteur": "Alice Durand",
                          "emailEmprunteur": "alice.durand@email.com"
                        }
                        """),
                409);

        // Étape 6 : effectue le premier retour de l'emprunt 2.
        afficher(
                "Rendre l'emprunt 2",
                "PATCH",
                "/emprunts/2/retour",
                patch("/emprunts/2/retour"),
                200);

        // Étape 7 : vérifie qu'un emprunt rendu ne peut pas l'être une seconde fois.
        afficher(
                "Rendre l'emprunt 2 une deuxième fois",
                "PATCH",
                "/emprunts/2/retour",
                patch("/emprunts/2/retour"),
                409);

        // Étape 8 : un auteur qui possède des livres ne doit pas pouvoir être supprimé.
        afficher(
                "Supprimer l'auteur 1 (il a des livres)",
                "DELETE",
                "/auteurs/1",
                delete("/auteurs/1"),
                409);

        // Étape 9 : la validation doit refuser un livre au titre vide et au prix négatif.
        afficher(
                "Créer un livre invalide (titre vide, prix négatif)",
                "POST",
                "/livres",
                post("/livres", """
                        {
                          "titre": "",
                          "isbn": "978-2-0000-0000-0",
                          "prix": -5,
                          "stock": 1,
                          "auteurId": 1
                        }
                        """),
                400);

        // Étape 10 : l'auteur 5 n'ayant aucun livre, sa suppression doit réussir.
        afficher(
                "Supprimer l'auteur 5 (aucun livre)",
                "DELETE",
                "/auteurs/5",
                delete("/auteurs/5"),
                204);

        // Bilan : compare le nombre de réponses conformes au nombre d'étapes exécutées.
        System.out.println("Bilan : " + nombreConformes + "/" + nombreEtapes + " étapes conformes"); // Affiche le résultat global du scénario.
    }
}
