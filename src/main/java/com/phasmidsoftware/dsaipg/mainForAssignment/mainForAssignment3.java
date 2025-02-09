package com.phasmidsoftware.dsaipg.mainForAssignment;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparator;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.Arrays;

public class mainForAssignment3 {
    public static Integer[] randomArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = (int) (Math.random() * 4000);
        }
        return array;
    }

    public static Integer[] orderedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    public static Integer[] pOrderedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size / 2; i++) {
            array[i] = (int) (Math.random() * 2000);
        }
        for (int i = size / 2; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    public static Integer[] reverseArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        return array;
    }

    public static void main(String[] args) {
        Integer[] random = randomArray(4000);
        Integer[] ordered = orderedArray(32000);
        Integer[] pOrdered = pOrderedArray(4000);
        Integer[] reverse = reverseArray(4000);

        Benchmark_Timer btr1 = new Benchmark_Timer("InsertionSortRandom", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(random, 0, 250));
        });
        double tr1 = btr1.run(false, 800);
        Benchmark_Timer btr2 = new Benchmark_Timer("InsertionSortRandom", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(random, 0, 500));
        });
        double tr2 = btr2.run(false, 400);
        Benchmark_Timer btr3 = new Benchmark_Timer("InsertionSortRandom", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(random, 0, 1000));
        });
        double tr3 = btr3.run(false, 300);
        Benchmark_Timer btr4 = new Benchmark_Timer("InsertionSortRandom", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(random, 0, 2000));
        });
        double tr4 = btr4.run(false, 200);
        Benchmark_Timer btr5 = new Benchmark_Timer("InsertionSortRandom", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(random, 0, 4000));
        });
        double tr5 = btr5.run(false, 150);
        System.out.printf("\nrandom: %.5fms, %.5fms, %.5fms, %.5fms, %.5fms\n\n", tr1, tr2, tr3, tr4, tr5);

        Benchmark_Timer bto = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 250));
        });
        double to = bto.run(false, 800);
        Benchmark_Timer bto2 = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 500));
        });
        double to2 = bto2.run(false, 400);
        Benchmark_Timer bto3 = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 1000));
        });
        double to3 = bto3.run(false, 300);
        Benchmark_Timer bto4 = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 2000));
        });
        double to4 = bto4.run(false, 200);
        Benchmark_Timer bto5 = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 4000));
        });
        double to5 = bto5.run(false, 150);
        Benchmark_Timer bto6 = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 8000));
        });
        double to6 = bto6.run(false, 150);
        Benchmark_Timer bto7 = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 16000));
        });
        double to7 = bto7.run(false, 150);
        Benchmark_Timer bto8 = new Benchmark_Timer("InsertionSortOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(ordered, 0, 32000));
        });
        double to8 = bto8.run(false, 150);
        System.out.printf("\nordered: %.5fms, %.5fms, %.5fms, %.5fms, %.5fms, %.5fms, %.5fms, %.5fms\n\n", to, to2, to3, to4, to5, to6, to7, to8);

        Benchmark_Timer btp = new Benchmark_Timer("InsertionSortPartialOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(pOrdered, 1875, 2125));
        });
        double tp = btp.run(false, 800);
        Benchmark_Timer btp2 = new Benchmark_Timer("InsertionSortPartialOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(pOrdered, 1750, 2250));
        });
        double tp2 = btp2.run(false, 400);
        Benchmark_Timer btp3 = new Benchmark_Timer("InsertionSortPartialOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(pOrdered, 1500, 2500));
        });
        double tp3 = btp3.run(false, 300);
        Benchmark_Timer btp4 = new Benchmark_Timer("InsertionSortPartialOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(pOrdered, 1000, 3000));
        });
        double tp4 = btp4.run(false, 200);
        Benchmark_Timer btp5 = new Benchmark_Timer("InsertionSortPartialOrdered", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(pOrdered, 0, 4000));
        });
        double tp5 = btp5.run(false, 150);
        System.out.printf("\npartially ordered: %.5fms, %.5fms, %.5fms, %.5fms, %.5fms\n\n", tp, tp2, tp3, tp4, tp5);


        Benchmark_Timer btrv = new Benchmark_Timer("InsertionSortReversed", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(reverse, 0, 250));
        });
        double trv = btrv.run(false, 800);
        Benchmark_Timer btrv2 = new Benchmark_Timer("InsertionSortReversed", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(reverse, 0, 500));
        });
        double trv2 = btrv2.run(false, 400);
        Benchmark_Timer btrv3 = new Benchmark_Timer("InsertionSortReversed", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(reverse, 0, 1000));
        });
        double trv3 = btrv3.run(false, 300);
        Benchmark_Timer btrv4 = new Benchmark_Timer("InsertionSortReversed", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(reverse, 0, 2000));
        });
        double trv4 = btrv4.run(false, 200);
        Benchmark_Timer btrv5 = new Benchmark_Timer("InsertionSortReversed", b-> {
            InsertionSortComparator.sort(Arrays.copyOfRange(reverse, 0, 4000));
        });
        double trv5 = btrv5.run(false, 150);
        System.out.printf("\nreversed: %.5fms, %.5fms, %.5fms, %.5fms, %.5fms\n\n", trv, trv2, trv3, trv4, trv5);
    }
}
