package com.ing.mortgagecalculator.model.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ing.mortgagecalculator.model.web.entity.Amount;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class MortgageCheckRequest {

    @JsonProperty(required = true)
    @NotNull(message = "Income cannot be null")
    private Amount income;

    @JsonProperty(required = true)
    @NotNull(message = "Maturity period cannot be null")
    private Integer maturityPeriod;

    @JsonProperty(required = true)
    @NotNull(message = "Loan value cannot be null")
    private Amount loanValue;

    @JsonProperty(required = true)
    @NotNull(message = "Home value cannot be null")
    private Amount homeValue;

}
