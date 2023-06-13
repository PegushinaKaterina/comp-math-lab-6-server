package com.example.compmathlab6server.service;

import com.example.compmathlab6server.dto.RequestDto;
import com.example.compmathlab6server.dto.ResponseDto;

public interface Service {
    ResponseDto solve(RequestDto requestDto);
}
