package kh.edu.istad.platform.payment.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.valueobject.CreditEntryId;

import java.util.UUID;

public class CreditEntry extends AggregateRoot<CreditEntryId> {
    private final CustomerId customerId;
    private Money totalCreditAmount;

    public void initializeCreditEntry() {
        if (getId() != null) {
            throw new PaymentDomainException("Credit entry id must be null before initialization!");
        }
        if (customerId == null) {
            throw new PaymentDomainException("CustomerId must not be null!");
        }
        if (totalCreditAmount == null || !totalCreditAmount.isGreaterThanOrEqualTo(Money.ZERO)) {
            throw new PaymentDomainException("Credit amount must not be negative!");
        }
        setId(new CreditEntryId(UUID.randomUUID()));
    }

    public void addCreditAmount(Money amount) {
        validateAmount(amount);
        totalCreditAmount = totalCreditAmount.add(amount);
    }

    public void subtractCreditAmount(Money amount) {
        validateAmount(amount);
        if (!totalCreditAmount.isGreaterThanOrEqualTo(amount)) {
            throw new PaymentDomainException("Credit balance is insufficient!");
        }
        totalCreditAmount = totalCreditAmount.subtract(amount);
    }

    private void validateAmount(Money amount) {
        if (getId() == null || totalCreditAmount == null) {
            throw new PaymentDomainException("Credit entry must be initialized before changing its balance!");
        }
        if (amount == null || !amount.isGreaterThanZero()) {
            throw new PaymentDomainException("Credit amount must be greater than zero!");
        }
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getTotalCreditAmount() {
        return totalCreditAmount;
    }

    private CreditEntry(Builder builder) {
        setId(builder.creditEntryId);
        customerId = builder.customerId;
        totalCreditAmount = builder.totalCreditAmount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CreditEntryId creditEntryId;
        private CustomerId customerId;
        private Money totalCreditAmount = Money.ZERO;

        private Builder() {
        }

        public Builder creditEntryId(CreditEntryId val) {
            creditEntryId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder totalCreditAmount(Money val) {
            totalCreditAmount = val;
            return this;
        }

        public CreditEntry build() {
            return new CreditEntry(this);
        }
    }
}
