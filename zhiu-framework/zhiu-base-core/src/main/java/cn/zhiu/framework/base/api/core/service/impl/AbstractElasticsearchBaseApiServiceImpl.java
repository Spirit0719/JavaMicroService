package cn.zhiu.framework.base.api.core.service.impl;


import cn.zhiu.framework.base.api.core.enums.PageOrderType;
import cn.zhiu.framework.base.api.core.request.ApiRequest;
import cn.zhiu.framework.base.api.core.request.ApiRequestFilter;
import cn.zhiu.framework.base.api.core.request.ApiRequestOrder;
import cn.zhiu.framework.base.api.core.request.ApiRequestPage;
import cn.zhiu.framework.base.api.core.response.ApiResponse;
import cn.zhiu.framework.base.api.core.service.BaseApiService;
import cn.zhiu.framework.base.api.core.util.BeanMapping;
import cn.zhiu.framework.bean.core.entity.BaseEntity;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * The type Abstract elasticsearch base api service.
 *
 * @author zhuzz
 * @time 2019 /04/26 11:59:48
 */
public abstract class AbstractElasticsearchBaseApiServiceImpl implements BaseApiService {

    @Autowired(required = false)
    protected ElasticsearchTemplate elasticsearchTemplate;

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final static Long SCROLL_TIME = 5 * 60 * 1000L;

    protected Sort convertSort(ApiRequestPage requestPage) {
        if (requestPage.getOrderList() != null && !requestPage.getOrderList().isEmpty()) {
            List<Sort.Order> orderList = new ArrayList<>();
            for (ApiRequestOrder requestOrder : requestPage.getOrderList()) {
                orderList.add(this.convertSortOrder(requestOrder));
            }
            return Sort.by(orderList);
        }
        return Sort.unsorted();
    }

    private Sort.Order convertSortOrder(ApiRequestOrder requestOrder) {
        Sort.Direction direction;
        if (requestOrder.getOrderType().equals(PageOrderType.DESC)) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.ASC;
        }
        return new Sort.Order(direction, requestOrder.getField());
    }

    protected Pageable convertPageable(ApiRequestPage requestPage) {
        return new PageRequest(requestPage.getPage(), requestPage.getPageSize(), this.convertSort(requestPage));
    }

    protected QueryBuilder convertQueryBuilder(ApiRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (request == null || request.getFilterList() == null || request.getFilterList().isEmpty()) {
            return boolQueryBuilder;
        }
        for (ApiRequestFilter filter : request.getFilterList()) {
            switch (filter.getOperatorType()) {
                case EQ:
                    if (filter.getValue() instanceof String || Objects.isNull(filter.getValue())) {
//                        filter.setValue(((String) filter.getValue()).toLowerCase());
                        filter.setField(filter.getField() + ".keyword");
                    }
                    if (Objects.isNull(filter.getValue())) {
                        boolQueryBuilder.mustNot(QueryBuilders.existsQuery(filter.getField()));
                    } else {

                        boolQueryBuilder.must(QueryBuilders.termQuery(filter.getField(), filter.getValue()));
                    }
                    break;
                case GE:
                    if (filter.getValue() instanceof Comparable) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(filter.getField()).gte(filter.getValue()));
                    } else {
                        logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                    }
                    break;
                case LE:
                    if (filter.getValue() instanceof Comparable) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(filter.getField()).lte(filter.getValue()));
                    } else {
                        logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                    }
                    break;
                case GT:
                    if (filter.getValue() instanceof Comparable) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(filter.getField()).gt(filter.getValue()));
                    } else {
                        logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                    }
                    break;
                case LT:
                    if (filter.getValue() instanceof Comparable) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(filter.getField()).lt(filter.getValue()));
                    } else {
                        logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                    }
                    break;
                case BETWEEN:
                    Object val1 = filter.getValueList().get(0);
                    Object val2 = filter.getValueList().get(1);
                    if (val1 instanceof Comparable && val2 instanceof Comparable) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(filter.getField()).from(val1).to(val2));
                    } else {
                        logger.error("字段({})不是可比较对象, value1={}, value2={}", filter.getField(), val1, val2);
                    }
                    break;
                case IN:
                    boolQueryBuilder.must(QueryBuilders.termsQuery(filter.getField(), filter.getValueList()));
                    break;
                case LIKE:
                    MatchPhraseQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(filter.getField(), filter.getValue());
                    boolQueryBuilder.must(matchQueryBuilder);
                    break;
                case LIKES:
                    BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
                    for (Object value : filter.getValueList()) {
                        MatchPhraseQueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery(filter.getField(), QueryParser.escape(value.toString()));
                        subBoolQueryBuilder.should(queryBuilder);
                    }
                    boolQueryBuilder.must(subBoolQueryBuilder);
                    break;
                case LIKE_PREFIX:
                    if (filter.getValue() instanceof String) {
                        filter.setField(filter.getField() + ".keyword");
                    }
                    boolQueryBuilder.must(QueryBuilders.prefixQuery(filter.getField(), filter.getValue().toString()));
                    break;
                case MULTI_MATCH:
                    MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(org.apache.lucene.queryparser.classic.QueryParser.escape(filter.getValue().toString()), filter.getFields().toArray(new String[filter.getFields().size()]));
                    multiMatchQueryBuilder.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
                    multiMatchQueryBuilder.minimumShouldMatch("30%");
                    multiMatchQueryBuilder.tieBreaker(0.3f);
                    boolQueryBuilder.must(multiMatchQueryBuilder);
                    break;
                case NOTIN:
                    boolQueryBuilder.mustNot(QueryBuilders.termsQuery(filter.getField(), filter.getValueList()));
                    break;
                default:
                    logger.error("不支持的运算符, op={}", filter.getOperatorType());
            }
        }
        logger.info("es query json : {}", boolQueryBuilder.toString());
        return boolQueryBuilder;
    }

    protected <T, E> ApiResponse<E> convertApiResponse(Page<T> page, Class<E> c) {
        ApiResponse<E> apiResponse = new ApiResponse<>();
        apiResponse.setPage(page.getNumber());
        apiResponse.setPageSize(page.getSize());
        apiResponse.setTotal(page.getTotalElements());
        apiResponse.setPagedData(BeanMapping.mapList(page.getContent(), c));

        return apiResponse;
    }


    protected <T extends BaseEntity> Map<String, Double> sum(ApiRequest apiRequest, List<String> fieldNames, Class<T> c) {

        Map<String, Double> resultMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(fieldNames)) {
            Document document = c.getAnnotation(Document.class);
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(convertQueryBuilder(apiRequest))
                    .withSearchType(SearchType.QUERY_THEN_FETCH)
                    .withIndices(document.indexName()).withTypes(document.type());

            for (String fieldName : fieldNames) {
                SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum_" + fieldName)
                        .field(fieldName);

                nativeSearchQueryBuilder.addAggregation(sumBuilder);
            }

            SearchQuery searchQuery = nativeSearchQueryBuilder.build();
            Aggregations aggregations = elasticsearchTemplate.query(searchQuery, searchResponse -> {
                return searchResponse.getAggregations();
            });

            Map<String, Aggregation> aggregationMap = aggregations.asMap();

            for (String fieldName : fieldNames) {
                InternalSum internalSum = (InternalSum) aggregationMap.get("sum_" + fieldName);
                double sumAmount = internalSum.getValue();
                resultMap.put(fieldName, sumAmount);
            }

        }
        return resultMap;

    }


    protected <T extends BaseEntity> Map<String, Long> count(ApiRequest apiRequest, List<String> fieldNames, Class<T> c) {

        Map<String, Long> resultMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(fieldNames)) {
            Document document = c.getAnnotation(Document.class);
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(convertQueryBuilder(apiRequest))
                    .withSearchType(SearchType.QUERY_THEN_FETCH)
                    .withIndices(document.indexName()).withTypes(document.type());

            for (String fieldName : fieldNames) {
                CardinalityAggregationBuilder cb = AggregationBuilders.cardinality("count_" + fieldName)
                        .field(fieldName)
                        .precisionThreshold(40000L);

                nativeSearchQueryBuilder.addAggregation(cb);
            }

            SearchQuery searchQuery = nativeSearchQueryBuilder.build();
            Aggregations aggregations = elasticsearchTemplate.query(searchQuery, searchResponse -> {
                return searchResponse.getAggregations();
            });

            Map<String, Aggregation> aggregationMap = aggregations.asMap();

            for (String fieldName : fieldNames) {
                InternalCardinality internalCardinality = (InternalCardinality) aggregationMap.get("count_" + fieldName);
                long countAmount = internalCardinality.getValue();
                resultMap.put(fieldName, countAmount);
            }

        }
        return resultMap;

    }
}
