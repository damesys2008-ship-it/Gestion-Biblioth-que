package com.formation.bibliotheque; // Place l'application dans le package racine détecté par Spring.

import org.springframework.boot.SpringApplication; // Fournit le point d'entrée standard de Spring Boot.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Active la configuration automatique de Spring Boot.

@SpringBootApplication // Configure l'application et le scan des composants du package.
public class BibliothequeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliothequeApiApplication.class, args); // Démarre le contexte Spring et le serveur web.
	}

}
