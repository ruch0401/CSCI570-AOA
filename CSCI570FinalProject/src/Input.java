import java.util.List;

public class Input {
    String firstString;
    String secondString;
    List<Integer> indexes1;
    List<Integer> indexes2;

    public Input(String firstString, String secondString, List<Integer> indexes1, List<Integer> indexes2) {
        this.firstString = firstString;
        this.secondString = secondString;
        this.indexes1 = indexes1;
        this.indexes2 = indexes2;
    }

    public String getFirstString() {
        return firstString;
    }

    public String getSecondString() {
        return secondString;
    }

    public List<Integer> getIndexes1() {
        return indexes1;
    }

    public List<Integer> getIndexes2() {
        return indexes2;
    }

    @Override
    public String toString() {
        return "Input [" +
                "firstString='" + firstString + '\'' +
                ", secondString='" + secondString + '\'' +
                ", indexes1=" + indexes1 +
                ", indexes2=" + indexes2 +
                ']';
    }
}
