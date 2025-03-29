 package com.phasmidsoftware.dsaipg.util.benchmark;

 import java.io.PrintWriter;
 import java.util.List;

 public class CsvExporter {
     /**
      * Exports a list of BenchmarkResult objects to a CSV file.
      * @param results  The list of benchmark results
      * @param filename The name of the CSV file to create
      */
     public static void exportResults(List<BenchmarkResult> results, String filename) {
         try (PrintWriter writer = new PrintWriter(filename)) {
             // 1) Write the CSV header
             writer.println("Algorithm,ArraySize,Comparisons,Swaps,Copies,Hits,Time");

             // 2) Write each row
             for (BenchmarkResult br : results) {
                 writer.printf("%s,%d,%d,%d,%d,%d,%d%n",
                         br.getAlgorithm(),
                         br.getArraySize(),
                         br.getComparisons(),
                         br.getSwaps(),
                         br.getCopies(),
                         br.getHits(),
                         br.getTime());
             }

             System.out.println("Exported CSV to " + filename);
         }
         catch (Exception e) {
             e.printStackTrace();
         }
     }
 }