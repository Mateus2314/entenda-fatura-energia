package com.understand_your_electricity_bill.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Initializer to load environment variables from .env file for tests.
 * This ensures that DB_URL, DB_USER, and DB_PASS are available during test execution.
 */
public class DotenvTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> envMap = new HashMap<>();

            // Load specific environment variables needed for tests
            if (dotenv.get("DB_URL") != null) {
                envMap.put("DB_URL", dotenv.get("DB_URL"));
            }
            if (dotenv.get("DB_USER") != null) {
                envMap.put("DB_USER", dotenv.get("DB_USER"));
            }
            if (dotenv.get("DB_PASS") != null) {
                envMap.put("DB_PASS", dotenv.get("DB_PASS"));
            }

            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", envMap));

        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file for tests: " + e.getMessage());
        }
    }
}

