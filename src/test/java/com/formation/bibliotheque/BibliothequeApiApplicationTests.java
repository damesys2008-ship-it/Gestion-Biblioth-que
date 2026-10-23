package com.formation.bibliotheque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BibliothequeApiApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void loadsInitialData() {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM emprunts", Integer.class)).isEqualTo(3);
	}

}
