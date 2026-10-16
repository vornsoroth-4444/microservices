package kh.edu.istad.platform.payment.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.valueobject.CreditHistoryId;
import kh.edu.istad.platform.payment.domain.valueobject.CreditTransactionType;
import kh.edu.istad.platform.payment.domain.valueobject.TransactionId;

import java.util.UUID;

public class CreditHistory extends AggregateRoot<CreditHistoryId> {
    private final CustomerId customerId;
    private final TransactionId transactionId;
    private final Money amount;
    private final CreditTransactionType transactionType;

    public void initializeTransaction() {
        if (getId() != null) {
            throw new PaymentDomainException("Credit history id must be null before initialization!");
        }
        if (customerId == null || transactionId == null) {
            throw new PaymentDomainException("CustomerId and TransactionId must not be null!");
        }
        if (amount == null || !amount.isGreaterThanZero()) {
            throw new PaymentDomainException("Transaction amount must be greater than zero!");
        }
        if (transactionType == null) {
            throw new PaymentDomainException("Transaction type must not be null!");
        }
        setId(new CreditHistoryId(UUID.randomUUID()));
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    public Money getAmount() {
        return amount;
    }

    public CreditTransactionType getTransactionType() {
        return transactionType;
    }

    private CreditHistory(Builder builder) {
        setId(builder.creditHistoryId);
        customerId = builder.customerId;
        transactionId = builder.transactionId;
        amount = builder.amount;
        transactionType = builder.transactionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CreditHistoryId creditHistoryId;
        private CustomerId customerId;
        private TransactionId transactionId;
        private Money amount;
        private CreditTransactionType transactionType;

        private Builder() {
        }

        public Builder creditHistoryId(CreditHistoryId val) {
            creditHistoryId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder transactionId(TransactionId val) {
            transactionId = val;
            return this;
        }

        public Builder amount(Money val) {
            amount = val;
            return this;
        }

        public Builder transactionType(CreditTransactionType val) {
            transactionType = val;
            return this;
        }

        public CreditHistory build() {
            return new CreditHistory(this);
        }
    }
}
