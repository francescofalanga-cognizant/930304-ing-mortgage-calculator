package com.ing.mortgagecalculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.mortgagecalculator.model.MortgageRate;
import com.ing.mortgagecalculator.model.web.entity.Amount;
import com.ing.mortgagecalculator.model.web.request.MortgageCheckRequest;
import com.ing.mortgagecalculator.model.web.response.MortgageCheckResponse;
import com.ing.mortgagecalculator.service.MortgageCalculatorService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(controllers = MortgageCalculatorController.class)
@ExtendWith(MockitoExtension.class)
class MortgageCalculatorControllerTest {

    public static final String EURO = "EUR";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MortgageCalculatorService service;

    @Autowired
    private ObjectMapper objectMapper;

    //Get method
    @Test
    @Order(1)
    public void testGetMortgageRates() throws Exception {
        List<MortgageRate> rates = List.of(new MortgageRate(1, 2.5, new Date()));
        given(service.getMortgageRates()).willReturn(rates);

        ResultActions response = mockMvc.perform(get("/api/interest-rates"));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(rates.size())));
    }

    //Post method
    @Test
    @Order(2)
    public void checkMortgage_feasible() throws Exception {
        var checkRequest = new MortgageCheckRequest(new Amount(60000, EURO), 20,  new Amount(200000.0, EURO), new Amount(300000.0, EURO));
        var checkResponse = new MortgageCheckResponse(true, new Amount(1000.0, "EUR"));
        given(service.checkMortgage(any(MortgageCheckRequest.class))).willReturn(checkResponse);

        // action
        ResultActions response = mockMvc.perform(post("/api/mortgage-check")
                .content(objectMapper.writeValueAsString(checkRequest)).contentType(
                        MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.feasible", is(true)))
                .andExpect(jsonPath("$.monthlyCost.value", is(checkResponse.getMonthlyCost().getValue())));

    }

    //Post method
    @Test
    @Order(3)
    public void checkMortgage_not_feasible() throws Exception {
        var checkRequest = new MortgageCheckRequest(new Amount(10000, EURO), 20,  new Amount(200000.0, EURO), new Amount(300000.0, EURO));
        var checkResponse = new MortgageCheckResponse(false,null);
        given(service.checkMortgage(any(MortgageCheckRequest.class))).willReturn(checkResponse);

        // action
        ResultActions response = mockMvc.perform(post("/api/mortgage-check")
                .content(objectMapper.writeValueAsString(checkRequest)).contentType(
                        MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.feasible", is(false)))
                .andExpect(jsonPath("$.monthlyCost").doesNotExist());

    }


}

