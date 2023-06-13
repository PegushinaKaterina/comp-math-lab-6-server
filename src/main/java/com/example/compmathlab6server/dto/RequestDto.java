package com.example.compmathlab6server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestDto {
    private int equation;
    private double x0;
    private double xn;
    private double y0;
    private double h;
    private double eps;
}
