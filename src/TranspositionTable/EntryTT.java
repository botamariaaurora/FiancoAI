package TranspositionTable;

public class EntryTT {


        /** Data in the entry's first slot */
        public TableData data1;

        /** Data in the entry's second slot */
        public TableData data2;

        /**
         * Constructor that initializes data1 with the provided TableData instance.
         * data2 is initialized as null.
         * @param //data The TableData to store in the first slot.
         */
        public EntryTT() {
            this.data1 = null;
            this.data2 = null;  // Second slot starts as empty
        }
}
