package locser.application.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for mapping and transforming toy-related data.
 * This class demonstrates business logic that can be easily unit tested.
 */
public class ToyMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Converts a list of toy names to uppercase.
     * 
     * @param toyNames List of toy names
     * @return List of uppercase toy names
     */
    public static List<String> convertToUppercase(List<String> toyNames) {
        if (toyNames == null) {
            return null;
        }
        return toyNames.stream()
                .map(name -> name != null ? name.toUpperCase() : null)
                .collect(Collectors.toList());
    }

    /**
     * Validates if a toy name is valid.
     * A valid toy name should not be null, empty, or contain only whitespace.
     * 
     * @param toyName The toy name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidToyName(String toyName) {
        return toyName != null && !toyName.trim().isEmpty();
    }

    /**
     * Generates a toy code based on name and user ID.
     * Format: FIRST_3_CHARS_OF_NAME + "_" + USER_ID + "_" + TIMESTAMP
     * 
     * @param toyName The name of the toy
     * @param userId The user ID
     * @return Generated toy code
     */
    public static String generateToyCode(String toyName, Long userId) {
        if (!isValidToyName(toyName) || userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid toy name or user ID");
        }

        String namePrefix = toyName.trim().toUpperCase().substring(0, Math.min(3, toyName.trim().length()));
        String timestamp = LocalDateTime.now().format(FORMATTER).replaceAll("[\\s:-]", "");
        
        return String.format("%s_%d_%s", namePrefix, userId, timestamp.substring(0, 14));
    }

    /**
     * Calculates the age of a toy in days.
     * 
     * @param createdDate The creation date of the toy
     * @return Age in days
     */
    public static long calculateToyAgeInDays(LocalDateTime createdDate) {
        if (createdDate == null) {
            throw new IllegalArgumentException("Created date cannot be null");
        }

        LocalDateTime now = LocalDateTime.now();
        if (createdDate.isAfter(now)) {
            throw new IllegalArgumentException("Created date cannot be in the future");
        }

        return java.time.Duration.between(createdDate, now).toDays();
    }

    /**
     * Formats a toy description with a maximum length.
     * If the description exceeds the limit, it will be truncated and "..." will be added.
     * 
     * @param description The original description
     * @param maxLength Maximum allowed length
     * @return Formatted description
     */
    public static String formatDescription(String description, int maxLength) {
        if (maxLength <= 0) {
            throw new IllegalArgumentException("Max length must be positive");
        }

        if (description == null) {
            return "";
        }

        if (description.length() <= maxLength) {
            return description;
        }

        if (maxLength <= 3) {
            return description.substring(0, maxLength);
        }
        return description.substring(0, maxLength - 3) + "...";
    }

    /**
     * Determines if a toy is considered "new" based on its age.
     * A toy is considered new if it's less than 7 days old.
     * 
     * @param createdDate The creation date of the toy
     * @return true if the toy is new, false otherwise
     */
    public static boolean isNewToy(LocalDateTime createdDate) {
        if (createdDate == null) {
            return false;
        }

        try {
            long ageInDays = calculateToyAgeInDays(createdDate);
            return ageInDays < 7;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Converts LocalDateTime to epoch timestamp.
     * 
     * @param dateTime The LocalDateTime to convert
     * @return Epoch timestamp in seconds
     */
    public static long toEpochSeconds(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Sanitizes toy name by removing special characters and extra spaces.
     * 
     * @param toyName The toy name to sanitize
     * @return Sanitized toy name
     */
    public static String sanitizeToyName(String toyName) {
        if (toyName == null) {
            return null;
        }

        // Remove special characters except spaces, hyphens, and apostrophes
        String sanitized = toyName.replaceAll("[^a-zA-Z0-9\\s\\-']", "");
        
        // Replace multiple spaces with single space and trim
        sanitized = sanitized.replaceAll("\\s+", " ").trim();
        
        return sanitized.isEmpty() ? null : sanitized;
    }
}