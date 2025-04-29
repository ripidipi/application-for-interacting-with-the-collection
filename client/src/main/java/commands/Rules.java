package commands;

import java.util.Comparator;

/**
 * Defines the set of available rules, each associated with a unique integer value.
 * <p>
 * This enum can be used whenever you need to work with rule priorities or ordering by value.
 * </p>
 */
public enum Rules {

    /**
     * Rule U – typically represents the “User” rule.
     * <p>
     * Value: 1
     * </p>
     */
    U(1),

    /**
     * Rule S – typically represents the “System” rule.
     * <p>
     * Value: 2
     * </p>
     */
    S(2);

    private final int value;

    /**
     * Constructs a {@code Rules} constant with its associated integer value.
     *
     * @param value the integer code representing this rule
     */
    Rules(int value) {
        this.value = value;
    }

    /**
     * Gets the integer code associated with this rule.
     *
     * @return the integer value of this rule
     */
    public int getValue() {
        return value;
    }

    /**
     * A comparator that orders {@code Rules} constants by their integer values in ascending order.
     * <p>
     * Example usage:
     * <pre>
     *     List&lt;Rules&gt; list = Arrays.asList(Rules.S, Rules.U);
     *     list.sort(new RulesComparator());
     *     // Now list order is [U, S]
     * </pre>
     * </p>
     */
    public static class RulesComparator implements Comparator<Rules> {
        /**
         * Compares two {@code Rules} by their integer values.
         *
         * @param r1 the first rule to compare
         * @param r2 the second rule to compare
         * @return a negative integer, zero, or a positive integer as
         *         the first argument is less than, equal to, or greater than the second
         */
        @Override
        public int compare(Rules r1, Rules r2) {
            return Integer.compare(r1.getValue(), r2.getValue());
        }
    }
}
