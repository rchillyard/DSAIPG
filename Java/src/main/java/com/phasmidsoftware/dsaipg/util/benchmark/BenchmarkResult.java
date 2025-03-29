 package com.phasmidsoftware.dsaipg.util.benchmark;

 public class BenchmarkResult {
     private final String algorithm;
     private final int arraySize;
     private final long comparisons;
     private final long swaps;
     private final long copies;
     private final long hits;
     private final long time; // typically in milliseconds

     public BenchmarkResult(String algorithm, int arraySize,
                            long comparisons, long swaps, long copies, long hits,
                            long time) {
         this.algorithm = algorithm;
         this.arraySize = arraySize;
         this.comparisons = comparisons;
         this.swaps = swaps;
         this.copies = copies;
         this.hits = hits;
         this.time = time;
     }

     public String getAlgorithm() { return algorithm; }
     public int getArraySize() { return arraySize; }
     public long getComparisons() { return comparisons; }
     public long getSwaps() { return swaps; }
     public long getCopies() { return copies; }
     public long getHits() { return hits; }
     public long getTime() { return time; }
 }