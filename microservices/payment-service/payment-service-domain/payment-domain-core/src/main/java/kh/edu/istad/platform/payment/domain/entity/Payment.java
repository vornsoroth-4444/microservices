package kh.edu.istad.platform.payment.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import kh.edu.istad.platform.payment.domain.valueobject.TransactionId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Payment extends AggregateRoot<PaymentId> {
    private final CustomerId customerId;
    private final OrderId orderId;
    private final Money price;
    private final PaymentMethod paymentMethod;

    private PaymentStatus status;
    private TransactionId transactionId;
    private List<String> failureMessages;

    public void initializePayment() {
        validateInitializePayment();
        super.setId(new PaymentId(UUID.randomUUID()));
        this.transactionId = new TransactionId(UUID.randomUUID());
        this.status = PaymentStatus.PENDING;
    }

    private void validateInitializePayment() {
        if (super.getId() != null) {
            throw new PaymentDomainException("Payment id must be null before initialization!");
        }
        if (status != null) {
            throw new PaymentDomainException("Payment status must be null before initialization!");
        }
        validatePayment();
    }

    public void validatePayment() {
        if (customerId == null) {
            throw new PaymentDomainException("CustomerId must not be null!");
        }
        if (orderId == null) {
            throw new PaymentDomainException("OrderId must not be null!");
        }
        if (price == null || !price.isGreaterThanZero()) {
            throw new PaymentDomainException("Total price must be greater than zero!");
        }
        if (paymentMethod == null) {
            throw new PaymentDomainException("PaymentMethod must not be null!");
        }
    }

    public void complete() {
        if (status != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in correct state for complete! Current status: " + status);
        }
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail(List<String> failureMessages) {
        if (status != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in correct state for fail! Current status: " + status);
        }
        this.status = PaymentStatus.FAILED;
        updateFailureMessages(failureMessages);
    }

    public void cancel() {
        if (status != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in correct state for cancel! Current status: " + status);
        }
        this.status = PaymentStatus.CANCELLED;
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream().filter(m -> !m.isEmpty()).toList());
        }
        if (this.failureMessages == null) {
            this.failureMessages = failureMessages != null ? new ArrayList<>(failureMessages) : new ArrayList<>();
        }
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Money getPrice() {
        return price;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    private Payment(Builder builder) {
        super.setId(builder.paymentId);
        customerId = builder.customerId;
        orderId = builder.orderId;
        price = builder.price;
        paymentMethod = builder.paymentMethod;
        status = builder.status;
        transactionId = builder.transactionId;
        failureMessages = builder.failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private PaymentId paymentId;
        private CustomerId customerId;
        private OrderId orderId;
        private Money price;
        private PaymentMethod paymentMethod;
        private PaymentStatus status;
        private TransactionId transactionId;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder paymentId(PaymentId val) {
            paymentId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder paymentMethod(PaymentMethod val) {
            paymentMethod = val;
            return this;
        }

        public Builder status(PaymentStatus val) {
            status = val;
            return this;
        }

        public Builder transactionId(TransactionId val) {
            transactionId = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
