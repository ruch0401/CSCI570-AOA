public class Alignment {
    String s1;
    String s2;

    public Alignment() {};

    public Alignment(String s1, String s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public String getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }

    @Override
    public String toString() {
        return String.format("String #1: [%s] \nString #2: [%s]", s1, s2);
    }
}
