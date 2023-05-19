package com.project.DistanceTracker.controllers;

import com.project.DistanceTracker.services.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping
public class CalculationController {

    private final Calculator calculator;

    @Autowired
    public CalculationController(Calculator calculator) {
        this.calculator = calculator;
    }

    @GetMapping("/nmea-calculator")
    public String home(){
        return "/nmea-calculator";
    }

    @PostMapping("/from_line")
    public String calculateFromLines(@ModelAttribute("lines") String lines,Model model) {
        model.addAttribute("result", calculator.getDistanceFromLine(lines));
        return "/nmea-calculator";
    }

    @PostMapping("/from_file")
    public String calculateFromFile(@RequestParam("file") MultipartFile file,Model model) {
        model.addAttribute("result", calculator.getDistanceFromFile(file));
        return "/nmea-calculator";
    }

}
