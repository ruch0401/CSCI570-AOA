import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class StringGenerator {
    public static final String BASE_PATH = "CSCI570FinalProject/resources";
    public static final String FILE_NAME = "input.txt";
    public static final int[][] MISMATCH_COST =
            {
                    {0, 110, 48, 94},
                    {110, 0, 118, 48},
                    {48, 118, 0, 110},
                    {94, 48, 110, 0}
            };
    public static final int GAP_PENALTY = 30;


    public static Map<Character, Integer> hm = new HashMap<>();

    public StringGenerator() {
        hm.put('A', 0);
        hm.put('C', 1);
        hm.put('G', 2);
        hm.put('T', 3);
    }

    public static void main(String[] args) throws Exception {
        StringGenerator stringGenerator = new StringGenerator();
        Input input = stringGenerator.generateInput(FILE_NAME);
        System.out.println(input);

        String a = stringGenerator.generateInputString(input.firstString, input.indexes1);
        String b = stringGenerator.generateInputString(input.secondString, input.indexes2);

        // Printing input strings
        System.out.println(a + "\n" + b);

        // Run Needleman and Wunsch Algorithm
//        Instant startTime = Instant.now();
//         Alignment alignment = stringGenerator.optimalAlignment("ATCGT", "TGGTC");
//        Alignment alignment = stringGenerator.optimalAlignment(a, b);
//        Instant endTime = Instant.now();
//        System.out.println(alignment);
//        System.out.printf("Time taken to execute the algorithm: %d ns%n", Duration.between(startTime, endTime).getNano());


        // testing out spaced efficient version to find scoring value
        System.out.println(stringGenerator.spaceEfficientAlignment("ATCGT", "TGGTC"));
    }

    private List<String> fetchDataFromFile(String filename) {
        Path path = Paths.get(String.format("%s/%s", BASE_PATH, filename));
        List<String> data = null;
        try {
            data = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private Input generateInput(String filename) {
        List<String> data = fetchDataFromFile(filename);
        int blankStringIndex = data.indexOf("");

        // Creating input for the generateString() method
        // The bifurcation and index allotment has been done by adding a blank character between the 2 sets of inputs
        String firstBaseString = data.get(0);
        String secondBaseString = data.get(blankStringIndex + 1);
        List<Integer> indexes1 = new ArrayList<>();
        List<Integer> indexes2 = new ArrayList<>();

        for (int i = 1; i < blankStringIndex; i++) {
            indexes1.add(Integer.parseInt(data.get(i)));
        }

        for (int i = blankStringIndex + 2; i < data.size(); i++) {
            indexes2.add(Integer.parseInt(data.get(i)));
        }

        return new Input(firstBaseString, secondBaseString, indexes1, indexes2);
    }

    /**
     * @param base    is the string using which the final string will be created
     * @param indexes an array of indexes after which the previous string is added to the cumulative string
     * @return cumulated string
     * @throws Exception if the length of the generated string does not match with the supposed length of the cumulated string
     */
    public String generateInputString(String base, List<Integer> indexes) throws Exception {
        int lengthSupposedToBe = (int) (Math.pow(2, indexes.size())) * base.length();
        StringBuilder sb = null;
        for (int index : indexes) {
            sb = new StringBuilder(base);
            base = sb.insert(index + 1, sb.toString().toCharArray(), 0, sb.toString().length()).toString();
        }
        assert sb != null;
        int generatedStringLength = sb.toString().length();
        if (generatedStringLength == lengthSupposedToBe) {
            return sb.toString();
        } else {
            throw new Exception("Actual and calculated length of the string do not match");
        }
    }

    public Alignment optimalAlignment(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        dp[0][0] = 0;

        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[0].length; j++) {
                if (i == 0) {
                    dp[i][j] = j * GAP_PENALTY;
                } else if (j == 0) {
                    dp[i][j] = i * GAP_PENALTY;
                } else {
                    int top = dp[i - 1][j] + GAP_PENALTY;
                    int left = dp[i][j - 1] + GAP_PENALTY;
                    int diagonal = dp[i - 1][j - 1] + getMismatchCost(a.charAt(i - 1), b.charAt(j - 1));
                    dp[i][j] = Math.min(diagonal, Math.min(top, left));
                }
            }
        }
        print2DMatrix(dp);
        return printAlignment(a, b, dp);
    }

    private Alignment printAlignment(String a, String b, int[][] dp) {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        int i = a.length();
        int j = b.length();
        while (i != 0 && j != 0) {
            int left = dp[i][j - 1] + GAP_PENALTY;
            int top = dp[i - 1][j] + GAP_PENALTY;
            int diagonal = dp[i - 1][j - 1] + getMismatchCost(a.charAt(i - 1), b.charAt(j - 1));
            if (dp[i][j] == diagonal) {
                sb1.append(a.charAt(i - 1));
                sb2.append(b.charAt(j - 1));
                i--;
                j--;
            } else if (dp[i][j] == left) {
                 sb1.append("_");
                sb2.append(b.charAt(j - 1));
                j--;
            } else if (dp[i][j] == top) {
                sb1.append(a.charAt(i - 1));
                sb2.append("_");
                i--;
            }
        }

        while (i > 0) {
            // append character of the string for which the index is changing to the corresponding stringbuilder
            // in this case, i is changing, hence we append sb1 with string a's character at i
            sb1.append(a.charAt(i - 1));
            sb2.append("_");
            i--;
        }

        while (j > 0) {
            sb1.append("_");
            // append character of the string for which the index is changing to the corresponding stringbuilder
            // in this case, j is changing, hence we append sb2 with string b's character at j
            sb2.append(b.charAt(j - 1));
            j--;
        }
        return new Alignment(sb1.reverse().toString(), sb2.reverse().toString());
    }

    private int spaceEfficientAlignment(String a, String b) {
        // first, I need to determine which string is small or big to decide the length of the evenEdits and oddEdits that we are going to create
        String big = a.length() < b.length() ? b : a;
        String small = a.length() >= b.length() ? b : a;

        // creating 2 arrays - evenEdits and oddEdits that will store my currentArray and previousArray
        int[] evenEdits = new int[small.length() + 1];
        int[] oddEdits = new int[small.length() + 1];

        // Initializing the evenEdits array because we will always start from index 0 which is even
        for (int j = 0; j < small.length() + 1; j++) {
            evenEdits[j] = j * GAP_PENALTY;
        }

        // Initialize 2 arrays, currentEdits and previousEdits which will store our DP calculations
        int[] currentEdits;
        int[] previousEdits;

        // check which (from oddEdits and evenEdits) is the currentEdit and which one is the other
        // we traverse the while loop till big.length() because now we need to traverse the bigger string.
        // refer to the dp[][] array in Solution1 to draw parallels
        for (int i = 1; i < big.length() + 1; i++) {
            if (i % 2 == 0) {
                currentEdits = evenEdits;
                previousEdits = oddEdits;
            } else {
                currentEdits = oddEdits;
                previousEdits = evenEdits;
            }

            // this is same as initializing the dp[][] array's first column
            currentEdits[0] = i * GAP_PENALTY;

            for (int j = 1; j < small.length() + 1; j++) {
                int left = currentEdits[j - 1] + GAP_PENALTY;
                int top = previousEdits[j] + GAP_PENALTY;
                int diagonal = previousEdits[j - 1] + getMismatchCost(a.charAt(i - 1), b.charAt(j - 1));
                currentEdits[j] = Math.min(diagonal, Math.min(left, top));
                System.out.println(Arrays.toString(currentEdits));
            }
        }
        // depending on the length of the bigger string (which spans across the rows in the dp[][] arrray)
        // the answer might be stored in either the evenEdits[] or the oddEdits[] array
        return (big.length() % 2 == 0) ? evenEdits[small.length()] : oddEdits[small.length()];
    }

    private int getMismatchCost(char c1, char c2) {
        int i = hm.get(c1);
        int j = hm.get(c2);
        System.out.println(String.format("Checking mismatch for[%s, %s]. Mismatch value found: %d", c1, c2, MISMATCH_COST[i][j]));
        return MISMATCH_COST[i][j];
    }

    private void print2DMatrix(int[][] dp) {
        for (int[] ints : dp) {
            for (int j = 0; j < dp[0].length; j++) {
                System.out.print(ints[j] + "\t");
            }
            System.out.println();
        }
    }
}
