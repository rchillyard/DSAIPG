package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

// 假设你已有 PriorityQueue & FourAryHeap 实现:
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.adt.pq.FourAryHeap;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

public class HeapBenchmark {

    // 堆大小上限
    public static final int M = 4095;
    // 插入数量
    public static final int NUM_INSERTIONS = 8000;
    // 额外删除次数
    public static final int NUM_REMOVALS = 4000;
    // 测试重复次数
    public static final int REPEATS = 1;

    /**
     * 生成测试数据: 固定随机数种子, 以保证可重复
     */
    public static List<Integer> generateTestData() {
        List<Integer> data = new ArrayList<>(NUM_INSERTIONS);
        Random rand = new Random();
        for (int i = 0; i < NUM_INSERTIONS; i++) {
            data.add(rand.nextInt());
        }
        return data;
    }
    

    /**
     * 仅改测试方法, 不动 PriorityQueue.give():
     * - 当堆大小>=M时, 先手动 take() 一个元素(溢出)并记录
     * - 然后再 give() 新元素
     * - 插入完成后再删 NUM_REMOVALS 个元素
     */
    public static void runTest(PriorityQueue<Integer> heap, List<Integer> testData) {
        Integer highestSpilled = null; // 记录溢出元素中最大的值(针对最大堆)
        int overflowCount = 0;         // 统计溢出发生的次数

        // 插入过程: 随时保持 size <= M
        for (Integer elem : testData) {
            // 如果堆已满, 先溢出删除一个
            if (heap.size() >= M) {
                try {
                    Integer spilled = heap.take();
                    overflowCount++;
                    // 记录最大溢出值
                    if (highestSpilled == null || spilled.compareTo(highestSpilled) > 0) {
                        highestSpilled = spilled;
                    }
                } catch (PQException e) {
                    e.printStackTrace();
                }
            }
            // 现在堆肯定有空位, 安全调用 give
            heap.give(elem);
        }

        // 再额外删除 4000 个元素
        for (int i = 0; i < NUM_REMOVALS; i++) {
            try {
                heap.take();
            } catch (PQException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Overflow count: " + overflowCount);
        System.out.println("The highestSpilled number: " + highestSpilled);
    }

    /**
     * 对 FourAryHeap 的测试, 同理
     */
    public static void runTest(FourAryHeap<Integer> heap, List<Integer> testData) {
        Integer highestSpilled = null;
        int overflowCount = 0;

        for (Integer elem : testData) {
            if (heap.size() >= M) {
                try {
                    Integer spilled = heap.take();
                    overflowCount++;
                    if (highestSpilled == null || spilled.compareTo(highestSpilled) > 0) {
                        highestSpilled = spilled;
                    }
                } catch (PQException e) {
                    e.printStackTrace();
                }
            }
            heap.give(elem);
        }

        for (int i = 0; i < NUM_REMOVALS; i++) {
            try {
                heap.take();
            } catch (PQException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Overflow count: " + overflowCount);
        System.out.println("The highestSpilled number: " + highestSpilled);
    }

    public static void main(String[] args) {
        // 生成测试数据
        List<Integer> testData = generateTestData();

        // 1. 基本二叉堆(不使用 Floyd's trick)
        Benchmark_Timer<Void> timerBasic = new Benchmark_Timer<>(
            "Basic Binary Heap Test",
            null,
            v -> {
                // 每次测试前构造新的堆, 并使用 runTest 进行插入(含手动溢出)
                PriorityQueue<Integer> heap = new PriorityQueue<Integer>(M, true, Comparator.naturalOrder(), false);
                runTest(heap, testData);
            },
            null
        );
        double timeBasic = timerBasic.run(null, REPEATS);
        System.out.println("[Basic Binary Heap] Average time over " + REPEATS + " runs: " + timeBasic + " ms");

        // 2. 二叉堆(使用 Floyd's trick)
        Benchmark_Timer<Void> timerFloyd = new Benchmark_Timer<>(
            "Binary Heap (Floyd's trick)",
            null,
            v -> {
                PriorityQueue<Integer> heap = new PriorityQueue<Integer>(M, true, Comparator.naturalOrder(), true);
                runTest(heap, testData);
            },
            null
        );
        double timeFloyd = timerFloyd.run(null, REPEATS);
        System.out.println("[Binary Heap w/ Floyd] Average time: " + timeFloyd + " ms");

        // 3. FourAryHeap (不使用 Floyd's trick)
        Benchmark_Timer<Void> timer3 = new Benchmark_Timer<>(
            "FourAryHeap",
            null,
            v -> {
                FourAryHeap<Integer> heap3 = new FourAryHeap<Integer>(M, true, Comparator.naturalOrder(), false);
                runTest(heap3, testData);
            },
            null
        );
        double time3 = timer3.run(null, REPEATS);
        System.out.println("[FourAryHeap] Average time: " + time3 + " ms");

        // 4. FourAryHeap (使用 Floyd's trick)
        Benchmark_Timer<Void> timer4 = new Benchmark_Timer<>(
            "FourAryHeap (Floyd)",
            null,
            v -> {
                FourAryHeap<Integer> heap4 = new FourAryHeap<Integer>(M, true, Comparator.naturalOrder(), true);
                runTest(heap4, testData);
            },
            null
        );
        double time4 = timer4.run(null, REPEATS);
        System.out.println("[FourAryHeap w/ Floyd] Average time: " + time4 + " ms");
    }
}