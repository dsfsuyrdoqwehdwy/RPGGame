import static java.lang.Math.pow;

public class Helper {
    public static double round(double number, int needRound) {
        double scale = pow(10, needRound);
        return Math.round(number * scale) / scale;
    }

    public static void printEqualsLine() {
        System.out.println("================================");
    }

    public static void printHyphenLine() {
        System.out.println("--------------------------------");
    }
}
