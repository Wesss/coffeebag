package org.samcrow;

import java.util.concurrent.RecursiveAction;

/**
 * Puts the lengths of non-empty strings into an output array
 * @author Sam Crow
 *
 */
public class SumToLength extends RecursiveAction {

    private static final long serialVersionUID = 1L;

    /**
     * The index of the first element to operate on
     */
    private final int low;
    /**
     * The index of the element after the last element to operate on
     */
    private final int high;
    /**
     * The input bits that indicate which strings from the input should be
     * included in the output
     */
    private final int[] inBits;
    /**
     * The strings whose length will be placed in the output array
     */
    final String[] inStrings;
    /**
     * The indices of values in the output array
     */
    private final int[] inIndices;
    /**
     * The integers set for output
     */
    private final int[] out;

    /**
     * Creates a task to put the lengths of strings from the range [low, high)
     * of the input
     * 
     * @param out
     *            an array that will be filled with the lengths of non-empty
     *            strings in the original array of strings
     * @param inStrings
     *            the strings to process
     * @param inBits
     *            the array of bits indicating where non-empty strings are
     *            present in the input
     * @param inIndices
     *            the output of the parallel-prefix sum, indicating the desired
     *            indices of the output
     * @param low
     *            the index of the first input element to operate on
     * @param high
     *            the index of the element after the last input element to
     *            operate on
     */
    public SumToLength(int[] out, String[] inStrings, int[] inBits,
            int[] inIndices, int low, int high) {

        assert out != null;
        assert inBits != null;
        assert inIndices != null;

        assert inBits.length == inIndices.length;
        assert out.length == inIndices[inIndices.length - 1];
        assert low <= high;
        assert low >= 0;
        assert high <= inBits.length;

        this.out = out;
        this.inStrings = inStrings;
        this.inBits = inBits;
        this.inIndices = inIndices;
        this.low = low;
        this.high = high;
    }

    @Override
    protected void compute() {
        final int size = high - low;
        if (size == 0) {
            // Do nothing
        }
        else if (size == 1) {
            if (inBits[low] == 1) {
                out[inIndices[low] - 1] = inStrings[low].length();
            }
        }
        else {
            // Parallelize
            final int middle = (low + high) / 2;
            final SumToLength task1 = new SumToLength(out, inStrings, inBits, inIndices, low, middle);
            final SumToLength task2 = new SumToLength(out, inStrings, inBits, inIndices, middle, high);
            
            task1.fork();
            task2.compute();
            task1.join();
        }
    }

}
