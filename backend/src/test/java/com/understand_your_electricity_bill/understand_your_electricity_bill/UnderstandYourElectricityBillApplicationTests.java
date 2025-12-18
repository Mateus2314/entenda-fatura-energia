package com.understand_your_electricity_bill.understand_your_electricity_bill;

import com.understand_your_electricity_bill.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UnderstandYourElectricityBillApplicationTests {

	@Autowired(required = false)
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		// Verifica que o contexto carrega corretamente
	}

	@Test
	void userRepositoryIsAvailable() {
		// Verifica que o UserRepository foi criado pelo Spring Data JPA
		assertThat(userRepository).isNotNull();
	}
}
