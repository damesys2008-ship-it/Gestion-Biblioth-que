import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Petit client Java autonome utilisé pour exécuter le scénario de démonstration Q33.
 * Ce programme appelle l'API comme le ferait une application cliente externe.
 */
public class ClientBibliotheque {

    /** Adresse de base de l'API locale ; les méthodes ajoutent le chemin demandé. */
    private static final String BASE_URL = "http://localhost:8080/api/v1";

    /** Client HTTP réutilisé pour envoyer toutes les requêtes du scénario. */
    private static final HttpClient client = HttpClient.newHttpClient();

    /** Nombre total de requêtes exécutées dans le scénario. */
    private static int nombreEtapes = 0;

    /** Nombre de réponses dont le statut correspond au statut attendu. */
    private static int nombreConformes = 0;

    /**
     * Envoie une requête GET et demande une réponse JSON.
     *
     * @param chemin chemin relatif à la base de l'API
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> get(String chemin) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .header("Accept", "application/json")
                .GET()
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Envoie une requête POST contenant un document JSON.
     *
     * @param chemin chemin relatif à la base de l'API
     * @param corpsJson contenu JSON de la requête
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> post(String chemin, String corpsJson) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpsJson))
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Envoie une requête PATCH sans corps, utilisée ici pour rendre un emprunt.
     *
     * @param chemin chemin relatif à la base de l'API
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> patch(String chemin) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .header("Accept", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Envoie une requête DELETE pour supprimer une ressource.
     *
     * @param chemin chemin relatif à la base de l'API
     * @return réponse HTTP reçue
     * @throws Exception si la requête ne peut pas être envoyée
     */
    private static HttpResponse<String> delete(String chemin) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
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
        nombreEtapes++;

        // Une étape est conforme uniquement si le statut reçu correspond au statut prévu.
        boolean conforme = reponse.statusCode() == codeAttendu;
        if (conforme) {
            nombreConformes++;
        }

        // Les quatre lignes et la ligne vide qui suit respectent le format de Q33.
        System.out.println("=== Étape " + nombreEtapes + " : " + description + " ===");
        System.out.println(methode + " " + chemin + " → " + reponse.statusCode()
                + " (attendu : " + codeAttendu + ") "
                + (conforme ? "CONFORME" : "NON CONFORME"));
        System.out.println("Réponse : " + reponse.body());
        System.out.println();
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
        System.out.println("Bilan : " + nombreConformes + "/" + nombreEtapes + " étapes conformes");
    }
}
