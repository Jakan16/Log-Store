package logstore.service

import logstore.domain.LogModel
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.MatchQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.rest.RestStatus
import org.elasticsearch.search.builder.SearchSourceBuilder

import javax.inject.Inject

class LogService {

    @Inject
    RestHighLevelClient client

    Map saveLog(LogModel inputLog) {
        Map fields = inputLog.toMap()
        IndexRequest request = new IndexRequest("logs").source(fields)
        IndexResponse response = client.index(request, RequestOptions.DEFAULT)
        ['status' : response.status(), 'id' : response.id]
    }

    /*
    Map getLogsById(String id) {
        GetRequest getIdRequest = new GetRequest(index: "logs", id: id)
        GetResponse response = client.get(getIdRequest, RequestOptions.DEFAULT)
        if (!response.exists) {
            return ["status" : RestStatus.NOT_FOUND, "content" : null]
        }
        Map result = response.sourceAsMap
        ["status" : RestStatus.OK, "content" : result]
    }
    */

    Map getLogsByCustomer(String customerID) {
        SearchRequest searchRequest = new SearchRequest("logs")
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("customer_id", customerID)
        searchSourceBuilder.query(matchQueryBuilder)
        searchRequest.source(searchSourceBuilder)
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT)
        RestStatus responseStatus = response.status()
        List<Map> results = response.hits.collect { it.sourceAsMap }
        ["status" : responseStatus, "num_results" : results.size(), "content" : results]
    }

    Map getLogByLogID(String logID) {
        SearchRequest searchRequest = new SearchRequest("logs")
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("log_id", logID)
        searchSourceBuilder.query(matchQueryBuilder)
        searchRequest.source(searchSourceBuilder)
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT)
        if (response.hits.hits.length != 1) {
            return ["status" : RestStatus.NOT_FOUND, "content" : null]
        }
        Map result = response.hits.collect { it.sourceAsMap }[0]
        ["status" : RestStatus.OK, "content" : result]
    }

    Map getLogs() {
        SearchRequest logs = new SearchRequest("logs")
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        searchSourceBuilder.query(QueryBuilders.matchAllQuery())
        logs.source(searchSourceBuilder)
        SearchResponse response = client.search(logs, RequestOptions.DEFAULT)
        RestStatus responseStatus = response.status()
        List<Map> results = response.hits.collect { it.sourceAsMap }
        ["status" : responseStatus, "num_results" : results.size(), "content" : results]
    }

}
