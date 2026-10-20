package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilterQuery;
import kh.edu.istad.platform.customer.domain.dto.CustomerReport;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.dto.CustomerSummaryStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.document.CustomerReportResult;
import kh.edu.istad.platform.customer.persistence.document.CustomerStatisticResult;
import kh.edu.istad.platform.customer.persistence.filter.MongoDynamicFilter;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import kh.edu.istad.platform.customer.persistence.repository.CustomerSegmentMongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

    private static final String CUSTOMERS_COLLECTION = "customers";
    private static final String SEGMENTS_COLLECTION = "customer_segments";

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerSegmentMongoRepository customerSegmentMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public CustomerReactiveRepositoryAdapter(
            CustomerMongoRepository customerMongoRepository,
            CustomerSegmentMongoRepository customerSegmentMongoRepository,
            CustomerPersistenceMapper customerPersistenceMapper,
            ReactiveMongoTemplate reactiveMongoTemplate) {
        this.customerMongoRepository = customerMongoRepository;
        this.customerSegmentMongoRepository = customerSegmentMongoRepository;
        this.customerPersistenceMapper = customerPersistenceMapper;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerDocument document = customerPersistenceMapper.toDocument(customer);
        if (document.getId() == null) {
            return customerMongoRepository.save(document)
                    .flatMap(saved -> findById(customer.getId()));
        }
        return customerMongoRepository.findById(document.getId())
                .flatMap(existing -> {
                    document.setCreatedAt(existing.getCreatedAt());
                    document.setCreatedBy(existing.getCreatedBy());
                    return customerMongoRepository.save(document);
                })
                .switchIfEmpty(Mono.defer(() -> customerMongoRepository.save(document)))
                .flatMap(saved -> findById(customer.getId()));
    }

    @Override
    public Flux<Customer> findAll() {
        return query(CustomerFilterQuery.empty());
    }

    @Override
    public Mono<Customer> findById(CustomerId id) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id.value().toString())),
                Aggregation.lookup(SEGMENTS_COLLECTION, "segment", "_id", "resolvedSegment"),
                Aggregation.unwind("resolvedSegment", true)
        );
        return reactiveMongoTemplate.aggregate(aggregation, CUSTOMERS_COLLECTION, CustomerDocument.class)
                .next()
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Customer> findByUsername(String username) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                Aggregation.lookup(SEGMENTS_COLLECTION, "segment", "_id", "resolvedSegment"),
                Aggregation.unwind("resolvedSegment", true)
        );
        return reactiveMongoTemplate.aggregate(aggregation, CUSTOMERS_COLLECTION, CustomerDocument.class)
                .next()
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(CustomerId id) {
        return customerMongoRepository.existsById(id.value().toString());
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return customerMongoRepository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return customerMongoRepository.existsByEmail(email);
    }

    @Override
    public Mono<Void> deleteById(CustomerId id) {
        return customerMongoRepository.deleteById(id.value().toString());
    }

    @Override
    public Flux<Customer> query() {
        return query(CustomerFilterQuery.empty());
    }

    @Override
    public Flux<Customer> query(CustomerFilterQuery filterQuery) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (filterQuery != null) {
            MongoDynamicFilter filter = MongoDynamicFilter.create()
                    .regexIgnoreCaseIfPresent("username", filterQuery.username())
                    .regexIgnoreCaseIfPresent("familyName", filterQuery.familyName())
                    .regexIgnoreCaseIfPresent("givenName", filterQuery.givenName())
                    .regexIgnoreCaseIfPresent("email", filterQuery.email())
                    .regexIgnoreCaseIfPresent("phoneNumber", filterQuery.phoneNumber())
                    .isIfPresent("status", filterQuery.status())
                    .orAnyRegexIgnoreCaseIfPresent(filterQuery.keyword(), "username", "familyName", "givenName", "email", "phoneNumber");

            if (StringUtils.hasText(filterQuery.segmentId())) {
                filter.isTextIfPresent("segment", filterQuery.segmentId());
            }

            Criteria criteria = filter.buildCombinedCriteria();
            if (criteria != null) {
                operations.add(Aggregation.match(criteria));
            }
        }

        // Resolving @DocumentReference reactively using MongoDB $lookup and $unwind
        operations.add(Aggregation.lookup(SEGMENTS_COLLECTION, "segment", "_id", "resolvedSegment"));
        operations.add(Aggregation.unwind("resolvedSegment", true));

        if (filterQuery != null && StringUtils.hasText(filterQuery.segmentName())) {
            operations.add(Aggregation.match(Criteria.where("resolvedSegment.name")
                    .regex(Pattern.quote(filterQuery.segmentName().trim()), "i")));
        }

        if (filterQuery != null) {
            MongoDynamicFilter.applySortAndPagination(
                    operations,
                    filterQuery.sortBy(),
                    filterQuery.sortDirection(),
                    filterQuery.page(),
                    filterQuery.size()
            );
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, CUSTOMERS_COLLECTION, CustomerDocument.class)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<CustomerSegment> findAllSegments() {
        return customerSegmentMongoRepository.findAll()
                .map(customerPersistenceMapper::toSegmentDomain);
    }

    @Override
    public Flux<CustomerStatistic> getStatistic() {
        return getStatistic(CustomerFilterQuery.empty());
    }

    /**
     * Aggregation for /statistics to report total customers by status and segment.
     */
    @Override
    public Flux<CustomerStatistic> getStatistic(CustomerFilterQuery filterQuery) {
        List<AggregationOperation> operations = new ArrayList<>();

        MongoDynamicFilter filter = MongoDynamicFilter.create()
                .regexIgnoreCaseIfPresent("username", filterQuery != null ? filterQuery.username() : null)
                .regexIgnoreCaseIfPresent("familyName", filterQuery != null ? filterQuery.familyName() : null)
                .regexIgnoreCaseIfPresent("givenName", filterQuery != null ? filterQuery.givenName() : null)
                .regexIgnoreCaseIfPresent("email", filterQuery != null ? filterQuery.email() : null)
                .regexIgnoreCaseIfPresent("phoneNumber", filterQuery != null ? filterQuery.phoneNumber() : null)
                .isIfPresent("status", filterQuery != null ? filterQuery.status() : null)
                .orAnyRegexIgnoreCaseIfPresent(filterQuery != null ? filterQuery.keyword() : null, "username", "familyName", "givenName", "email", "phoneNumber");

        if (filterQuery == null || filterQuery.status() == null) {
            filter.neIfPresent("status", null);
        }

        if (filterQuery != null && StringUtils.hasText(filterQuery.segmentId())) {
            filter.isTextIfPresent("segment", filterQuery.segmentId());
        }

        Criteria criteria = filter.buildCombinedCriteria();
        if (criteria != null) {
            operations.add(Aggregation.match(criteria));
        }

        // Resolving segment reactively
        operations.add(Aggregation.lookup(SEGMENTS_COLLECTION, "segment", "_id", "resolvedSegment"));
        operations.add(Aggregation.unwind("resolvedSegment", true));

        if (filterQuery != null && StringUtils.hasText(filterQuery.segmentName())) {
            operations.add(Aggregation.match(Criteria.where("resolvedSegment.name")
                    .regex(Pattern.quote(filterQuery.segmentName().trim()), "i")));
        }

        // Project status and segmentName (defaulting null to "Unassigned")
        operations.add(Aggregation.project("status")
                .and(ConditionalOperators.ifNull("resolvedSegment.name").then("Unassigned")).as("segmentName"));

        // Group by status AND segmentName, count total
        operations.add(Aggregation.group(
                Fields.from(
                        Fields.field("status", "status"),
                        Fields.field("segmentName", "segmentName")
                )
        ).count().as("total"));

        // Project to match CustomerStatisticResult properties
        operations.add(Aggregation.project("total")
                .and("_id.status").as("status")
                .and("_id.segmentName").as("segmentName"));

        // Sort by status ASC, then total DESC
        operations.add(Aggregation.sort(Sort.Direction.ASC, "status").and(Sort.Direction.DESC, "total"));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, CUSTOMERS_COLLECTION, CustomerStatisticResult.class)
                .map(customerPersistenceMapper::toStatisticDomain);
    }

    @Override
    public Flux<CustomerReport> getReport() {
        return getReport(CustomerFilterQuery.empty());
    }

    @Override
    public Flux<CustomerReport> getReport(CustomerFilterQuery filterQuery) {
        List<AggregationOperation> operations = new ArrayList<>();

        MongoDynamicFilter filter = MongoDynamicFilter.create()
                .regexIgnoreCaseIfPresent("username", filterQuery != null ? filterQuery.username() : null)
                .regexIgnoreCaseIfPresent("familyName", filterQuery != null ? filterQuery.familyName() : null)
                .regexIgnoreCaseIfPresent("givenName", filterQuery != null ? filterQuery.givenName() : null)
                .regexIgnoreCaseIfPresent("email", filterQuery != null ? filterQuery.email() : null)
                .regexIgnoreCaseIfPresent("phoneNumber", filterQuery != null ? filterQuery.phoneNumber() : null)
                .isIfPresent("status", filterQuery != null ? filterQuery.status() : null)
                .orAnyRegexIgnoreCaseIfPresent(filterQuery != null ? filterQuery.keyword() : null, "username", "familyName", "givenName", "email", "phoneNumber");

        if (filterQuery == null || filterQuery.status() == null) {
            filter.neIfPresent("status", null);
        }

        if (filterQuery != null && StringUtils.hasText(filterQuery.segmentId())) {
            filter.isTextIfPresent("segment", filterQuery.segmentId());
        }

        Criteria criteria = filter.buildCombinedCriteria();
        if (criteria != null) {
            operations.add(Aggregation.match(criteria));
        }

        operations.add(Aggregation.lookup(SEGMENTS_COLLECTION, "segment", "_id", "resolvedSegment"));
        operations.add(Aggregation.unwind("resolvedSegment", true));

        if (filterQuery != null && StringUtils.hasText(filterQuery.segmentName())) {
            operations.add(Aggregation.match(Criteria.where("resolvedSegment.name")
                    .regex(Pattern.quote(filterQuery.segmentName().trim()), "i")));
        }

        operations.add(Aggregation.group("status")
                .count().as("total")
                .push(Aggregation.ROOT).as("customers"));
        operations.add(Aggregation.project("total", "customers").and("_id").as("status"));
        operations.add(Aggregation.sort(Sort.Direction.DESC, "total"));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, CUSTOMERS_COLLECTION, CustomerReportResult.class)
                .map(customerPersistenceMapper::toReportDomain);
    }

    @Override
    public Mono<CustomerSummaryStatistic> getSummaryStatistic() {
        return getStatistic().collectList().map(list -> {
            long total = list.stream().mapToLong(CustomerStatistic::total).sum();
            long active = list.stream()
                    .filter(s -> s.status() == CustomerStatus.ACTIVE)
                    .mapToLong(CustomerStatistic::total)
                    .sum();
            long inactive = list.stream()
                    .filter(s -> s.status() == CustomerStatus.INACTIVE)
                    .mapToLong(CustomerStatistic::total)
                    .sum();
            return CustomerSummaryStatistic.builder()
                    .totalCustomers(total)
                    .totalActive(active)
                    .totalInactive(inactive)
                    .details(list)
                    .build();
        });
    }

    @Override
    public Mono<Long> countCustomersBySegmentId(String segmentId) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("segment").is(segmentId),
                Criteria.where("segment.$id").is(segmentId)
        );
        return reactiveMongoTemplate.count(Query.query(criteria), CUSTOMERS_COLLECTION);
    }
}
