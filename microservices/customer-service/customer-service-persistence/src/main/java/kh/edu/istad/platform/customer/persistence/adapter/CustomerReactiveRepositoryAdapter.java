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
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import kh.edu.istad.platform.customer.persistence.repository.CustomerSegmentMongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

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
    public Flux<CustomerSegment> findAllSegments() {
        return customerSegmentMongoRepository.findAll()
                .map(customerPersistenceMapper::toSegmentDomain);
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerDocument document = customerPersistenceMapper.toDocument(customer);
        return customerMongoRepository.save(document)
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
                Aggregation.lookup("customer_segments", "segment", "_id", "resolvedSegment"),
                Aggregation.unwind("resolvedSegment", true)
        );
        return reactiveMongoTemplate.aggregate(aggregation, "customerDocument", CustomerDocument.class)
                .next()
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> DeleteById(CustomerId id) {
        return customerMongoRepository.deleteById(id.value().toString());
    }

    @Override
    public Mono<Customer> findByUserName(String username) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                Aggregation.lookup("customer_segments", "segment", "_id", "resolvedSegment"),
                Aggregation.unwind("resolvedSegment", true)
        );
        return reactiveMongoTemplate.aggregate(aggregation, "customerDocument", CustomerDocument.class)
                .next()
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> query() {
        return query(CustomerFilterQuery.empty());
    }

    @Override
    public Flux<Customer> query(CustomerFilterQuery filterQuery) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (filterQuery != null) {
            List<Criteria> criteriaList = buildCriteriaList(filterQuery);
            if (criteriaList.size() == 1) {
                operations.add(Aggregation.match(criteriaList.get(0)));
            } else if (criteriaList.size() > 1) {
                operations.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
            }
        }

        // Resolving the @DocumentReference reactively using MongoDB $lookup and $unwind
        operations.add(Aggregation.lookup("customer_segments", "segment", "_id", "resolvedSegment"));
        operations.add(Aggregation.unwind("resolvedSegment", true));

        if (filterQuery != null) {
            if (StringUtils.hasText(filterQuery.sortBy())) {
                Sort.Direction direction = "DESC".equalsIgnoreCase(filterQuery.sortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                operations.add(Aggregation.sort(Sort.by(direction, filterQuery.sortBy().trim())));
            }

            if (filterQuery.page() != null && filterQuery.size() != null && filterQuery.size() > 0) {
                long page = Math.max(0, filterQuery.page());
                operations.add(Aggregation.skip(page * filterQuery.size()));
                operations.add(Aggregation.limit(filterQuery.size()));
            }
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, "customerDocument", CustomerDocument.class)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<CustomerStatistic> getStatistic() {
        return getStatistic(CustomerFilterQuery.empty());
    }

    @Override
    public Flux<CustomerStatistic> getStatistic(CustomerFilterQuery filterQuery) {
        List<AggregationOperation> operations = new ArrayList<>();

        List<Criteria> criteriaList = buildCriteriaList(filterQuery);
        if (filterQuery == null || filterQuery.status() == null) {
            criteriaList.add(Criteria.where("status").ne(null));
        }

        if (criteriaList.size() == 1) {
            operations.add(Aggregation.match(criteriaList.get(0)));
        } else if (criteriaList.size() > 1) {
            operations.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
        }

        operations.add(Aggregation.group("status").count().as("total"));
        operations.add(Aggregation.project("total").and("_id").as("status"));
        operations.add(Aggregation.sort(Sort.Direction.DESC, "total"));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, "customerDocument", CustomerStatisticResult.class)
                .map(customerPersistenceMapper::toStatisticDomain);
    }

    @Override
    public Flux<CustomerReport> getReport() {
        return getReport(CustomerFilterQuery.empty());
    }

    @Override
    public Flux<CustomerReport> getReport(CustomerFilterQuery filterQuery) {
        List<AggregationOperation> operations = new ArrayList<>();

        List<Criteria> criteriaList = buildCriteriaList(filterQuery);
        if (filterQuery == null || filterQuery.status() == null) {
            criteriaList.add(Criteria.where("status").ne(null));
        }

        if (criteriaList.size() == 1) {
            operations.add(Aggregation.match(criteriaList.get(0)));
        } else if (criteriaList.size() > 1) {
            operations.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
        }

        // Resolving the @DocumentReference reactively using MongoDB $lookup and $unwind
        operations.add(Aggregation.lookup("customer_segments", "segment", "_id", "resolvedSegment"));
        operations.add(Aggregation.unwind("resolvedSegment", true));

        operations.add(Aggregation.group("status")
                .count().as("total")
                .push(Aggregation.ROOT).as("customers"));
        operations.add(Aggregation.project("total", "customers").and("_id").as("status"));
        operations.add(Aggregation.sort(Sort.Direction.DESC, "total"));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, "customerDocument", CustomerReportResult.class)
                .map(customerPersistenceMapper::toReportDomain);
    }

    @Override
    public Mono<CustomerSummaryStatistic> getSummaryStatistic() {
        return getStatistic().collectList().map(list -> {
            long total = list.stream().mapToLong(CustomerStatistic::total).sum();
            long active = list.stream()
                    .filter(s -> s.status() == CustomerStatus.ACTIVE)
                    .mapToLong(CustomerStatistic::total)
                    .findFirst()
                    .orElse(0L);
            long inactive = list.stream()
                    .filter(s -> s.status() == CustomerStatus.INACTIVE)
                    .mapToLong(CustomerStatistic::total)
                    .findFirst()
                    .orElse(0L);
            return CustomerSummaryStatistic.builder()
                    .totalCustomers(total)
                    .totalActive(active)
                    .totalInactive(inactive)
                    .details(list)
                    .build();
        });
    }

    private List<Criteria> buildCriteriaList(CustomerFilterQuery filterQuery) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (filterQuery == null) {
            return criteriaList;
        }

        if (StringUtils.hasText(filterQuery.username())) {
            criteriaList.add(Criteria.where("username")
                    .regex(Pattern.quote(filterQuery.username().trim()), "i"));
        }

        if (StringUtils.hasText(filterQuery.familyName())) {
            criteriaList.add(Criteria.where("familyName")
                    .regex(Pattern.quote(filterQuery.familyName().trim()), "i"));
        }

        if (StringUtils.hasText(filterQuery.givenName())) {
            criteriaList.add(Criteria.where("givenName")
                    .regex(Pattern.quote(filterQuery.givenName().trim()), "i"));
        }

        if (StringUtils.hasText(filterQuery.email())) {
            criteriaList.add(Criteria.where("email")
                    .regex(Pattern.quote(filterQuery.email().trim()), "i"));
        }

        if (StringUtils.hasText(filterQuery.phoneNumber())) {
            criteriaList.add(Criteria.where("phoneNumber")
                    .regex(Pattern.quote(filterQuery.phoneNumber().trim()), "i"));
        }

        if (filterQuery.status() != null) {
            criteriaList.add(Criteria.where("status").is(filterQuery.status()));
        }

        if (StringUtils.hasText(filterQuery.keyword())) {
            String pattern = Pattern.quote(filterQuery.keyword().trim());
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("username").regex(pattern, "i"),
                    Criteria.where("familyName").regex(pattern, "i"),
                    Criteria.where("givenName").regex(pattern, "i"),
                    Criteria.where("email").regex(pattern, "i"),
                    Criteria.where("phoneNumber").regex(pattern, "i")
            ));
        }

        return criteriaList;
    }
}
