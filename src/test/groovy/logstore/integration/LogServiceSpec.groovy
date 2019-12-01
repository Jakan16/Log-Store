package logstore.unit

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import logstore.bootstrap.TestData
import logstore.service.LogService
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.rest.RestStatus
import org.testcontainers.elasticsearch.ElasticsearchContainer
import spock.lang.AutoCleanup
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

    static log42 = TestData.createLog("42")
    static log21 = TestData.createLog("21")
    static Map logIds = [:]

    void "test logs are saved and can be retrieved"() {
        when:
        Map response42 = logService.saveLog(log42)
        Map response21 = logService.saveLog(log21)

        then:
        response42.status == RestStatus.CREATED
        response42.id
        response21.status == RestStatus.CREATED
        response21.id

        when:
        logIds["log42"] = response42.id
        logIds["log21"] = response21.id

        then:
        true
    }

    void "test saved logs can be retrieved"() {
        when:
        Map responseGetById42 = logService.getLogById((String) logIds["log42"])

        then:
        responseGetById42.status == RestStatus.OK
        responseGetById42.content['agent_id'] == log42.agentID
        responseGetById42.content['content'] == log42.content

        when:
        Map responseGetById21 = logService.getLogById((String) logIds["log21"])

        then:
        responseGetById21.status == RestStatus.OK
        responseGetById21.content['agent_id'] == log21.agentID
        responseGetById21.content['content'] == log21.content
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
