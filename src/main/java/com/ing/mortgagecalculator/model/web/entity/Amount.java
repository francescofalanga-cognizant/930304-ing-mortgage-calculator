package com.ing.mortgagecalculator.model.web.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amount {

    private double value;

    // Currency of the amount
    // Do i really need to validate this?
    private String currency;

}
