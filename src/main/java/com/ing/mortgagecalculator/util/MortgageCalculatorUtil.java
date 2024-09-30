package com.ing.mortgagecalculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MortgageCalculatorUtil {


    public static double roundDoubleValues(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
