package commands;

import java.util.Comparator;

public enum Rules {

    U(1),
    S(2);

    private final int value;

    Rules(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static class RulesComparator implements Comparator<Rules> {
        @Override
        public int compare(Rules r1, Rules r2) {
            return Integer.compare(r1.getValue(), r2.getValue());
        }
    }
}
