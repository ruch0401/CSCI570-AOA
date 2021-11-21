import java.util.*;

public class StringGenerator2 {
    public static final int[][] MISMATCH_COST =
            {
                    {0, 110, 48, 94},
                    {110, 0, 118, 48},
                    {48, 118, 0, 110},
                    {94, 48, 110, 0}
            };
    public static final int GAP_PENALTY = 30;
    public static Map<Character, Integer> hm = new HashMap<>();

    static String a = "AGTACGCA";
    static String b = "TATGC";

    static class Pair {
        String a;
        String b;

        Pair(String a, String b) {
            this.a = a;
            this.b = b;
        }

        public Pair() {}

        public Pair add(Pair pair) {
            return new Pair(this.a + pair.a, this.b + pair.b);
        }

        @Override
        public String toString() {
            return String.format("String #1: [%s]\nString #2: [%s]", a, b);
        }
    }


    public static void main(String[] args) {
        MapCytokynesToIndices();
        System.out.println(Hirschberg(a, b));
    }

    private static void MapCytokynesToIndices() {
        hm.put('A', 0);
        hm.put('C', 1);
        hm.put('G', 2);
        hm.put('T', 3);
    }

    private static Pair Hirschberg(String a, String b) {
        Pair ans = new Pair();
        StringBuilder a1 = new StringBuilder();
        StringBuilder b1 = new StringBuilder();

        if (a.length() == 0) {
            for (int i = 0; i < b.length(); i++) {
                a1.append("_");
                b1.append(b.charAt(i));
            }
        } else if (b.length() == 0) {
            for (int i = 0; i < a.length(); i++) {
                a1.append(a.charAt(i));
                b1.append("_");
            }
        } else if (a.length() == 1 || b.length() == 1) {
            ans = NeedlemanWunsch(a, b);
        } else {
            int alen = a.length();
            int blen = b.length();
            int bmid = b.length() / 2;

            List<Integer> scoreL = NWScore(a, b.substring(0, bmid));
            String aRev = new StringBuilder(a).reverse().toString();
            String bRev = new StringBuilder(b.substring(bmid + 1, blen)).reverse().toString();
            List<Integer> scoreR = NWScore(aRev, bRev);
            System.out.println(scoreL + "\n" + scoreR);
            Collections.reverse(scoreR);
            int amid = getMin(scoreL, scoreR);

            Pair p1 = Hirschberg(a.substring(0, amid), b.substring(0, bmid));
            Pair p2 = Hirschberg(a.substring(amid + 1, alen), b.substring(bmid + 1, blen));
            System.out.println(a1 + "\n" + b1);
            ans = p1.add(p2);
        }
        return ans;
    }

    private static int getMin(List<Integer> a, List<Integer> b) {
        int min = Integer.MAX_VALUE;
        List<Integer> ans = new ArrayList<>();
        for (int i = 0; i < b.size(); i++) {
            int sum = a.get(i) + b.get(i);
            ans.add(sum);
            min = Math.min(min, sum);
        }
        return ans.indexOf(min);
    }

    private static List<Integer> NWScore(String a, String b) {
        // creating 2 arrays - evenEdits and oddEdits that will store my currentArray and previousArray
        int[] evenEdits = new int[b.length() + 1];
        int[] oddEdits = new int[b.length() + 1];

        // Initializing the evenEdits array because we will always start from index 0 which is even
        for (int j = 0; j < b.length() + 1; j++) {
            evenEdits[j] = j * GAP_PENALTY;
        }

        // Initialize 2 arrays, currentEdits and previousEdits which will store our DP calculations
        int[] currentEdits;
        int[] previousEdits;

        // check which (from oddEdits and evenEdits) is the currentEdit and which one is the other
        // we traverse the while loop till big.length() because now we need to traverse the bigger string.
        // refer to the dp[][] array in Solution1 to draw parallels
        for (int i = 1; i < a.length() + 1; i++) {
            if (i % 2 == 0) {
                currentEdits = evenEdits;
                previousEdits = oddEdits;
            } else {
                currentEdits = oddEdits;
                previousEdits = evenEdits;
            }

            // this is same as initializing the dp[][] array's first column
            currentEdits[0] = i * GAP_PENALTY;

            for (int j = 1; j < b.length() + 1; j++) {
                int left = currentEdits[j - 1] + GAP_PENALTY;
                int top = previousEdits[j] + GAP_PENALTY;
                int diagonal = previousEdits[j - 1] + getMismatchCost(a.charAt(i - 1), b.charAt(j - 1));
                currentEdits[j] = Math.min(diagonal, Math.min(left, top));
            }
        }
        // depending on the length of the bigger string (which spans across the rows in the dp[][] arrray)
        // the answer might be stored in either the evenEdits[] or the oddEdits[] array
        List<Integer> evenList = new ArrayList<>();
        List<Integer> oddList = new ArrayList<>();
        for (int elem: evenEdits) {
            evenList.add(elem);
        }

        for (int elem: oddEdits) {
            oddList.add(elem);
        }

        return (a.length() % 2 == 0) ? evenList : oddList;
    }

    private static int getMismatchCost(char c1, char c2) {
        int i = hm.get(c1);
        int j = hm.get(c2);
        // System.out.println(String.format("Checking mismatch for[%s, %s]. Mismatch value found: %d", c1, c2, MISMATCH_COST[i][j]));
        return MISMATCH_COST[i][j];
    }

    private static Pair NeedlemanWunsch(String a, String b) {
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

    private static Pair printAlignment(String a, String b, int[][] dp) {
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
        return new Pair(sb1.reverse().toString(), sb2.reverse().toString());
    }

    private static void print2DMatrix(int[][] dp) {
        for (int[] ints : dp) {
            for (int j = 0; j < dp[0].length; j++) {
                System.out.print(ints[j] + "\t");
            }
            System.out.println();
        }
    }
}
