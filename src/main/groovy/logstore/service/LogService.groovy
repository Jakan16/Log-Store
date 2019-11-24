package logstore.service

import logstore.domain.LogModel
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.rest.RestStatus
import org.elasticsearch.search.builder.SearchSourceBuilder

import javax.inject.Inject

class LogService {

    @Inject
    RestHighLevelClient client

    RestStatus saveLog(LogModel inputLog) {
        Map fields = inputLog.toMap()
        IndexRequest request = new IndexRequest("logs").source(fields)
        IndexResponse response = client.index(request, RequestOptions.DEFAULT)
        response.status()
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
