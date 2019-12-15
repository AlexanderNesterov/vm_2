import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class Kostya {
    private static final String OK = "IER := 0 Нет ошибок";
    private static final String ERROR_1 = "IER := 1 Требуемая точность не достигнута";
    private static final String ERROR_2 = "IER := 2 Требуемая точность не достигается. Модуль разности между\n" +
            "двумя последовательными интерполяционными значениями перестаёт\n" +
            "уменьшаться";
    private static final String ERROR_3 = "IER := 3 Нарушение порядка последовательности";
    private static final String ERROR_4 = "IER := 4 Точка не принадлжеит никакому отрезку";

    private double[] h;
    private double[] a;
    private double[] b;
    private double[] cc;
    private double[] f;
    private double[] mu;
    private double[] nu;
    private double[] c;
    private double[] d;
    private double[] b_c;

    public void calculate() throws Exception {

        Scanner sc = new Scanner(new File("resources/test_1.txt"));
        FileOutputStream fos = new FileOutputStream("resources/output.txt");

        int length = Integer.parseInt(sc.nextLine());
        double[] x = readToArray(length, sc);
        double[] y = readToArray(length, sc);
        String[] borders = sc.nextLine().split(" ");
        double leftBorderCondition = Double.parseDouble(borders[0]);
        double rightBorderCondition = Double.parseDouble(borders[1]);
        double pointValue = Double.parseDouble(sc.nextLine());

        System.out.println("length " + length);
        System.out.println("x " + Arrays.toString(x));
        System.out.println("y " + Arrays.toString(y));
        System.out.println("left border " + leftBorderCondition);
        System.out.println("right border " + rightBorderCondition);
        System.out.println("point value " + pointValue);

        sc.close();

/*        for (int i = 0; i < length - 1; i++) {
            if (x[i] > x[i + 1]) {
                fos.write(ERROR_3.getBytes());
                fos.close();
                throw new Exception(ERROR_3);
            }
        }*/

        int segmentNumber = 0;

        try {
            segmentNumber = checkSegment(x, pointValue);
        } catch (Exception e) {
            fos.write(e.getMessage().getBytes());
            fos.close();
            e.printStackTrace();
        }

        System.out.println("segment number " + segmentNumber);

        calculateH(x, length);
        calculateVectors(y, leftBorderCondition, rightBorderCondition, length);
        calculateMuNu(length);
        calculateC(length);
        calculateD(y, length);

        double result = calculateSpline(x, pointValue, segmentNumber);
        double sp = c[1] - d[1] * h[1];
        System.out.println(c[1] + " " + d[1] + " " + h[1]);
        System.out.println(Arrays.toString(b_c));
        System.out.println(result);
        System.out.println(sp);
    }

    private double[] readToArray(int length, Scanner sc) {
        String[] line = sc.nextLine().split(" ");
        double[] result = new double[length];

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

    private void calculateH(double[] x, int length) {
        h = new double[length + 1];

        for (int i = 1; i < length; i++) {
            h[i] = x[i] - x[i - 1];
        }
    }

    private void calculateVectors(double[] y, double leftBorder, double rightBorder, int length) {
        a = new double[length];
        b = new double[length];
        cc = new double[length];
        f = new double[length];
        a[0] = 0;
        b[0] = 0;
        cc[0] = 1;
        f[0] = leftBorder;

        for (int i = 1; i < length - 1; i++) {
            a[i] = h[i];
            b[i] = h[i + 1];
            cc[i] = h[i] - h[i - 1];
            f[i] = 6 * ((y[i + 1] - y[i]) / h[i + 1] - (y[i] - y[i - 1]) / h[i]);
        }

        cc[length - 1] = 1;
        a[length - 1] = 0;
        b[length - 1] = 0;
        f[length - 1] = rightBorder;

        System.out.println("a: " + Arrays.toString(a));
        System.out.println("b: " + Arrays.toString(b));
        System.out.println("cc: " + Arrays.toString(cc));
    }

    private void calculateMuNu(int length) {
        mu = new double[length + 1];
        nu = new double[length + 1];
        mu[1] = -b[0] / cc[0];
        nu[1] = f[0] / cc[0];

        for (int i = 1; i < length; i++) {
            mu[i + 1] = -b[i] / (cc[i] + mu[i] * a[i]);
            nu[i + 1] = (f[i] - a[i] * nu[i]) / (cc[i] + mu[i] * a[i]);
        }

        System.out.println("mu: " + Arrays.toString(mu));
        System.out.println("nu: " + Arrays.toString(nu));
    }

    private void calculateC(int length) {
        c = new double[length + 1];
        c[length - 1] = (f[length - 1] - a[length - 1] * nu[length - 1])
                / (cc[length - 1] + a[length - 1] * mu[length - 1]);

        for (int i = length - 1; i >= 0; i--) {
            c[i] = mu[i + 1] * c[i + 1] + nu[i + 1];
        }
    }

    private void calculateD(double[] y, int length) {
        d = new double[length + 1];
        b_c = new double[length + 1];

        for (int i = 1; i < length; i++) {
            d[i] = (c[i] - c[i - 1]) / h[i];
//            b_c[i] = h[i] / 2 * c[i] - h[i] * h[i] / 6 * d[i] + (y[i] - y[i - 1]) / h[i];
//            b_c[i] = c[i] + d[i] * (д - x[i]);
        }
    }

    private double calculateSpline(double[] x, double pointValue, int segmentNumber) {
        return c[segmentNumber] + d[segmentNumber] * (pointValue - x[segmentNumber]);
    }
}

