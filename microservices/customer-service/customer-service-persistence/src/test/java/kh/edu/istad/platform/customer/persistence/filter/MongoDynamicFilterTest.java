package kh.edu.istad.platform.customer.persistence.filter;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MongoDynamicFilterTest {

    @Test
    @DisplayName("Should return null criteria when filter is empty")
    void shouldReturnNullCriteriaWhenEmpty() {
        MongoDynamicFilter filter = MongoDynamicFilter.create();
        assertThat(filter.buildCombinedCriteria()).isNull();
    }

    @Test
    @DisplayName("Should build single criteria without andOperator wrapper")
    void shouldBuildSingleCriteria() {
        MongoDynamicFilter filter = MongoDynamicFilter.create()
                .isIfPresent("status", CustomerStatus.ACTIVE);

        Criteria criteria = filter.buildCombinedCriteria();
        assertThat(criteria).isNotNull();

        Document doc = criteria.getCriteriaObject();
        assertThat(doc.get("status")).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should combine multiple criteria with andOperator")
    void shouldCombineMultipleCriteria() {
        MongoDynamicFilter filter = MongoDynamicFilter.create()
                .regexIgnoreCaseIfPresent("username", "john")
                .isIfPresent("status", CustomerStatus.ACTIVE)
                .isTextIfPresent("segment", "seg-100");

        Criteria criteria = filter.buildCombinedCriteria();
        assertThat(criteria).isNotNull();

        Document doc = criteria.getCriteriaObject();
        assertThat(doc).containsKey("$and");
        List<?> andList = (List<?>) doc.get("$and");
        assertThat(andList).hasSize(3);
    }

    @Test
    @DisplayName("Should construct multi-field regex keyword search")
    void shouldConstructKeywordSearch() {
        MongoDynamicFilter filter = MongoDynamicFilter.create()
                .orAnyRegexIgnoreCaseIfPresent("searchKey", "username", "email", "phoneNumber");

        Criteria criteria = filter.buildCombinedCriteria();
        assertThat(criteria).isNotNull();

        Document doc = criteria.getCriteriaObject();
        assertThat(doc).containsKey("$or");
        List<?> orList = (List<?>) doc.get("$or");
        assertThat(orList).hasSize(3);
    }

    @Test
    @DisplayName("Should apply sort and pagination to aggregation operations")
    void shouldApplySortAndPaginationToAggregation() {
        List<AggregationOperation> operations = new ArrayList<>();
        MongoDynamicFilter.applySortAndPagination(operations, "username", "DESC", 2, 10);

        assertThat(operations).hasSize(3); // sort, skip, limit
    }

    @Test
    @DisplayName("Should apply sort and pagination to MongoDB Query")
    void shouldApplySortAndPaginationToQuery() {
        Query query = new Query();
        MongoDynamicFilter.applySortAndPagination(query, "createdAt", "ASC", 1, 20);

        assertThat(query.getSkip()).isEqualTo(20);
        assertThat(query.getLimit()).isEqualTo(20);
        assertThat(query.getSortObject()).containsKey("createdAt");
    }
}
