package expressionevaluator;

/**
 *
 * @author Akshaya Damodaran G01129364
 */
public class SymbolTable<T> {

    static class TableEntry<K, V> {

        private K key;
        private V value;

        public TableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

    }

    private TableEntry[] hashtable;
    private int numOfEntries;
    private int arraySize;
    private static final int DEFAULT_ARRAY_SIZE = 2;

    public SymbolTable() {
        this(DEFAULT_ARRAY_SIZE);
    }

    /**
     * Constructor for dynamic array of increased size
     *
     * @param arraySize
     */
    public SymbolTable(int arraySize) {
        this.arraySize = arraySize;
        hashtable = (TableEntry[]) new TableEntry[arraySize];
        TableEntry initTableEntry = new TableEntry("", "");
        for (int i = 0; i < this.arraySize; i++) {
            hashtable[i] = initTableEntry;
        }

    }

    /**
     * Creates a TableEntry object having the key with its value and inserts it
     * into the Symbol Table after calculating index by hashing the key. This
     * method uses linear probing if a location is in use. If the key already
     * exists in the table, the method replaces its value with v. If the key
     * isn't in the table and the table is >= 80% full, the hash table is
     * expanded to twice the size and rehashed.
     *
     * @param k
     * @param v
     */
    public void put(String k, T v) {

        TableEntry newEntry = new TableEntry(k, v);
        int hashCode = k.hashCode() & 0x7fffffff;;
        int index = (hashCode % arraySize);
        if (null == this.get(k) && numOfEntries >= 0.8 * arraySize) {
            rehash(arraySize);
            index = hashCode % arraySize;
            while (hashtable[index].key.toString() != "") {
                index++;
                index = index % arraySize;
            }
            hashtable[index] = newEntry;
            numOfEntries++;
        } else if (null != this.get(k)) {
            TableEntry updatedEntry = new TableEntry(k, v);
            hashtable[index] = updatedEntry;
        } else {
            hashtable[index] = newEntry;
            numOfEntries++;
        }
    }

    /**
     * Removes the given key (and associated value) from the table and returns
     * the value removed or null if value is not found.
     *
     * @param k
     * @return value of the key if found, otherwise null
     */
    public T remove(String k) {

        TableEntry checkEntry = (TableEntry) this.get(k);
        if (checkEntry == null) {
            return null;
        } else {
            int hashCode = k.hashCode() & 0x7fffffff;
            int index = hashCode % arraySize;
            hashtable[index] = new TableEntry("#", "#");
            return (T) checkEntry.value;
        }

    }

    /**
     * Given a key, returns the value from the table
     *
     * @param k
     * @return returns null if key not found.
     */
    public T get(String k) {
        int hashCode = k.hashCode() & 0x7fffffff;;
        int index = hashCode % arraySize;
        while (hashtable[index].key.toString() != null) {
            if (hashtable[index].key.toString().equals(k)) {
                return (T) hashtable[index].value;
            }
            if (index == arraySize - 1) {
                break;
            }
            index = (index + 1) % arraySize;

        }

        return null;
    }

    /**
     *
     * @return returns the capacity of the symbol table
     */
    public int getCapacity() {
        return arraySize;
    }

    /**
     *
     * @return returns the number of entries in the symbol table
     */
    public int size() {
        return numOfEntries;
    }

    public void printEntries() {

        if (!(hashtable[0] == null && hashtable[1] == null && arraySize == 2)) {
            for (int i = 0; i < arraySize; i++) {
                if (hashtable[i] != null) {
                    System.out.print(hashtable[i].key.toString() + "=" + hashtable[i].value.toString() + " ");
                }
            }
        }
        System.out.println("\n");

        this.clear();
    }

    /**
     * Clears the symbol table
     */
    public void clear() {

        TableEntry[] newTable = new TableEntry[2];
        hashtable = newTable;
        numOfEntries = 0;
        arraySize = 2;
        TableEntry initTableEntry = new TableEntry("", "");
        for (int i = 0; i < this.arraySize; i++) {
            hashtable[i] = initTableEntry;
        }
        System.out.println("\n");
    }

    /**
     * Doubles the size of the array and rehashes the keys of the symbol table
     * entries and places the TableEntry objects in their new locations.
     *
     * @param size
     * @return
     */
    public boolean rehash(int size) {

        TableEntry[] tempArray = (TableEntry[]) new TableEntry[2 * arraySize];
        TableEntry initTableEntry = new TableEntry("", "");
        for (int i = 0; i < 2 * arraySize; i++) {
            tempArray[i] = initTableEntry;
        }
        int newHashCode;
        int newIndex;
        for (int i = 0; i < arraySize; i++) {

            newHashCode = (hashtable[i].key.toString().hashCode()) & 0x7fffffff;
            newIndex = newHashCode % (2 * arraySize);
            tempArray[newIndex] = hashtable[i];
        }

        hashtable = tempArray;
        System.out.println("**Doubling array size");
        arraySize = 2 * arraySize;
        return true;

    }
}
