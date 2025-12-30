package com.understand_your_electricity_bill.dto.validation;

import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Utility class for common DTO validations.
 */
public final class DtoValidationUtils {

    private DtoValidationUtils() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Validates that validUntil date is not before validFrom date.
     *
     * @param validFrom Start date
     * @param validUntil End date
     * @throws IllegalArgumentException if validUntil is before validFrom
     */
    public static void validateDateRange(LocalDate validFrom, LocalDate validUntil) {
        if (validFrom != null && validUntil != null && validUntil.isBefore(validFrom)) {
            throw new IllegalArgumentException("Valid until date must be after or equal to valid from date");
        }
    }

    /**
     * Trims a string if not null, otherwise returns null.
     *
     * @param value String to trim
     * @return Trimmed string or null
     */
    public static String trimIfNotNull(String value) {
        return value != null ? value.trim() : null;
    }

    /**
     * Removes all non-digit characters from a string.
     * Used for normalizing CPF, CNPJ, phone numbers, etc.
     *
     * @param value String to clean
     * @return String with only digits, or null if input is null
     */
    public static String keepOnlyDigits(String value) {
        return value != null ? value.replaceAll("[^0-9]", "") : null;
    }

    /**
     * Checks if a Record-based UpdateDTO has at least one field to update.
     * Excludes the 'id' field from the check since it's required for identification.
     *
     * @param updateDto The UpdateDTO record instance
     * @return true if at least one non-id field is not null, false otherwise
     * @throws IllegalArgumentException if the object is not a record
     */
    public static boolean hasUpdates(Record updateDto) {
        if (updateDto == null) {
            return false;
        }

        Class<?> recordClass = updateDto.getClass();
        if (!recordClass.isRecord()) {
            throw new IllegalArgumentException("Object must be a Record type");
        }

        // Get all record components (fields)
        RecordComponent[] components = recordClass.getRecordComponents();

        // Check if any field (except 'id') is not null
        return Arrays.stream(components)
                .filter(component -> !"id".equals(component.getName())) // Exclude ID field
                .anyMatch(component -> {
                    try {
                        Object value = component.getAccessor().invoke(updateDto);
                        return value != null;
                    } catch (Exception e) {
                        return false;
                    }
                });
    }
}
