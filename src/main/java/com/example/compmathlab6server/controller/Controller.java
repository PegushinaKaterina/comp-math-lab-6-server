package com.example.compmathlab6server.controller;

import com.example.compmathlab6server.dto.RequestDto;
import com.example.compmathlab6server.dto.ResponseDto;
import com.example.compmathlab6server.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/api")
@RestController
public class Controller {
    private final ServiceImpl service;

    @Autowired
    public Controller(ServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/solve")
    public ResponseEntity<ResponseDto> solve(@RequestBody RequestDto requestDto) {
        return ResponseEntity.ok(service.solve(requestDto));
    }
}