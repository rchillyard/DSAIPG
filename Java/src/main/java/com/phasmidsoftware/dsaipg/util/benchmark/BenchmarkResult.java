public class BenchmarkResult {
    private String algorithm;
    private int arraySize;
    private long comparisons;
    private long swaps;
    private long copies;
    private long hits;
    private long time; 

    public BenchmarkResult(String algorithm, int arraySize, long comparisons, long swaps, long copies, long hits, long time) {
        this.algorithm = algorithm;
        this.arraySize = arraySize;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.copies = copies;
        this.hits = hits;
        this.time = time;
    }
}
