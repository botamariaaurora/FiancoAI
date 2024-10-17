package TranspositionTable;

import other.move.Move;

public class TranspositionTable {

    /** Number of bits to use as the primary code */
    private final int numBitsPrimaryCode;

    /** Max number of entries in the table */
    private final int maxNumEntries;

    /** The transposition table */
    private EntryTT[] table;

    /**
     * Constructor.
     * @param numBitsPrimaryCode Number of bits from the hash to use as the index.
     */
    public TranspositionTable(int numBitsPrimaryCode) {
        this.numBitsPrimaryCode = numBitsPrimaryCode;
        this.maxNumEntries = 1 << numBitsPrimaryCode;  // 2^numBitsPrimaryCode
        this.table = null;
    }

    /** Allocates space for the transposition table */
    public void allocate() {
        this.table = new EntryTT[maxNumEntries];
    }

    /** Clears the memory for the table */
    public void deallocate() {
        this.table = null;
    }

    /**
     * Retrieves stored TableData for a given fullHash.
     * @param fullHash The 64-bit hash value used as a key.
     * @return The stored TableData, or null if not found.
     */
    public TableData retrieve(long fullHash) {
        int index = getIndex((int)fullHash  >>> (Long.SIZE - numBitsPrimaryCode));
        EntryTT entry = table[index];

        if (entry == null) {
            return null; // No entry stored at this index
        }

        // Check both slots (data1 and data2) for the fullHash
        if (entry.data1 != null && entry.data1.fullHash == fullHash) {
            return entry.data1;
        }
        if (entry.data2 != null && entry.data2.fullHash == fullHash) {
            return entry.data2;
        }

        return null; // Not found
    }

    public void store(Move bestMove, long fullHash, float value, int depth, byte boundType) {
        int index = getIndex((int) fullHash >>> (Long.SIZE - numBitsPrimaryCode));
        EntryTT entry = table[index];

        // Create new TableData
        TableData newData = new TableData(bestMove, fullHash, value, depth, boundType);

        // If no entry exists at this index, create a new one
        if (entry == null) {
            entry = new EntryTT();
            entry.data1 = newData;
            table[index] = entry;
            return;
        }

        // Check if we can overwrite an existing slot or store in an empty slot
        if (entry.data1 == null || entry.data1.fullHash == fullHash) {
            entry.data1 = newData;
        } else if (entry.data2 == null || entry.data2.fullHash == fullHash) {
            entry.data2 = newData;
        } else {
            // Replace the entry with the shallower depth
            if (entry.data1.depth < entry.data2.depth) {
                entry.data1 = newData;
            } else {
                entry.data2 = newData;
            }
        }
    }

    /**
     * Utility method to get the index from the fullHash.
     * @param fullHash The full 64-bit hash.
     * @return The index in the transposition table.
     */
    private int getIndex(long fullHash) {
        return (int) (fullHash >>> (Long.SIZE - numBitsPrimaryCode));
    }
}
