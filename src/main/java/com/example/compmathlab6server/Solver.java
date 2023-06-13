package com.example.compmathlab6server;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Solver {
    public static double round(double x) {
        return Math.round(x * 10000) / 10000D;
    }

    public static double getC(double x0, double y0, BiFunction<Double, Double, Double> fToGetC) {
        return fToGetC.apply(x0, y0);
    }

    public static List<double[]> getExactSolution(double x0, double xn, double y0, double h, double c, BiFunction<Double, Double, Double> fForExactSolution) {
        int n = (int) (Math.floor((xn - x0) / h) + 1);

        List<double[]> points = new ArrayList<>();

        double[] point = new double[]{x0, y0};
        points.add(point);

        for (int i = 1; i < n; i++) {
            double x = x0 + i * h;
            double y = fForExactSolution.apply(x, c);

            point = new double[]{round(x), round(y)};
            points.add(point);
        }
        return points;
    }

    @FunctionalInterface
    public interface getYFunction<T, U, R> {
        R apply(T t, T t2, T t3, U u);
    }

    private static Double getYByEulerMethod(Double h, Double x, Double y, BiFunction<Double, Double, Double> f) {
        double yDerivative = f.apply(x, y);
        return y + h * yDerivative;
    }

    private static Double getYByRungeKuttaMethod(Double h, Double x, Double y, BiFunction<Double, Double, Double> f) {
        double k1 = h * f.apply(x, y);
        double k2 = h * f.apply(x + h / 2, y + k1 / 2);
        double k3 = h * f.apply(x + h / 2, y + k2 / 2);
        double k4 = h * f.apply(x + h, y + k3);

        return y + 1D / 6D * (k1 + 2 * k2 + 2 * k3 + k4);
    }


    public static List<double[]> solveByEulerMethod(double x0,
                                                    double xn,
                                                    double y0,
                                                    double h,
                                                    double eps,
                                                    BiFunction<Double, Double, Double> f) {
        return solveByMethod(x0, xn, y0, h, eps, f, Solver::getYByEulerMethod, 1);
    }

    public static List<double[]> solveByRungeKuttaMethod(double x0,
                                                         double xn,
                                                         double y0,
                                                         double h,
                                                         double eps,
                                                         BiFunction<Double, Double, Double> f) {

        return solveByMethod(x0, xn, y0, h, eps, f, Solver::getYByRungeKuttaMethod, 4);
    }

    public static List<double[]> solveByMethod(double x0,
                                               double xn,
                                               double y0,
                                               double h,
                                               double eps,
                                               BiFunction<Double, Double, Double> f,
                                               getYFunction<Double, BiFunction<Double, Double, Double>, Double> getY,
                                               double p) {

        List<double[]> points = new ArrayList<>();

        double[] point = new double[]{x0, y0};
        points.add(point);

        int i = 0;
        double currentH = h;
        double x = x0;

        while (x <= xn) {
            if(i >= 100000) {
                throw new RuntimeException();
            }
            double previousX = points.get(i)[0];
            double previousY = points.get(i)[1];
            i++;

            double yH = getY.apply(currentH, previousX, previousY, f);
            double yHalfH = getY.apply(currentH / 2, previousX, previousY, f);
//            System.out.println(yH + " " + yHalfH);
            while (Math.abs(yH - yHalfH) / p > eps && currentH > 0.0001) {
                currentH /= 2;
                yH = getY.apply(currentH, previousX, previousY, f);
                yHalfH = getY.apply(currentH / 2, previousX, previousY, f);
            }

            x += currentH;
            double y = yH;

            if (round(x) <= xn) {
                point = new double[]{round(x), round(y)};
                points.add(point);
            }
//            System.out.println(h + " " + x);
        }
        return points;
    }

    public static List<double[]> solveByAdamsMethod(double x0, double xn, double y0, double h, BiFunction<Double, Double, Double> f) {
        int n = (int) (Math.floor((xn - x0) / h) + 1);
        System.out.println(n);

        List<double[]> points = new ArrayList<>();

        double[] point = new double[]{x0, y0};
        points.add(point);

        List<Double> yDerivative = new ArrayList<>();
        yDerivative.add(f.apply(x0, y0));

        for (int i = 1; i < 4; i++) {
            double previousX = points.get(i - 1)[0];
            double previousY = points.get(i - 1)[1];

            double x = x0 + i * h;
            double y = getYByRungeKuttaMethod(h, previousX, previousY, f);

            point = new double[]{round(x), round(y)};
            points.add(point);

            yDerivative.add(f.apply(x, y));
        }

        for (int i = 4; i < n; i++) {
            double previousY = points.get(i - 1)[1];

            double x = x0 + i * h;
            double y = getYByByAdamsMethod(h, previousY, yDerivative, i - 1, f);

            point = new double[]{round(x), round(y)};
            points.add(point);

            if(Double.isNaN(f.apply(x, y)) || Double.isInfinite(f.apply(x, y))) {
                throw new RuntimeException();
            }
            yDerivative.add(f.apply(x, y));

//            System.out.println(f.apply(x, y));

        }
        return points;
    }

    private static Double getYByByAdamsMethod(double h, double y, List<Double> yDerivative, int i, BiFunction<Double, Double, Double> f) {
        double f1 = yDerivative.get(i) - yDerivative.get(i - 1);
        double f2 = yDerivative.get(i) - 2 * yDerivative.get(i - 1) + yDerivative.get(i - 2);
        double f3 = yDerivative.get(i) - 3 * yDerivative.get(i - 1) + 3 * yDerivative.get(i - 2) - yDerivative.get(i - 3);

        return y + h * yDerivative.get(i) + Math.pow(h, 2) / 2 * f1 + 5 * Math.pow(h, 3) / 12 * f2 + 3 * Math.pow(h, 4) / 8 * f3;

    }


//    public static ResponseDto solveByEulerMethod(double x0,
//                                                 double xn,
//                                                 double y0,
//                                                 double h,
//                                                 double eps,
//                                                 BiFunction<Double, Double, Double> f) {
////        return solveByMethod(x0, xn, y0, h, eps, f, Solver::getYByEulerMethod);
//
//        List<Double> xList = new ArrayList<>();
//        xList.add(x0);
//
//        List<Double> yList = new ArrayList<>();
//        yList.add(y0);
//
//        int i = 0;
//        double currentH = h;
//
//        while (xList.get(i) <= xn) {
//            int j = i;
//            i++;
//
//            double yDerivative = f.apply(xList.get(j), yList.get(j));
//
//            double yH = getYByEulerMethod(h, xList.get(j), yList.get(j), f);
//            double yHalfH = getYByEulerMethod(h / 2, xList.get(j), yList.get(j), f);
//            while (yH - yHalfH > eps) {
//                h /= 2;
//                yH = getYByEulerMethod(h, xList.get(j), yList.get(j), f);
//                yHalfH = getYByEulerMethod(h / 2, xList.get(j), yList.get(j), f);
//            }
//
//            xList.add(x0 + i * currentH);
//            yList.add(yH);
//        }
//
//        return new ResponseDto(xList, yList);
//    }

//    public static ResponseDto solveByRungeKuttaMethod(double x0,
//                                                      double xn,
//                                                      double y0,
//                                                      double h,
//                                                      double eps,
//                                                      BiFunction<Double, Double, Double> f) {
//
//        return solveByMethod(x0, xn, y0, h, eps, f, Solver::getYByRungeKuttaMethod);
//        List<Double> xList = new ArrayList<>();
//        xList.add(x0);
//
//        List<Double> yList = new ArrayList<>();
//        yList.add(y0);
//
//        int i = 0;
//        double currentH = h;
//
//        while (xList.get(i) <= xn) {
//            int j = i;
//            i++;
//
//            double yH = getYByRungeKuttaMethod(currentH, xList.get(j), yList.get(j), f);
//            double yHalfH = getYByRungeKuttaMethod(currentH / 2, xList.get(j), yList.get(j), f);
//            while (yH - yHalfH > eps) {
//                h /= 2;
//                yH = getYByRungeKuttaMethod(currentH, xList.get(j), yList.get(j), f);
//                yHalfH = getYByRungeKuttaMethod(currentH / 2, xList.get(j), yList.get(j), f);
//            }
//
//            xList.add(x0 + i * currentH);
//            yList.add(yH);
//        }
//        return new ResponseDto(xList, yList);
//    }
}
