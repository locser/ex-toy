package locser.application.util;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ToyMapper Utility Tests")
class ToyMapperTest {

    @Nested
    @DisplayName("Convert to Uppercase Tests")
    class ConvertToUppercaseTests {

        @Test
        @DisplayName("Should convert all toy names to uppercase")
        void shouldConvertAllToyNamesToUppercase() {
            // Given
            List<String> toyNames = Arrays.asList("teddy bear", "robot", "doll");

            // When
            List<String> result = ToyMapper.convertToUppercase(toyNames);

            // Then
            assertThat(result).containsExactly("TEDDY BEAR", "ROBOT", "DOLL");
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() {
            // Given
            List<String> toyNames = Collections.emptyList();

            // When
            List<String> result = ToyMapper.convertToUppercase(toyNames);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle null list")
        void shouldHandleNullList() {
            // When
            List<String> result = ToyMapper.convertToUppercase(null);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should handle list with null elements")
        void shouldHandleListWithNullElements() {
            // Given
            List<String> toyNames = Arrays.asList("teddy bear", null, "robot");

            // When
            List<String> result = ToyMapper.convertToUppercase(toyNames);

            // Then
            assertThat(result).containsExactly("TEDDY BEAR", null, "ROBOT");
        }
    }

    @Nested
    @DisplayName("Toy Name Validation Tests")
    class ToyNameValidationTests {

        @Test
        @DisplayName("Should return true for valid toy names")
        void shouldReturnTrueForValidToyNames() {
            assertThat(ToyMapper.isValidToyName("Teddy Bear")).isTrue();
            assertThat(ToyMapper.isValidToyName("Robot")).isTrue();
            assertThat(ToyMapper.isValidToyName("Action Figure")).isTrue();
            assertThat(ToyMapper.isValidToyName("123")).isTrue();
        }

        @Test
        @DisplayName("Should return false for invalid toy names")
        void shouldReturnFalseForInvalidToyNames() {
            assertThat(ToyMapper.isValidToyName(null)).isFalse();
            assertThat(ToyMapper.isValidToyName("")).isFalse();
            assertThat(ToyMapper.isValidToyName("   ")).isFalse();
            assertThat(ToyMapper.isValidToyName("\t\n")).isFalse();
        }
    }

    @Nested
    @DisplayName("Toy Code Generation Tests")
    class ToyCodeGenerationTests {

        @Test
        @DisplayName("Should generate toy code with correct format")
        void shouldGenerateToyCodeWithCorrectFormat() {
            // Given
            String toyName = "Teddy Bear";
            Long userId = 123L;

            // When
            String result = ToyMapper.generateToyCode(toyName, userId);

            // Then
            assertThat(result).startsWith("TED_123_");
            assertThat(result).hasSize(23); // TED_123_ + 14 digit timestamp
        }

        @Test
        @DisplayName("Should handle short toy names")
        void shouldHandleShortToyNames() {
            // Given
            String toyName = "Hi";
            Long userId = 456L;

            // When
            String result = ToyMapper.generateToyCode(toyName, userId);

            // Then
            assertThat(result).startsWith("HI_456_");
        }

        @Test
        @DisplayName("Should throw exception for invalid inputs")
        void shouldThrowExceptionForInvalidInputs() {
            assertThatThrownBy(() -> ToyMapper.generateToyCode(null, 123L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid toy name or user ID");

            assertThatThrownBy(() -> ToyMapper.generateToyCode("", 123L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid toy name or user ID");

            assertThatThrownBy(() -> ToyMapper.generateToyCode("Teddy", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid toy name or user ID");

            assertThatThrownBy(() -> ToyMapper.generateToyCode("Teddy", 0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid toy name or user ID");

            assertThatThrownBy(() -> ToyMapper.generateToyCode("Teddy", -1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid toy name or user ID");
        }
    }

    @Nested
    @DisplayName("Toy Age Calculation Tests")
    class ToyAgeCalculationTests {

        @Test
        @DisplayName("Should calculate correct age in days")
        void shouldCalculateCorrectAgeInDays() {
            // Given
            LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

            // When
            long ageInDays = ToyMapper.calculateToyAgeInDays(threeDaysAgo);

            // Then
            assertThat(ageInDays).isEqualTo(3L);
        }

        @Test
        @DisplayName("Should return 0 for toys created today")
        void shouldReturnZeroForToysCreatedToday() {
            // Given
            LocalDateTime today = LocalDateTime.now().minusHours(2);

            // When
            long ageInDays = ToyMapper.calculateToyAgeInDays(today);

            // Then
            assertThat(ageInDays).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should throw exception for null date")
        void shouldThrowExceptionForNullDate() {
            assertThatThrownBy(() -> ToyMapper.calculateToyAgeInDays(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Created date cannot be null");
        }

        @Test
        @DisplayName("Should throw exception for future date")
        void shouldThrowExceptionForFutureDate() {
            // Given
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

            // When & Then
            assertThatThrownBy(() -> ToyMapper.calculateToyAgeInDays(futureDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Created date cannot be in the future");
        }
    }

    @Nested
    @DisplayName("Description Formatting Tests")
    class DescriptionFormattingTests {

        @Test
        @DisplayName("Should return original description if within limit")
        void shouldReturnOriginalDescriptionIfWithinLimit() {
            // Given
            String description = "A beautiful toy";
            int maxLength = 20;

            // When
            String result = ToyMapper.formatDescription(description, maxLength);

            // Then
            assertThat(result).isEqualTo("A beautiful toy");
        }

        @Test
        @DisplayName("Should truncate and add ellipsis if exceeds limit")
        void shouldTruncateAndAddEllipsisIfExceedsLimit() {
            // Given
            String description = "This is a very long description that exceeds the limit";
            int maxLength = 20;

            // When
            String result = ToyMapper.formatDescription(description, maxLength);

            // Then
            assertThat(result).isEqualTo("This is a very l...");
            assertThat(result).hasSize(20);
        }

        @Test
        @DisplayName("Should handle null description")
        void shouldHandleNullDescription() {
            // When
            String result = ToyMapper.formatDescription(null, 10);

            // Then
            assertThat(result).isEqualTo("");
        }

        @Test
        @DisplayName("Should throw exception for invalid max length")
        void shouldThrowExceptionForInvalidMaxLength() {
            assertThatThrownBy(() -> ToyMapper.formatDescription("test", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Max length must be positive");

            assertThatThrownBy(() -> ToyMapper.formatDescription("test", -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Max length must be positive");
        }
    }

    @Nested
    @DisplayName("New Toy Detection Tests")
    class NewToyDetectionTests {

        @Test
        @DisplayName("Should return true for toys less than 7 days old")
        void shouldReturnTrueForToysLessThan7DaysOld() {
            // Given
            LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

            // When
            boolean result = ToyMapper.isNewToy(threeDaysAgo);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false for toys 7 days or older")
        void shouldReturnFalseForToys7DaysOrOlder() {
            // Given
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);

            // When & Then
            assertThat(ToyMapper.isNewToy(sevenDaysAgo)).isFalse();
            assertThat(ToyMapper.isNewToy(tenDaysAgo)).isFalse();
        }

        @Test
        @DisplayName("Should return false for null date")
        void shouldReturnFalseForNullDate() {
            // When
            boolean result = ToyMapper.isNewToy(null);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for future date")
        void shouldReturnFalseForFutureDate() {
            // Given
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

            // When
            boolean result = ToyMapper.isNewToy(futureDate);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Epoch Conversion Tests")
    class EpochConversionTests {

        @Test
        @DisplayName("Should convert LocalDateTime to epoch seconds")
        void shouldConvertLocalDateTimeToEpochSeconds() {
            // Given
            LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

            // When
            long epochSeconds = ToyMapper.toEpochSeconds(dateTime);

            // Then
            assertThat(epochSeconds).isPositive();
        }

        @Test
        @DisplayName("Should throw exception for null datetime")
        void shouldThrowExceptionForNullDatetime() {
            assertThatThrownBy(() -> ToyMapper.toEpochSeconds(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("DateTime cannot be null");
        }
    }

    @Nested
    @DisplayName("Toy Name Sanitization Tests")
    class ToyNameSanitizationTests {

        @Test
        @DisplayName("Should remove special characters")
        void shouldRemoveSpecialCharacters() {
            // Given
            String toyName = "Teddy@Bear#123!";

            // When
            String result = ToyMapper.sanitizeToyName(toyName);

            // Then
            assertThat(result).isEqualTo("TeddyBear123");
        }

        @Test
        @DisplayName("Should preserve allowed characters")
        void shouldPreserveAllowedCharacters() {
            // Given
            String toyName = "Teddy-Bear's Toy 123";

            // When
            String result = ToyMapper.sanitizeToyName(toyName);

            // Then
            assertThat(result).isEqualTo("Teddy-Bear's Toy 123");
        }

        @Test
        @DisplayName("Should normalize multiple spaces")
        void shouldNormalizeMultipleSpaces() {
            // Given
            String toyName = "Teddy    Bear   Toy";

            // When
            String result = ToyMapper.sanitizeToyName(toyName);

            // Then
            assertThat(result).isEqualTo("Teddy Bear Toy");
        }

        @Test
        @DisplayName("Should handle null input")
        void shouldHandleNullInput() {
            // When
            String result = ToyMapper.sanitizeToyName(null);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for empty result")
        void shouldReturnNullForEmptyResult() {
            // Given
            String toyName = "@#$%^&*()";

            // When
            String result = ToyMapper.sanitizeToyName(toyName);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should trim whitespace")
        void shouldTrimWhitespace() {
            // Given
            String toyName = "  Teddy Bear  ";

            // When
            String result = ToyMapper.sanitizeToyName(toyName);

            // Then
            assertThat(result).isEqualTo("Teddy Bear");
        }
    }
}