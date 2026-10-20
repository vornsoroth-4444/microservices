package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerSegmentTest {

    @Test
    @DisplayName("Should successfully validate valid customer segment")
    void shouldValidateCustomerSegment() {
        CustomerSegment segment = CustomerSegment.builder()
                .name("Premium")
                .type(CustomerSegmentType.PREMIUM)
                .description("VIP customers")
                .build();

        segment.validate();

        assertThat(segment.getName()).isEqualTo("Premium");
        assertThat(segment.getType()).isEqualTo(CustomerSegmentType.PREMIUM);
    }

    @Test
    @DisplayName("Should throw exception when segment name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        CustomerSegment segment = CustomerSegment.builder()
                .name("   ")
                .type(CustomerSegmentType.SILVER)
                .build();

        assertThatThrownBy(segment::validate)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("name must not be blank");
    }

    @Test
    @DisplayName("Should throw exception when segment type is null")
    void shouldThrowExceptionWhenTypeIsNull() {
        CustomerSegment segment = CustomerSegment.builder()
                .name("Silver")
                .type(null)
                .build();

        assertThatThrownBy(segment::validate)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("type must not be null");
    }

    @Test
    @DisplayName("Should successfully update segment")
    void shouldUpdateSegment() {
        CustomerSegment segment = CustomerSegment.builder()
                .id("seg-1")
                .name("Silver")
                .type(CustomerSegmentType.SILVER)
                .description("Old desc")
                .build();

        segment.update("Gold", CustomerSegmentType.GOLD, "New desc");

        assertThat(segment.getName()).isEqualTo("Gold");
        assertThat(segment.getType()).isEqualTo(CustomerSegmentType.GOLD);
        assertThat(segment.getDescription()).isEqualTo("New desc");
    }
}
