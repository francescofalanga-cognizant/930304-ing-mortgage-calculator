package com.ing.mortgagecalculator.model.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ing.mortgagecalculator.model.web.entity.Amount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MortgageCheckResponse {

    private boolean feasible;
    private Amount monthlyCost;
}
