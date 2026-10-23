package com.formation.bibliotheque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/** Vérifie le démarrage de l'application et le chargement des données de test. */
@SpringBootTest
class BibliothequeApiApplicationTests {

	/** Accès SQL utilisé pour vérifier le contenu initial de la base H2. */
	@Autowired // Injecte l'accès JDBC fourni par Spring Boot.
	private JdbcTemplate jdbcTemplate; // Permet de vérifier les données initiales en base.

	/** Le contexte Spring doit pouvoir démarrer avec la configuration courante. */
	@Test // Exécute ce contrôle dans la suite de tests JUnit.
	void contextLoads() {
		// Le succès du test confirme que le contexte Spring a démarré.
	}

	/** Le script data.sql doit charger les trois emprunts de démonstration. */
	@Test // Exécute la vérification des données chargées au démarrage.
	void loadsInitialData() {
		// Interroge la table et vérifie que le script a inséré trois emprunts.
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM emprunts", Integer.class)).isEqualTo(3);
	}

}
