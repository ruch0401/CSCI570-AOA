import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StringGenerator {
    public static final String BASE_PATH = "CSCI570FinalProject/resources";
    public static void main(String[] args) throws Exception {
        Input input = generateInput("input.txt");
        System.out.println(input);
    }

    private static List<String> fetchDataFromFile(String filename) {
        Path path = Paths.get(String.format("%s/%s", BASE_PATH, filename));
        List<String> data = null;
        try {
            data = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static Input generateInput(String filename) throws Exception {
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
