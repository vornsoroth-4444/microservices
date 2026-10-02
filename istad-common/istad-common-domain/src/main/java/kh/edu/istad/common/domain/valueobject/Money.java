package kh.edu.istad.common.domain.valueobject;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount
) {
    public void isGraterThanZero(){
        if (!(amount.compareTo(BigDecimal.ZERO )> 0 )){
            System.out.println("Amount must be greater than zero");
            throw new RuntimeException("Amount must be greater than zero");
        }
    }
    // more logic
}
