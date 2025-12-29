package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.Tariff;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.understand_your_electricity_bill.understand_your_electricity_bill.UnderstandYourElectricityBillApplication.class)
@ActiveProfiles("test")
class TariffRepositoryTest {

    // Load .env file
    static {
        Dotenv dotenv = Dotenv.configure()
                .directory("../../")
                .ignoreIfMissing()
                .load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TariffRepository tariffRepository;

    private Tariff testTariff1;
    private Tariff testTariff2;
    private Tariff testTariff3;
    private Tariff expiredTariff;

    @BeforeEach
    void setUp() {
        // Tariff 1 - CPFL PAULISTA - Residential B1 (Currently Valid)
        testTariff1 = new Tariff();
        testTariff1.setGenerationDate(LocalDate.of(2024, 12, 1));
        testTariff1.setDescriptionReh("RESOLUÇÃO HOMOLOGATÓRIA Nº 3.500/2024");
        testTariff1.setDistributor("CPFL PAULISTA");
        testTariff1.setCnpjDistributor("02198431000104");
        testTariff1.setValidFrom(LocalDate.of(2024, 12, 1));
        testTariff1.setValidUntil(null); // Currently valid
        testTariff1.setTariffBaseDesc("Tarifa de Aplicação");
        testTariff1.setSubgroup("B1");
        testTariff1.setTariffModality("Convencional");
        testTariff1.setConsumerClass("Residencial");
        testTariff1.setConsumerSubclass("Residencial");
        testTariff1.setDetail("Normal");
        testTariff1.setTariffPostName("Não se aplica");
        testTariff1.setTertiaryUnit("kWh");
        testTariff1.setAccessingAgent("Não se aplica");
        testTariff1.setTusdValue(new BigDecimal("0.3450"));
        testTariff1.setTeValue(new BigDecimal("0.4250"));
        testTariff1.setFlagGenerationDate(LocalDate.of(2024, 12, 1));
        testTariff1.setCompetenceDate(LocalDate.of(2024, 12, 1));
        testTariff1.setActivatedFlagName("Verde");
        testTariff1.setFlagAdditionalValue(BigDecimal.ZERO);
        testTariff1.setCreatedAt(LocalDateTime.now());
        testTariff1.setUpdatedAt(LocalDateTime.now());

        // Tariff 2 - LIGHT - Residential B1 (Currently Valid)
        testTariff2 = new Tariff();
        testTariff2.setGenerationDate(LocalDate.of(2024, 11, 15));
        testTariff2.setDescriptionReh("RESOLUÇÃO HOMOLOGATÓRIA Nº 3.495/2024");
        testTariff2.setDistributor("LIGHT");
        testTariff2.setCnpjDistributor("60444437000171");
        testTariff2.setValidFrom(LocalDate.of(2024, 11, 15));
        testTariff2.setValidUntil(null); // Currently valid
        testTariff2.setTariffBaseDesc("Tarifa de Aplicação");
        testTariff2.setSubgroup("B1");
        testTariff2.setTariffModality("Convencional");
        testTariff2.setConsumerClass("Residencial");
        testTariff2.setConsumerSubclass("Residencial");
        testTariff2.setDetail("Normal");
        testTariff2.setTariffPostName("Não se aplica");
        testTariff2.setTertiaryUnit("kWh");
        testTariff2.setAccessingAgent("Não se aplica");
        testTariff2.setTusdValue(new BigDecimal("0.3680"));
        testTariff2.setTeValue(new BigDecimal("0.4520"));
        testTariff2.setFlagGenerationDate(LocalDate.of(2024, 11, 15));
        testTariff2.setCompetenceDate(LocalDate.of(2024, 11, 1));
        testTariff2.setActivatedFlagName("Amarela");
        testTariff2.setFlagAdditionalValue(new BigDecimal("1.7800"));
        testTariff2.setCreatedAt(LocalDateTime.now());
        testTariff2.setUpdatedAt(LocalDateTime.now());

        // Tariff 3 - CEMIG - Commercial B3 (Currently Valid)
        testTariff3 = new Tariff();
        testTariff3.setGenerationDate(LocalDate.of(2024, 10, 20));
        testTariff3.setDescriptionReh("RESOLUÇÃO HOMOLOGATÓRIA Nº 3.480/2024");
        testTariff3.setDistributor("CEMIG");
        testTariff3.setCnpjDistributor("06981180000116");
        testTariff3.setValidFrom(LocalDate.of(2024, 10, 20));
        testTariff3.setValidUntil(null); // Currently valid
        testTariff3.setTariffBaseDesc("Tarifa de Aplicação");
        testTariff3.setSubgroup("B3");
        testTariff3.setTariffModality("Convencional");
        testTariff3.setConsumerClass("Comercial");
        testTariff3.setConsumerSubclass("Comercial");
        testTariff3.setDetail("Normal");
        testTariff3.setTariffPostName("Não se aplica");
        testTariff3.setTertiaryUnit("kWh");
        testTariff3.setAccessingAgent("Não se aplica");
        testTariff3.setTusdValue(new BigDecimal("0.3890"));
        testTariff3.setTeValue(new BigDecimal("0.4780"));
        testTariff3.setFlagGenerationDate(LocalDate.of(2024, 10, 20));
        testTariff3.setCompetenceDate(LocalDate.of(2024, 10, 1));
        testTariff3.setActivatedFlagName("Vermelha P1");
        testTariff3.setFlagAdditionalValue(new BigDecimal("4.3200"));
        testTariff3.setCreatedAt(LocalDateTime.now());
        testTariff3.setUpdatedAt(LocalDateTime.now());

        // Expired Tariff - CPFL PAULISTA (Expired)
        expiredTariff = new Tariff();
        expiredTariff.setGenerationDate(LocalDate.of(2023, 1, 1));
        expiredTariff.setDescriptionReh("RESOLUÇÃO HOMOLOGATÓRIA Nº 3.000/2023");
        expiredTariff.setDistributor("CPFL PAULISTA");
        expiredTariff.setCnpjDistributor("02198431000104");
        expiredTariff.setValidFrom(LocalDate.of(2023, 1, 1));
        expiredTariff.setValidUntil(LocalDate.of(2023, 12, 31)); // Expired
        expiredTariff.setTariffBaseDesc("Tarifa de Aplicação");
        expiredTariff.setSubgroup("B1");
        expiredTariff.setTariffModality("Convencional");
        expiredTariff.setConsumerClass("Residencial");
        expiredTariff.setConsumerSubclass("Residencial");
        expiredTariff.setDetail("Normal");
        expiredTariff.setTariffPostName("Não se aplica");
        expiredTariff.setTertiaryUnit("kWh");
        expiredTariff.setAccessingAgent("Não se aplica");
        expiredTariff.setTusdValue(new BigDecimal("0.3000"));
        expiredTariff.setTeValue(new BigDecimal("0.3800"));
        expiredTariff.setFlagGenerationDate(LocalDate.of(2023, 1, 1));
        expiredTariff.setCompetenceDate(LocalDate.of(2023, 1, 1));
        expiredTariff.setActivatedFlagName("Verde");
        expiredTariff.setFlagAdditionalValue(BigDecimal.ZERO);
        expiredTariff.setCreatedAt(LocalDateTime.now());
        expiredTariff.setUpdatedAt(LocalDateTime.now());
    }

    // ========================================================================
    // BASIC CRUD TESTS
    // ========================================================================

    @Test
    @DisplayName("Should save tariff successfully")
    void shouldSaveTariff() {
        // When
        Tariff savedTariff = tariffRepository.save(testTariff1);

        // Then
        assertThat(savedTariff).isNotNull();
        assertThat(savedTariff.getId()).isNotNull();
        assertThat(savedTariff.getDistributor()).isEqualTo("CPFL PAULISTA");
        assertThat(savedTariff.getSubgroup()).isEqualTo("B1");
    }

    @Test
    @DisplayName("Should find tariff by ID")
    void shouldFindTariffById() {
        // Given
        Tariff savedTariff = entityManager.persistAndFlush(testTariff1);

        // When
        Optional<Tariff> foundTariff = tariffRepository.findById(savedTariff.getId());

        // Then
        assertThat(foundTariff).isPresent();
        assertThat(foundTariff.get().getDistributor()).isEqualTo("CPFL PAULISTA");
    }

    @Test
    @DisplayName("Should find all tariffs")
    void shouldFindAllTariffs() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);
        entityManager.persistAndFlush(testTariff3);

        // When
        List<Tariff> tariffs = tariffRepository.findAll();

        // Then
        assertThat(tariffs).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Should update tariff")
    void shouldUpdateTariff() {
        // Given
        Tariff savedTariff = entityManager.persistAndFlush(testTariff1);
        BigDecimal newTusdValue = new BigDecimal("0.4000");

        // When
        savedTariff.setTusdValue(newTusdValue);
        Tariff updatedTariff = tariffRepository.save(savedTariff);

        // Then
        assertThat(updatedTariff.getTusdValue()).isEqualByComparingTo(newTusdValue);
    }

    @Test
    @DisplayName("Should delete tariff")
    void shouldDeleteTariff() {
        // Given
        Tariff savedTariff = entityManager.persistAndFlush(testTariff1);
        Long countBefore = tariffRepository.count();

        // When
        tariffRepository.delete(savedTariff);
        Long countAfter = tariffRepository.count();

        // Then
        assertThat(countAfter).isLessThan(countBefore);
    }

    // ========================================================================
    // SEARCH BY DISTRIBUTOR TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find tariffs by distributor")
    void shouldFindByDistributor() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);

        // When
        List<Tariff> cpflTariffs = tariffRepository.findByDistributor("CPFL PAULISTA");

        // Then
        assertThat(cpflTariffs).isNotEmpty();
        assertThat(cpflTariffs).allMatch(t -> t.getDistributor().equals("CPFL PAULISTA"));
    }

    @Test
    @DisplayName("Should find tariffs by CNPJ distributor")
    void shouldFindByCnpjDistributor() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);

        // When
        List<Tariff> tariffs = tariffRepository.findByCnpjDistributor("02198431000104");

        // Then
        assertThat(tariffs).isNotEmpty();
        assertThat(tariffs).allMatch(t -> t.getCnpjDistributor().equals("02198431000104"));
    }

    @Test
    @DisplayName("Should find tariffs by distributor containing (case-insensitive)")
    void shouldFindByDistributorContaining() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);

        // When
        List<Tariff> tariffs = tariffRepository.findByDistributorContainingIgnoreCase("cpfl");

        // Then
        assertThat(tariffs).isNotEmpty();
        assertThat(tariffs).allMatch(t -> t.getDistributor().toUpperCase().contains("CPFL"));
    }

    @Test
    @DisplayName("Should count tariffs by distributor")
    void shouldCountByDistributor() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(expiredTariff);

        // When
        long count = tariffRepository.countByDistributor("CPFL PAULISTA");

        // Then
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    // ========================================================================
    // SEARCH BY SUBGROUP AND MODALITY TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find tariffs by subgroup")
    void shouldFindBySubgroup() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);
        entityManager.persistAndFlush(testTariff3);

        // When
        List<Tariff> b1Tariffs = tariffRepository.findBySubgroup("B1");

        // Then
        assertThat(b1Tariffs).hasSizeGreaterThanOrEqualTo(2);
        assertThat(b1Tariffs).allMatch(t -> t.getSubgroup().equals("B1"));
    }

    @Test
    @DisplayName("Should find tariffs by tariff modality")
    void shouldFindByTariffModality() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);

        // When
        List<Tariff> conventionalTariffs = tariffRepository.findByTariffModality("Convencional");

        // Then
        assertThat(conventionalTariffs).isNotEmpty();
        assertThat(conventionalTariffs).allMatch(t -> t.getTariffModality().equals("Convencional"));
    }

    @Test
    @DisplayName("Should find tariffs by consumer class")
    void shouldFindByConsumerClass() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff3);

        // When
        List<Tariff> residentialTariffs = tariffRepository.findByConsumerClass("Residencial");

        // Then
        assertThat(residentialTariffs).isNotEmpty();
        assertThat(residentialTariffs).allMatch(t -> t.getConsumerClass().equals("Residencial"));
    }

    @Test
    @DisplayName("Should find tariffs by consumer subclass")
    void shouldFindByConsumerSubclass() {
        // Given
        entityManager.persistAndFlush(testTariff1);

        // When
        List<Tariff> tariffs = tariffRepository.findByConsumerSubclass("Residencial");

        // Then
        assertThat(tariffs).isNotEmpty();
    }

    // ========================================================================
    // VALIDITY PERIOD TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find currently valid tariffs")
    void shouldFindCurrentlyValidTariffs() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(expiredTariff);

        // When
        List<Tariff> currentTariffs = tariffRepository.findCurrentlyValid();

        // Then
        assertThat(currentTariffs).isNotEmpty();
        assertThat(currentTariffs).allMatch(t ->
            t.getValidFrom().isBefore(LocalDate.now().plusDays(1)) &&
            (t.getValidUntil() == null || t.getValidUntil().isAfter(LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("Should find tariffs valid at specific date")
    void shouldFindValidAtDate() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(expiredTariff);
        LocalDate testDate = LocalDate.of(2024, 12, 15);

        // When
        List<Tariff> validTariffs = tariffRepository.findValidAt(testDate);

        // Then
        assertThat(validTariffs).isNotEmpty();
        assertThat(validTariffs).noneMatch(t -> t.getValidUntil() != null && t.getValidUntil().isBefore(testDate));
    }

    @Test
    @DisplayName("Should find expired tariffs")
    void shouldFindExpiredTariffs() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(expiredTariff);

        // When
        List<Tariff> expired = tariffRepository.findExpiredBefore(LocalDate.now());

        // Then
        assertThat(expired).isNotEmpty();
        assertThat(expired).allMatch(t -> t.getValidUntil() != null && t.getValidUntil().isBefore(LocalDate.now()));
    }

    @Test
    @DisplayName("Should find future tariffs")
    void shouldFindFutureTariffs() {
        // Given
        Tariff futureTariff = new Tariff();
        futureTariff.setGenerationDate(LocalDate.now());
        futureTariff.setDistributor("FUTURE DISTRIBUTOR");
        futureTariff.setCnpjDistributor("12345678901234");
        futureTariff.setValidFrom(LocalDate.now().plusMonths(1));
        futureTariff.setSubgroup("B1");
        futureTariff.setTusdValue(new BigDecimal("0.5000"));
        futureTariff.setTeValue(new BigDecimal("0.6000"));
        futureTariff.setCreatedAt(LocalDateTime.now());
        futureTariff.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(futureTariff);

        // When
        List<Tariff> future = tariffRepository.findByValidFromAfter(LocalDate.now());

        // Then
        assertThat(future).isNotEmpty();
        assertThat(future).allMatch(t -> t.getValidFrom().isAfter(LocalDate.now()));
    }

    @Test
    @DisplayName("Should count currently valid tariffs")
    void shouldCountCurrentlyValid() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);
        entityManager.persistAndFlush(expiredTariff);

        // When
        long count = tariffRepository.countCurrentlyValid();

        // Then
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    // ========================================================================
    // COMPLEX QUERIES TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find tariff by distributor and subgroup valid at date")
    void shouldFindByDistributorAndSubgroupValidAt() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(expiredTariff);
        LocalDate testDate = LocalDate.of(2024, 12, 15);

        // When
        List<Tariff> tariffs = tariffRepository.findByDistributorAndSubgroupValidAt(
            "CPFL PAULISTA", "B1", testDate
        );

        // Then
        assertThat(tariffs).isNotEmpty();
        assertThat(tariffs).allMatch(t ->
            t.getDistributor().equals("CPFL PAULISTA") &&
            t.getSubgroup().equals("B1")
        );
    }

    @Test
    @DisplayName("Should find tariff by distributor, subgroup and modality valid at date")
    void shouldFindByDistributorAndSubgroupAndModalityValidAt() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        LocalDate testDate = LocalDate.of(2024, 12, 15);

        // When
        Optional<Tariff> tariff = tariffRepository.findByDistributorAndSubgroupAndModalityValidAt(
            "CPFL PAULISTA", "B1", "Convencional", testDate
        );

        // Then
        assertThat(tariff).isPresent();
        assertThat(tariff.get().getDistributor()).isEqualTo("CPFL PAULISTA");
        assertThat(tariff.get().getSubgroup()).isEqualTo("B1");
        assertThat(tariff.get().getTariffModality()).isEqualTo("Convencional");
    }

    @Test
    @DisplayName("Should find most recent tariff by distributor and subgroup")
    void shouldFindMostRecentTariff() {
        // Given
        entityManager.persistAndFlush(expiredTariff);
        entityManager.persistAndFlush(testTariff1);

        // When
        Optional<Tariff> mostRecent = tariffRepository.findMostRecentByDistributorAndSubgroup(
            "CPFL PAULISTA", "B1"
        );

        // Then
        assertThat(mostRecent).isPresent();
        assertThat(mostRecent.get().getValidFrom()).isEqualTo(testTariff1.getValidFrom());
    }

    @Test
    @DisplayName("Should check if tariff exists for distributor and subgroup at date")
    void shouldCheckTariffExists() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        LocalDate testDate = LocalDate.of(2024, 12, 15);

        // When
        boolean exists = tariffRepository.existsByDistributorAndSubgroupValidAt(
            "CPFL PAULISTA", "B1", testDate
        );

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when tariff does not exist for date")
    void shouldReturnFalseWhenTariffDoesNotExist() {
        // Given
        entityManager.persistAndFlush(expiredTariff);
        LocalDate testDate = LocalDate.of(2024, 12, 15); // After expired tariff

        // When
        boolean exists = tariffRepository.existsByDistributorAndSubgroupValidAt(
            "CPFL PAULISTA", "B1", testDate
        );

        // Then
        assertThat(exists).isFalse();
    }

    // ========================================================================
    // TARIFF FLAG TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find tariffs by activated flag name")
    void shouldFindByActivatedFlagName() {
        // Given
        entityManager.persistAndFlush(testTariff1);
        entityManager.persistAndFlush(testTariff2);
        entityManager.persistAndFlush(testTariff3);

        // When
        List<Tariff> greenFlagTariffs = tariffRepository.findByActivatedFlagName("Verde");

        // Then
        assertThat(greenFlagTariffs).isNotEmpty();
        assertThat(greenFlagTariffs).allMatch(t -> t.getActivatedFlagName().equals("Verde"));
    }

    @Test
    @DisplayName("Should find tariffs with yellow flag")
    void shouldFindYellowFlagTariffs() {
        // Given
        entityManager.persistAndFlush(testTariff2);

        // When
        List<Tariff> yellowFlagTariffs = tariffRepository.findByActivatedFlagName("Amarela");

        // Then
        assertThat(yellowFlagTariffs).isNotEmpty();
        assertThat(yellowFlagTariffs.get(0).getFlagAdditionalValue())
            .isGreaterThan(BigDecimal.ZERO);
    }
}

