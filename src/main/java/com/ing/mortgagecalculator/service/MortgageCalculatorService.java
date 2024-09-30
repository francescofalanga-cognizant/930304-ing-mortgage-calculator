package com.ing.mortgagecalculator.service;

import com.ing.mortgagecalculator.exception.MaturityPeriodNotFoundException;
import com.ing.mortgagecalculator.model.MortgageRate;
import com.ing.mortgagecalculator.model.web.entity.Amount;
import com.ing.mortgagecalculator.model.web.request.MortgageCheckRequest;
import com.ing.mortgagecalculator.model.web.response.MortgageCheckResponse;
import com.ing.mortgagecalculator.util.MortgageCalculatorUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;


@Service
@Getter
public class MortgageCalculatorService {
    @Value("${mortgage.calculator.limit.number.of.income.times}")
    private int LIMIT_NUMBER_OF_INCOME_TIMES;

    @Value("${mortgage.calculator.min.loan.term}")
    private int MIN_LOAN_YEARS;

    @Value("${mortgage.calculator.max.loan.term}")
    private int MAX_LOAN_YEARS;

    @Value("${mortgage.calculator.base.loan.interest}")
    private double BASE_LOAN_INTEREST;

    @Value("${mortgage.calculator.loan.interest.increase.factor}")
    private double LOAN_INCREASE_FACTOR;

    // Value of loan interest increase saved in memory
    private final List<MortgageRate> mortgageRates = new ArrayList<>();

    /**
     * This method is called at startup and generates a list of mortgage rates.
     */
    @PostConstruct
    public void init() {
        IntStream.rangeClosed(MIN_LOAN_YEARS, MAX_LOAN_YEARS).forEach(year ->
                mortgageRates.add(new MortgageRate(year, calculateInterestRate(year), new Date()))
        );
    }

    /**
     * This method checks if a mortgage is feasible based on the request.
     * @param request MortgageCheckRequest
     * @return MortgageCheckResponse
     */
    public MortgageCheckResponse checkMortgage(MortgageCheckRequest request) {
        var mortgageRateByPeriod = mortgageRates.stream().filter(rate -> rate.getMaturityPeriod() == request.getMaturityPeriod()).findFirst()
                .orElseThrow(() -> new MaturityPeriodNotFoundException("Maturity period not found: " + request.getMaturityPeriod()));
        boolean isFeasible = request.getLoanValue().getValue() <= request.getIncome().getValue() * LIMIT_NUMBER_OF_INCOME_TIMES && request.getLoanValue().getValue() <= request.getHomeValue().getValue();
        var monthlyCosts = isFeasible ? calculateMonthlyCosts(request, mortgageRateByPeriod) : null;
        return new MortgageCheckResponse(isFeasible, (isFeasible) ? new Amount(monthlyCosts, request.getLoanValue().getCurrency()) : null);
    }

    /**
     * This method calculates the interest rate based on the year.
     * @param year int
     * @return double the interest rate
     */
    private double calculateInterestRate(int year) {
        var yearDiff = year - MIN_LOAN_YEARS;
        return MortgageCalculatorUtil.roundDoubleValues(BASE_LOAN_INTEREST + (yearDiff > 0 ? (yearDiff * LOAN_INCREASE_FACTOR) : 0));
    }

    /**
     * This method calculates the monthly costs based on the request and mortgage rate.
     * @param request MortgageCheckRequest
     * @param mortgageRate MortgageRate
     * @return double the monthly cost
     */
    private double calculateMonthlyCosts(MortgageCheckRequest request, MortgageRate mortgageRate) {
        double monthlyInterestRate = mortgageRate.getInterestRate() / 12 / 100;
        return MortgageCalculatorUtil.roundDoubleValues(request.getLoanValue().getValue() * monthlyInterestRate / (1 - Math.pow(1 + monthlyInterestRate, -mortgageRate.getMaturityPeriod() * 12)));
    }
}

