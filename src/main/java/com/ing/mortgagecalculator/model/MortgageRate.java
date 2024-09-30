package com.ing.mortgagecalculator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@AllArgsConstructor
@Getter
@Setter
public class MortgageRate {

    private int maturityPeriod;
    private double interestRate;
    private Date lastUpdate;

}
