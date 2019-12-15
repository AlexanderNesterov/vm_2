import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Andrey {
    private static final String OK = "IER := 0 Нет ошибок";
    private static final String ERROR_1 = "IER := 1 Требуемая точность не достигнута";
    private static final String ERROR_2 = "IER := 2 Требуемая точность не достигается. Модуль разности между\n" +
            "двумя последовательными интерполяционными значениями перестаёт\n" +
            "уменьшаться";
    private static final String ERROR_3 = "IER := 3 Нарушение порядка последовательности";
    private static final String ERROR_4 = "IER := 4 Точка не принадлжеит никакому отрезку";

    private double[] x;
    private double[] y;
    private double[] h;
    private double[] a;
    private double[] b;
    private double[] c;
    private double[] d;
    private double[] f;
    private double[] mu;
    private double[] nu;
    private double leftBorder;
    private double rightBorder;
    private double pointValue;

    public void calculate() throws Exception {

        Scanner sc = new Scanner(new File("resources/test_1.txt"));
        FileOutputStream fos = new FileOutputStream("resources/output.txt");

        int length = Integer.parseInt(sc.nextLine());
        x = readToArray(length, sc);
        y = readToArray(length, sc);
        String[] borders = sc.nextLine().split(" ");
        leftBorder = Double.parseDouble(borders[0]);
        rightBorder = Double.parseDouble(borders[1]);
        pointValue = Double.parseDouble(sc.nextLine());

        System.out.println("length " + length);
        System.out.println("x " + Arrays.toString(x));
        System.out.println("y " + Arrays.toString(y));
        System.out.println("left border " + leftBorder);
        System.out.println("right border " + rightBorder);
        System.out.println("point value " + pointValue);

        sc.close();

        if (length < 2) {
            throw new Exception();
        }

        for (int i = 0; i < length - 1; i++) {
            if (x[i] > x[i + 1]) {
                fos.write(ERROR_3.getBytes());
                fos.close();
                throw new Exception(ERROR_3);
            }
        }

        int segmentNumber = 0;

        try {
            segmentNumber = checkSegment(x, pointValue);
        } catch (Exception e) {
            fos.write(e.getMessage().getBytes());
            fos.close();
            e.printStackTrace();
        }

        System.out.println("segment number " + segmentNumber);

        a = new double[length + 1];
        b = new double[length + 1];
        c = new double[length + 1];
        d = new double[length + 1];
        mu = new double[length + 1];
        nu = new double[length + 1];

        calculateSpline(length);
//        Map<Double, Double> map = calculateDerivative(length);
//        System.out.println(map);
        System.out.println(calcOne(segmentNumber));
//        System.out.println(c[segmentNumber] + d[segmentNumber] * (pointValue - x[segmentNumber]));
    }

    private double[] readToArray(int length, Scanner sc) {
        String[] line = sc.nextLine().split(" ");
        double[] result = new double[length + 1];

        for (int i = 0; i < length; i++) {
            result[i] = Double.parseDouble(line[i]);
        }

        return result;
    }

    private int checkSegment(double[] x, double pointValue) throws Exception {
        for (int i = 0; i < x.length - 1; i++) {
            if (pointValue >= x[i] && pointValue < x[i + 1]) {
                return i + 1;
            }
        }

        throw new Exception(ERROR_4);
    }

    private double h(int i) {
        return x[i] - x[i - 1];
    }

    private double f(int i) {
        return (y[i + 1] - y[i]) / h(i + 1) - (y[i] - y[i - 1]) / h(i);
    }

    private void calculateSpline(int length) {
        mu[1] = rightBorder;
        nu[1] = leftBorder;

        for (int i = 1; i < length; i++) {
            mu[i + 1] = -h(i + 1) / (2 * (h(i) + h  (i + 1)) + h(i) * mu[i]);
            nu[i + 1] = (6 * f(i) - h(i) * nu[i]) / (2 * (h(i) + h(i + 1)) + h(i) * mu[i]);
        }

        for (int i = length - 1; i > 0; i--) {
            c[i] = mu[i + 1] * c[i + 1] + nu[i + 1];
        }

        for (int i = 0; i < length + 1; i++)
            a[i] = y[i];

        for (int i = 1; i < length + 1; i++) {
            d[i] = (c[i] - c[i - 1]) / h(i);
        }

        for (int i = 1; i < length + 1; i++) {
            b[i] = (h(i) / 2) * c[i] - (h(i) * h(i) / 6) * d[i] + (y[i] - y[i - 1]) / h(i);
        }
    }

    private double calcOne(int i) {
        return c[i] + d[i] * (pointValue - x[i]);
    }

/*    private Map<Double, Double> calculateDerivative(int length) {
        Map<Double, Double> map = new HashMap<>();

        for (int i = 0; i < length; i++) {
            map.put(x[i], calcOne(i));
        }

        return map;
    }*/
}
