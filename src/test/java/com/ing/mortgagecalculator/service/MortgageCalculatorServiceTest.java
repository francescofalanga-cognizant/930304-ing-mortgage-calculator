package com.ing.mortgagecalculator.service;

import com.ing.mortgagecalculator.exception.MaturityPeriodNotFoundException;
import com.ing.mortgagecalculator.model.MortgageRate;
import com.ing.mortgagecalculator.model.web.entity.Amount;
import com.ing.mortgagecalculator.model.web.request.MortgageCheckRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MortgageCalculatorServiceTest {

    public static final String EURO = "EUR";


    @InjectMocks
    private MortgageCalculatorService service;

    @Mock
    private List<MortgageRate> mortgageRates;

    private List<MortgageRate> ratesValues;

    @BeforeEach
    public void setup(){
        ratesValues = List.of(new MortgageRate(20, 3.5, new Date()));
    }


    @Test
    public void testCheckMortgage_OK_feasible() {
        ReflectionTestUtils.setField(service, "mortgageRates", ratesValues);
        ReflectionTestUtils.setField(service, "LIMIT_NUMBER_OF_INCOME_TIMES", 4);

        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(new Amount(50000.0, EURO));
        request.setMaturityPeriod(20);
        request.setLoanValue(new Amount(100000.0, EURO));
        request.setHomeValue(new Amount(200000.0, EURO));

        var result = service.checkMortgage(request);

        // Assert
        assertTrue(result.isFeasible());
        assertNotNull(result.getMonthlyCost());
    }

    @Test
    public void testCheckMortgage_KO_exception() {
        ReflectionTestUtils.setField(service, "mortgageRates", ratesValues);
        ReflectionTestUtils.setField(service, "LIMIT_NUMBER_OF_INCOME_TIMES", 4);

        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(new Amount(50000.0, EURO));
        request.setMaturityPeriod(1);
        request.setLoanValue(new Amount(100000.0, EURO));
        request.setHomeValue(new Amount(200000.0, EURO));

        assertThrows(MaturityPeriodNotFoundException.class, () -> service.checkMortgage(request));
    }


    @Test
    public void testCheckMortgage_KO_not_feasible() {
        ReflectionTestUtils.setField(service, "mortgageRates", ratesValues);
        ReflectionTestUtils.setField(service, "LIMIT_NUMBER_OF_INCOME_TIMES", 4);

        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(new Amount(10000.0, EURO));
        request.setMaturityPeriod(20);
        request.setLoanValue(new Amount(100000.0, EURO));
        request.setHomeValue(new Amount(200000.0, EURO));

        var result = service.checkMortgage(request);

        //Assert
        assertFalse(result.isFeasible());
        assertNull(result.getMonthlyCost());

    }

}
