package logstore.unit

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import logstore.bootstrap.TestData
import logstore.service.LogService
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.rest.RestStatus
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.AutoCleanup
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Shared

import javax.inject.Inject

@Ignore
@Testcontainers
class LogServiceSpec extends Specification {

    @Shared
    ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.4.2")

    @Shared
    LogService logService

    @Shared @Inject @AutoCleanup
    RestHighLevelClient client

    @Shared @Inject @AutoCleanup
    EmbeddedServer embeddedServer

    void setupSpec() {
        container.start()
        embeddedServer = ApplicationContext
                .build()
                .properties(
                            'httpHosts':"${container.httpHostAddress}",
                            'cluster.name':'docker-cluster'
                )
                .packages('logstore')
                .environments('test')
                .run(EmbeddedServer)
        def rClientBuilder = RestClient.builder(HttpHost.create(container.httpHostAddress))
        client = new RestHighLevelClient(rClientBuilder)
        logService = embeddedServer.applicationContext.createBean(LogService, embeddedServer.URL)
    }

    void "test logs are saved and can be retrieved"() {
        setup:
        def log42 = TestData.createLog("42")
        def log21 = TestData.createLog("21")

        when:
        Map response42 = logService.saveLog(log42)
        Map response21 = logService.saveLog(log21)

        then:
        response42.status == RestStatus.CREATED
        response42.id
        response21.status == RestStatus.CREATED
        response21.id

        when:
        Map responseGetAll = logService.getLogs()

        then:
        responseGetAll.status == RestStatus.OK
        responseGetAll.num_results == 2
        responseGetAll.content.size() == 2

        when:
        Map responseGetById = logService.getLogById(response42.id)

        then:
        responseGetById.status == RestStatus.OK
        responseGetById.content['agent_id'] == '42'
    }

}
