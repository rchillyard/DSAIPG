package com.phasmidsoftware.dsaipg.select;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;
import org.junit.Test;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("ALL")
public class SlowSelectTest {
    @Test
    public void testSelectWithIntegers() throws Exception {
        Integer[] a = {34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89};
        final Config config = setupConfig("false", "false", "0", "0", "", "");
        Integer[] b = a.clone();
        InsertionSort<Integer> sorter = new InsertionSort<Integer>(b.length, config);
        sorter.sort(b, 0, b.length);
        int k = 1;
        while (k <= a.length) {
            SlowSelect<Integer> ss = new SlowSelect<>(k);
            Integer result = ss.select(a, ss.k);
            assertEquals(b[k - 1], result);
            k++;
        }
    }

    @Test
    public void testSelectWithCharacters() throws Exception {
        Character[] a = {'a', 'Z', 'e', 'R', '2', 'w', 'B', '9', 'z', '0', 'A', 'r', 'b', '3', 'E', 'W', '$', '%', '9', 'Z', 'e', '!', '#', '2', 'b'};
        final Config config = setupConfig("false", "false", "0", "0", "", "");
        Character[] b = a.clone();
        InsertionSort<Character> sorter = new InsertionSort<Character>(b.length, config);
        sorter.sort(b, 0, b.length);
        int k = 1;
        while (k <= a.length) {
            SlowSelect<Character> ss = new SlowSelect<>(k);
            Character result = ss.select(a, ss.k);
            assertEquals(b[k - 1], result);
            k++;
        }
    }

    @Test
    public void testSelectWithStrings() throws Exception {
        String[] a = {"Texas", "new Mexico", "Florida", "alabama", "Oregon",
                "Michigan", "utah", "New York", "california", "georgia",
                "Idaho", "south Dakota", "Louisiana", "ohio", "massachusetts",
                "Colorado", "nevada", "Wyoming", "North Dakota", "maine",
                "kentucky", "New Jersey", "missouri", "Alaska", "virginia",
                "Minnesota", "hawaii", "Arkansas", "indiana", "Washington",
                "Pennsylvania", "illinois", "west Virginia", "South Carolina", "arizona",
                "Iowa", "rhode Island", "new Hampshire", "Tennessee", "Maryland",
                "connecticut", "Montana", "Wisconsin", "delaware", "north Carolina",
                "vermont", "Kansas", "mississippi", "Oklahoma", "nebraska"};
        final Config config = setupConfig("false", "false", "0", "0", "", "");
        String[] b = a.clone();
        InsertionSort<String> sorter = new InsertionSort<String>(b.length, config);
        sorter.sort(b, 0, b.length);
        int k = 1;
        while (k <= a.length) {
            SlowSelect<String> ss = new SlowSelect<>(k);
            String result = ss.select(a, ss.k);
            assertEquals(b[k - 1], result);
            k++;
        }
    }

    @Test
    public void testSelectFirstElement() throws Exception {
        Integer[] a = {34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89};
        int k = 1;
        SlowSelect<Integer> ss = new SlowSelect<>(k);
        Integer result = ss.select(a, ss.k);
        assertEquals(-100, result.longValue());
    }

    @Test
    public void testSelectLastElement() throws Exception {
        Integer[] a = {34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89};
        int k = a.length;
        SlowSelect<Integer> ss = new SlowSelect<>(k);
        Integer result = ss.select(a, ss.k);
        assertEquals(89, result.longValue());
    }

}