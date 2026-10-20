package kh.edu.istad.platform.customer.persistence.filter;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reusable dynamic filtering utility for Reactive Spring Data MongoDB.
 * Constructs clean, composable MongoDB Criteria, Aggregations, and Queries.
 */
public class MongoDynamicFilter {

    private final List<Criteria> criteriaList = new ArrayList<>();

    public static MongoDynamicFilter create() {
        return new MongoDynamicFilter();
    }

    public MongoDynamicFilter isIfPresent(String field, Object value) {
        if (value != null) {
            criteriaList.add(Criteria.where(field).is(value));
        }
        return this;
    }

    public MongoDynamicFilter isTextIfPresent(String field, String value) {
        if (StringUtils.hasText(value)) {
            criteriaList.add(Criteria.where(field).is(value.trim()));
        }
        return this;
    }

    public MongoDynamicFilter regexIgnoreCaseIfPresent(String field, String value) {
        if (StringUtils.hasText(value)) {
            criteriaList.add(Criteria.where(field).regex(Pattern.quote(value.trim()), "i"));
        }
        return this;
    }

    public MongoDynamicFilter neIfPresent(String field, Object value) {
        if (value != null) {
            criteriaList.add(Criteria.where(field).ne(value));
        }
        return this;
    }

    public MongoDynamicFilter inIfNotEmpty(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            criteriaList.add(Criteria.where(field).in(values));
        }
        return this;
    }

    public MongoDynamicFilter orAnyRegexIgnoreCaseIfPresent(String keyword, String... fields) {
        if (StringUtils.hasText(keyword) && fields != null && fields.length > 0) {
            String pattern = Pattern.quote(keyword.trim());
            List<Criteria> orList = new ArrayList<>();
            for (String field : fields) {
                if (StringUtils.hasText(field)) {
                    orList.add(Criteria.where(field).regex(pattern, "i"));
                }
            }
            if (!orList.isEmpty()) {
                criteriaList.add(new Criteria().orOperator(orList.toArray(new Criteria[0])));
            }
        }
        return this;
    }

    public Criteria buildCombinedCriteria() {
        if (criteriaList.isEmpty()) {
            return null;
        }
        if (criteriaList.size() == 1) {
            return criteriaList.get(0);
        }
        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    public List<Criteria> getCriteriaList() {
        return new ArrayList<>(criteriaList);
    }

    public static void applySortAndPagination(
            List<AggregationOperation> operations,
            String sortBy,
            String sortDirection,
            Integer page,
            Integer size
    ) {
        if (StringUtils.hasText(sortBy)) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            operations.add(Aggregation.sort(Sort.by(direction, sortBy.trim())));
        }

        if (page != null && size != null && size > 0) {
            long pageIndex = Math.max(0, page);
            operations.add(Aggregation.skip(pageIndex * size));
            operations.add(Aggregation.limit(size));
        }
    }

    public static void applySortAndPagination(
            Query query,
            String sortBy,
            String sortDirection,
            Integer page,
            Integer size
    ) {
        if (StringUtils.hasText(sortBy)) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            query.with(Sort.by(direction, sortBy.trim()));
        }

        if (page != null && size != null && size > 0) {
            long pageIndex = Math.max(0, page);
            query.skip(pageIndex * size).limit(size);
        }
    }
}
