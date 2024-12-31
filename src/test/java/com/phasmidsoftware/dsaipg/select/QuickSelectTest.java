package com.phasmidsoftware.dsaipg.select;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.Partition;
import com.phasmidsoftware.dsaipg.util.Config;
import org.junit.Test;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("ALL")
public class QuickSelectTest {

    @Test
    public void testSelectWithIntegers() throws Exception {
        Integer[] a = {34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89};
        final Config config = setupConfig("false", "false", "0", "0", "", "");
        Integer[] b = a.clone();
        InsertionSort<Integer> sorter = new InsertionSort<Integer>(b.length, config);
        sorter.sort(b, 0, b.length);
        int k = 0;
        while (k < a.length) {
            QuickSelect<Integer> qs = new QuickSelect<>();
            Integer result = qs.select(a, k);
            assertEquals(b[k], result);
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
        int k = 0;
        while (k < a.length) {
            QuickSelect<Character> qs = new QuickSelect<>();
            Character result = qs.select(a, k);
            assertEquals(b[k], result);
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
        int k = 0;
        while (k < a.length) {
            QuickSelect<String> qs = new QuickSelect<>();
            String result = qs.select(a, k);
            assertEquals(b[k], result);
            k++;
        }
    }

    @Test
    public void testSelectFirstElement() throws Exception {
        Integer[] a = {34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89};
        int k = 0;
        QuickSelect<Integer> qs = new QuickSelect<Integer>();
        Integer result = qs.select(a, k);
        assertEquals(-100, result.longValue());
    }

    @Test
    public void testSelectLastElement() throws Exception {
        Integer[] a = {34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89};
        int k = a.length - 1;
        QuickSelect<Integer> qs = new QuickSelect<Integer>();
        Integer result = qs.select(a, k);
        assertEquals(89, result.longValue());
    }

    @Test
    public void testPartition() throws Exception {
        Integer[] a = {34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89};
        Integer expected = a[0];
        QuickSelect<Integer> qs = new QuickSelect<>();
        int[] result = qs.partition(new Partition<>(a, 0, a.length));
        assertEquals(expected, a[result[0]]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectWithEmptyArray() throws Exception {
        Integer[] a = {};
        int k = 0;
        QuickSelect<Integer> qs = new QuickSelect<>();
        qs.select(a, k);
    }

    @Test
    public void testSelectWithSingleElement() throws Exception {
        Integer[] a = {42};
        int k = 0;
        QuickSelect<Integer> qs = new QuickSelect<>();
        Integer result = qs.select(a, k);
        assertEquals(Integer.valueOf(42), result);
    }

    @Test
    public void testSelectWithDuplicates() throws Exception {
        Integer[] a = {5, 1, 5, 3, 5, 2};
        int k = 3;
        QuickSelect<Integer> qs = new QuickSelect<>();
        Integer result = qs.select(a, k);
        assertEquals(Integer.valueOf(5), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectWithInvalidKNegative() throws Exception {
        Integer[] a = {1, 2, 3, 4, 5};
        int k = -1;
        QuickSelect<Integer> qs = new QuickSelect<>();
        qs.select(a, k);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectWithInvalidKOutOfBounds() throws Exception {
        Integer[] a = {1, 2, 3, 4, 5};
        int k = 5;
        QuickSelect<Integer> qs = new QuickSelect<>();
        qs.select(a, k);
    }
}