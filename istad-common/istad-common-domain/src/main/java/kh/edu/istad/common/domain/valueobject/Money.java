package kh.edu.istad.common.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(
        BigDecimal amount
) {
    public static final Money ZERO = new Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN));

    public Money(BigDecimal amount) {
        this.amount = amount != null ? setScale(amount) : null;
    }

    public void isGraterThanZero(){
        if (!(amount != null && amount.compareTo(BigDecimal.ZERO )> 0 )){
            System.out.println("Amount must be greater than zero");
            throw new RuntimeException("Amount must be greater than zero");
        }
    }

    public boolean isGreaterThanZero() {
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Money money) {
        return this.amount != null && money != null && this.amount.compareTo(money.amount()) >= 0;
    }

    public Money add(Money money) {
        return new Money(setScale(this.amount.add(money.amount())));
    }

    public Money subtract(Money money) {
        return new Money(setScale(this.amount.subtract(money.amount())));
    }

    public Money multiply(int multiplier) {
        return new Money(setScale(this.amount.multiply(new BigDecimal(multiplier))));
    }

    private static BigDecimal setScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        if (amount == null || other.amount == null) return amount == other.amount;
        return amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount != null ? setScale(amount).stripTrailingZeros().hashCode() : 0;
    }
}
