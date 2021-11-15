import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StringGenerator {
    public static void main(String[] args) throws Exception {
        // Reading data from the file
        Path path = Paths.get("CSCI570FinalProject/resources/input.txt");
        List<String> data = Files.readAllLines(path);
        System.out.println(data);
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

        String finalString1 = generateInputString(firstBaseString, indexes1);
        String finalString2 = generateInputString(secondBaseString, indexes2);
        System.out.println(finalString1 + "\n" + finalString2);
    }


    /**
     * @param base is the string using which the final string will be created
     * @param indexes an array of indexes after which the previous string is added to the cumulative string
     * @return cumulated string
     * @throws Exception if the length of the generated string does not match with the supposed length of the cumulated string
     */
    public static String generateInputString(String base, List<Integer> indexes) throws Exception {
        int lengthSupposedToBe =  (int) (Math.pow(2, indexes.size())) * base.length();
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
