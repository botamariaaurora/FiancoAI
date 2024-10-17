package TranspositionTable;

public class TTBounds {

    /** An invalid value stored in Transposition Table */
    public static final byte INVALID_VALUE = (byte) 0x0;

    /** An exact (maybe heuristic) value stored in Transposition Table */
    public static final byte EXACT_VALUE = (byte) 0x1;

    /** A lower bound stored in Transposition Table */
    public static final byte LOWER_BOUND = (byte) (0x1 << 1);

    /** An upper bound stored in Transposition Table */
    public static final byte UPPER_BOUND = (byte) (0x1 << 2);
}
