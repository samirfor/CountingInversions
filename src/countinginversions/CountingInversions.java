package countinginversions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import std.In;

/**
 * Counting Inversions with O(n * log n)
 *
 * @author Samir C. Costa
 */
public class CountingInversions {

    public long merge(int[] array, int[] left, int[] right) {
        int i = 0, j = 0, count = 0;
        while (i < left.length || j < right.length) {
            if (i == left.length) {
                array[i + j] = right[j];
                j++;
            } else if (j == right.length) {
                array[i + j] = left[i];
                i++;
            } else if (left[i] <= right[j]) {
                array[i + j] = left[i];
                i++;
            } else {
                array[i + j] = right[j];
                count += left.length - i;
                j++;
            }
        }
        return count;
    }

    public long countInversions(int[] array, boolean trace) {
        if (array.length < 2) {
            return 0;
        }

        int m = (array.length + 1) / 2;
        int left[] = Arrays.copyOfRange(array, 0, m);
        int right[] = Arrays.copyOfRange(array, m, array.length);

        // Trace
        if (trace) {
            System.out.print("L: {");
            for (int i = 0; i < left.length; i++) {
                System.out.print(left[i]);
                if (i != left.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("}\t\t");
            System.out.print("R: {");
            for (int i = 0; i < right.length; i++) {
                System.out.print(right[i]);
                if (i != right.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("}");
        }
        //

        return countInversions(left, trace) + countInversions(right, trace) + merge(array, left, right);
    }

    private int countRowsOfFile(String filepath) throws FileNotFoundException, IOException {
        LineNumberReader lnr;
        lnr = new LineNumberReader(new FileReader(filepath));
        lnr.skip(Long.MAX_VALUE);
        int linhas = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
        // Finally, the LineNumberReader object should be closed to prevent resource leak
        lnr.close();
        return linhas;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar CountingInversions.jar filepath [trace] [numTests]");
            System.exit(1);
        }
        In in = new In(args[0]);
        CountingInversions ci = new CountingInversions();
        boolean trace = false;

        // Counting rows of file
        int countLines = 0;
        try {
            countLines = ci.countRowsOfFile(args[0]) - 1;
        } catch (IOException ex) {
            System.out.println("File " + args[0] + " not found.");
            System.exit(1);
        }

        // Carregando do arquivo
        int array[] = new int[countLines];
        for (int i = 0; i < countLines; i++) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.length() != 0) {
                array[i] = Integer.parseInt(line);
            }

        }
        
        // Trace
        if (args.length == 2 && args[1].equalsIgnoreCase("trace")) {
            trace = true;
            System.out.print("Inicial array: {");
            for (int i = 0; i < array.length; i++) {
                System.out.print(array[i]);
                if (i != array.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("}");
            System.out.println("Trace:");
        }

        // Statistics
        int numTests;
        if (args.length == 3) {
            numTests = Integer.parseInt(args[2]); // get from cli
        } else {
            numTests = 15; // default
        }
        long costs[] = new long[numTests];
        long sumCosts = 0;
        long inversions = ci.countInversions(array, trace);
        for (int i = 0; i < numTests; i++) {
            long start = System.nanoTime();
            ci.countInversions(array, trace);
            long cost = (System.nanoTime() - start) / (long) Math.pow(10, 6); // miliseconds
            System.out.println("Elapsed time of the algorithm #" + (i + 1) + " = " + cost + " ms.");
            costs[i] = cost;
            sumCosts += cost;
        }
        System.out.println("Inversions = " + inversions);

        long avg = sumCosts / numTests;
        long deviations[] = new long[numTests];
        long variance = 0;
        for (int i = 0; i < numTests; i++) {
            deviations[i] = costs[i] - avg;
            variance += deviations[i] * deviations[i];
        }
        variance /= 10;
        System.out.println("\nStatistics for " + numTests + " executions:");
        System.out.println("Average = " + avg);
        System.out.println("Variance = " + variance);
        System.out.println("Standart deviation = " + Math.sqrt(variance));
    }

}
