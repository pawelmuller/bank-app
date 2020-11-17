import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int to_calculate;
        while (true) {
            to_calculate = input.nextInt();
            if (to_calculate < 0) break;
            System.out.println(to_calculate + "! = " + Main.calculate(to_calculate));
        }
        input.close();
    }

    private static long calculate(int factor) {
        if (factor > 0) return calculate(factor - 1) * factor;
        else return 1;
    }
}
