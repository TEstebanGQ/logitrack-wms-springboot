package com.proyecto.proyectoSpringBoot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProyectoSpringBootApplicationTests {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
		assertNotNull(passwordEncoder);
		assertTrue(passwordEncoder.matches("admin123", "$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm"));
	}

}
