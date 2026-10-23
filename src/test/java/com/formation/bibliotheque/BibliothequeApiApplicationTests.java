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
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** Le contexte Spring doit pouvoir démarrer avec la configuration courante. */
	@Test
	void contextLoads() {
	}

	/** Le script data.sql doit charger les trois emprunts de démonstration. */
	@Test
	void loadsInitialData() {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM emprunts", Integer.class)).isEqualTo(3);
	}

}
