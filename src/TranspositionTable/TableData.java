package TranspositionTable;

import other.move.Move;

public class TableData {
    public Move bestMove;
    public long fullHash;
    public float value;
    public int depth;
    public byte boundType;

    // Constructor
    public TableData(Move bestMove, long fullHash, float value, int depth, byte boundType) {
        this.bestMove = bestMove;
        this.fullHash = fullHash;
        this.value = value;
        this.depth = depth;
        this.boundType = boundType;
    }
}