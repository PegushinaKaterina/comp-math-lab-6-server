package com.example.compmathlab6server.dto;

import com.example.compmathlab6server.entity.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ResponseDto {
    private List<String[]> euler;

    private List<String[]> rungeKutta;

    private List<String[]> adams;

    private List<String[]> exactSolution;

    private List<String> errors;

    double max = Integer.MIN_VALUE;
    double min = Integer.MAX_VALUE;

    double maxDifference = Integer.MIN_VALUE;


    public ResponseDto(List<double[]> euler, List<double[]> rungeKutta, List<double[]> adams, List<double[]> exactSolution, List<String> errors) {
        List<double[]> eulerRes = getRes(euler, exactSolution);
        List<double[]> rungeKuttaRes = getRes(rungeKutta, exactSolution);

        this.euler = new ArrayList<>();
        for (double[] doubles : eulerRes) {
            this.euler.add(new String[]{String.valueOf(doubles[0]), String.valueOf(doubles[1])});
            if (doubles[1] > max) {
                max = doubles[1];
            }
            if (doubles[1] < min) {
                min = doubles[1];
            }
        }

        this.rungeKutta = new ArrayList<>();
        for (double[] doubles : rungeKuttaRes) {
            this.rungeKutta.add(new String[]{String.valueOf(doubles[0]), String.valueOf(doubles[1])});
            if (doubles[1] > max) {
                max = doubles[1];
            }
            if (doubles[1] < min) {
                min = doubles[1];
            }
        }

        this.adams = new ArrayList<>();
        for (int i = 0; i < adams.size(); i++) {
            this.adams.add(new String[]{String.valueOf(adams.get(i)[0]), String.valueOf(adams.get(i)[1])});
            if (adams.get(i)[1] > max) {
                max = adams.get(i)[1];
            }
            if (adams.get(i)[1] < min) {
                min = adams.get(i)[1];
            }
            if(Math.abs(adams.get(i)[1] - exactSolution.get(i)[1]) > maxDifference) {
                maxDifference = Math.abs(adams.get(i)[1] - exactSolution.get(i)[1]);
            }
        }

        this.exactSolution = new ArrayList<>();
        for (double[] doubles : exactSolution) {
            this.exactSolution.add(new String[]{String.valueOf(doubles[0]), String.valueOf(doubles[1])});
            if (doubles[1] > max) {
                max = doubles[1];
            }
            if (doubles[1] < min) {
                min = doubles[1];
            }
        }

        this.errors = errors;
    }

    public static List<double[]> getRes(List<double[]> list, List<double[]> exactSolution) {
        List<double[]> listRes = new ArrayList<>();
        list.add(new double[]{Double.MAX_VALUE, 0});
        int j = 0;
        for (int i = 1; i < list.size() && j < exactSolution.size(); i++) {
            if (Math.abs(list.get(i)[0] - exactSolution.get(j)[0]) > Math.abs(list.get(i - 1)[0] - exactSolution.get(j)[0])) {
                listRes.add(list.get(i - 1));
                j ++;
            }

        }
        return  listRes;
    }
}
