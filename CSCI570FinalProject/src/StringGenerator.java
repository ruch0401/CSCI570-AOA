public class StringGenerator {
    public static void main(String[] args) throws Exception {
        String baseString1 = "ACTG";
        int[] indexes1 = {3, 6, 1};
        String baseString2 = "TACG";
        int[] indexes2 = {1, 2, 9};

        String finalString1 = generateInputString(baseString1, indexes1);
        String finalString2 = generateInputString(baseString2, indexes2);
        System.out.println(finalString1 + "\n" + finalString2);
    }


    /**
     * @param base is the string using which the final string will be created
     * @param indexes an array of indexes after which the previous string is added to the cumulative string
     * @return cumulated string
     * @throws Exception if the length of the generated string does not match with the supposed length of the cumulated string
     */
    public static String generateInputString(String base, int[] indexes) throws Exception {
        int lengthSupposedToBe =  (int) (Math.pow(2, indexes.length)) * base.length();
        StringBuilder sb = null;
        for (int index: indexes) {
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
}
