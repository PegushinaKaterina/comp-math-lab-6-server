package com.example.compmathlab6server.service;
import com.example.compmathlab6server.Solver;
import com.example.compmathlab6server.dto.RequestDto;
import com.example.compmathlab6server.dto.ResponseDto;
import com.example.compmathlab6server.entity.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Override
    public ResponseDto solve(RequestDto requestDto) {
        BiFunction<Double, Double, Double> f;
        BiFunction<Double, Double, Double> fToGetC;
        BiFunction<Double, Double, Double> fForExactSolution;
        double x0 = requestDto.getX0();
        double xn = requestDto.getXn();
        double y0 = requestDto.getY0();
        double h = requestDto.getH();
        double eps = requestDto.getEps();
        if (requestDto.getEquation() == 1) {
            fToGetC = (x, y) -> ((y + x + 1) / Math.exp(x));
            f = (x, y) -> (y + x);
            fForExactSolution = (x, c) -> (c * Math.exp(x) - x - 1);
        } else if (requestDto.getEquation()  == 2) {
            fToGetC = (x, y) -> (y * Math.exp(2 * Math.cos(x)));
            f = (x, y) -> (2 * Math.sin(x) * y);
            fForExactSolution = (x, c) -> (c / Math.exp(2 * Math.cos(x)));
        } else {
            fToGetC = (x, y) -> (y / Math.exp(Math.pow(x, 5)));
            f = (x, y) -> (5 * Math.pow(x, 4) * y);
            fForExactSolution = (x, c) -> (c * Math.exp(Math.pow(x, 5)));
        }
        double c = Solver.getC(x0, y0, fToGetC);
        List<String> errors = new ArrayList<>();
        List<double[]> euler = new ArrayList<>();
        try {
            euler = Solver.solveByEulerMethod(x0, xn, y0, h, eps, f);
        } catch (RuntimeException e) {
            errors.add("В методе Эйлера превышено время ожидания. Измените входные данные\n");
        }
        List<double[]> rungeKutta = new ArrayList<>();
        try {
            rungeKutta = Solver.solveByRungeKuttaMethod(x0, xn, y0, h, eps, f);
        } catch (RuntimeException e) {
            errors.add("В методе Рунге-Кутта превышено время ожидания. Измените входные данные\n");
        }
        List<double[]> adams = new ArrayList<>();
        try {
            adams = Solver.solveByAdamsMethod(x0, xn, y0, h, f);
        } catch (RuntimeException e) {
            errors.add("Привет, меня зовут Валли. Я еще не умею работать с Nan и Infinity, поэтому метод Адамса сломался(. Измените входные данные\n");

        }
        List<double[]> exactSolution = Solver.getExactSolution(x0, xn, y0, h, c, fForExactSolution);
        return new ResponseDto(euler, rungeKutta, adams, exactSolution, errors);
    }
}
