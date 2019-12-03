package logstore.integration

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import logstore.bootstrap.TestData
import logstore.service.LogService
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.rest.RestStatus
import org.testcontainers.elasticsearch.ElasticsearchContainer
import spock.lang.AutoCleanup
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Shared
import javax.inject.Singleton

class LogServiceSpec extends Specification {

    @Shared
    ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.4.2")

    @Shared
    LogService logService

    @Shared @AutoCleanup
    ApplicationContext applicationContext

    void setupSpec() {
        container.start()
        applicationContext = ApplicationContext.run(
                ["elasticsearch.httpHosts": "http://${container.httpHostAddress}",
                "elasticsearch.cluster.name": "docker-cluster"],
                "test"
        )
        logService = applicationContext.getBean(LogService)
    }

    static log42 = TestData.createLog("log 1", "customer 1","42")
    static log21 = TestData.createLog("log 2", "customer 1","21")

    void "test logs are saved and can be retrieved"() {
        when:
        Map response42 = logService.saveLog(log42)
        Map response21 = logService.saveLog(log21)

        then:
        response42.status == RestStatus.CREATED
        response42.id
        response21.status == RestStatus.CREATED
        response21.id
    }

    @Ignore // elasticsearch configuration thing means it doesn't work
    void "test saved logs can be retrieved by log id"() {
        when:
        Map responseGetById42 = logService.getLogByLogID(log42.logID)

        then:
        responseGetById42.status == RestStatus.OK
        responseGetById42.content.agent_id == log42.agentID
        responseGetById42.content.content == log42.content

        when:
        Map responseGetById21 = logService.getLogByLogID(log21.logID)

        then:
        responseGetById21.status == RestStatus.OK
        responseGetById21.content.agent_id == log21.agentID
        responseGetById21.content.content == log21.content
    }

    @Ignore // elasticsearch configuration thing means it doesn't work
    void "test saved logs can be retrieved by customer id"() {
        when:
        Map responseGetByCustomer = logService.getLogsByCustomer(log42.customerID)

        then:
        responseGetByCustomer.status == RestStatus.OK
        responseGetByCustomer.content.num_results == 2
        responseGetByCustomer.content.content.size == 2
    }

    @Factory
    static class HttpAsyncClientBuilderFactory {
        @Replaces(HttpAsyncClientBuilder.class)
        @Singleton
        HttpAsyncClientBuilder builder() {
            HttpAsyncClientBuilder.create()
        }
    }

}
