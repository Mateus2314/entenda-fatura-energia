package com.understand_your_electricity_bill.understand_your_electricity_bill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.understand_your_electricity_bill")
@EnableJpaRepositories(basePackages = "com.understand_your_electricity_bill.repository")
@EntityScan(basePackages = "com.understand_your_electricity_bill.model")
public class UnderstandYourElectricityBillApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnderstandYourElectricityBillApplication.class, args);
	}

}
