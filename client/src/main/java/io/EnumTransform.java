package io;

import exceptions.IncorrectConstant;

import static io.EnumInput.inputAssistent;

public class EnumTransform {

    /**
     * Converts a string input to the corresponding enum constant.
     *
     * @param enumType The enum class type to convert to.
     * @param input The string input to convert to an enum.
     * @param <T> The type of the enum.
     * @return The corresponding enum constant if the input is valid.
     * @throws IncorrectConstant if the input is not a valid value from the enum.
     */
    public static <T extends Enum<T>> T TransformToEnum(Class<T> enumType, String input) {
        try {
            if (input == null || input.trim().isEmpty()) {
                throw new IllegalArgumentException();
            }
            return Enum.valueOf(enumType, input.toUpperCase());
        } catch (IllegalArgumentException e) {
            DistributionOfTheOutputStream.println(new IncorrectConstant(enumType.getSimpleName()).getMessage());
        }
        return inputAssistent(enumType);
    }

}
