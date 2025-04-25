package locser.controller.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import locser.toy.domain.model.enums.ValueEnum;

/**
 * A converter factory that creates converters for enums that implement
 * ValueEnum.
 * This allows Spring to convert string values to enum constants based on their
 * numeric value.
 */
@Component
public class ValueEnumConverterFactory implements ConverterFactory<String, ValueEnum> {

    @Override
    public <T extends ValueEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToValueEnumConverter<>(targetType);
    }

    /**
     * Generic converter for ValueEnum implementations.
     */
    private static class StringToValueEnumConverter<T extends ValueEnum> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToValueEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }

            // Try to parse as integer first
            try {
                int value = Integer.parseInt(source);
                return convertFromValue(value);
            } catch (NumberFormatException e) {
                // If not a number, try to match by name
                return convertFromName(source);
            }
        }

        /**
         * Convert from numeric value to enum constant.
         */
        private T convertFromValue(int value) {
            T[] enumConstants = enumType.getEnumConstants();
            for (T constant : enumConstants) {
                if (constant.getValue() == value) {
                    return constant;
                }
            }
            throw new IllegalArgumentException(
                    "No enum constant " + enumType.getCanonicalName() + " with value " + value);
        }

        /**
         * Convert from name to enum constant.
         */
        private T convertFromName(String name) {
            try {
                return (T) Enum.valueOf((Class<? extends Enum>) enumType, name.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "No enum constant " + enumType.getCanonicalName() + " with name " + name);
            }
        }
    }
}
