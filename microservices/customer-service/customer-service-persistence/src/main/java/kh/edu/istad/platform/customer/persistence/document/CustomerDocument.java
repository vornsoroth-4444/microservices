package kh.edu.istad.platform.customer.persistence.document;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customers")
public class CustomerDocument implements Persistable<String> {

    @Id
    private String id;

    private String username;

    private String familyName;

    private String givenName;

    private String email;

    private String phoneNumber;

    private CustomerStatus status;

    // @DocumentReference: stored as a reference ID in MongoDB
    @DocumentReference
    private CustomerSegmentDocument segment;

    // Populated when resolving the reference reactively via MongoDB $lookup
    private CustomerSegmentDocument resolvedSegment;

    public CustomerSegmentDocument getSegment() {
        return resolvedSegment != null ? resolvedSegment : segment;
    }

    public void setSegment(CustomerSegmentDocument segment) {
        this.segment = segment;
    }

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private String modifiedBy;

    @LastModifiedDate
    private Instant modifiedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
