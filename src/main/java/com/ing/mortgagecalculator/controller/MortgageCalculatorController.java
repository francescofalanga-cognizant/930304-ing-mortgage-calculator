package com.ing.mortgagecalculator.controller;


import com.ing.mortgagecalculator.exception.MaturityPeriodNotFoundException;
import com.ing.mortgagecalculator.model.MortgageRate;
import com.ing.mortgagecalculator.model.web.request.MortgageCheckRequest;
import com.ing.mortgagecalculator.model.web.response.MortgageCheckResponse;
import com.ing.mortgagecalculator.service.MortgageCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Valid
@Tag(name = "Mortgage Management", description = "Operations pertaining to mortgage in Mortgage Management")
public class MortgageCalculatorController {

    @Autowired
    private MortgageCalculatorService mortgageService;

    @Operation(summary = "View a list of available mortgage rates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/interest-rates")
    public List<MortgageRate> getMortgageRates() {
        return mortgageService.getMortgageRates();
    }

    @Operation(summary = "Check if a mortgage is feasible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked mortgage"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/mortgage-check")
    public MortgageCheckResponse checkMortgage(@Valid @RequestBody MortgageCheckRequest request) {
        return mortgageService.checkMortgage(request);
    }


    @ExceptionHandler(MaturityPeriodNotFoundException.class)
    public ResponseEntity<String> handleException(MaturityPeriodNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}