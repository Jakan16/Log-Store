package logstore.unit

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.http.client.RxHttpClient
import logstore.service.LogService
import spock.lang.AutoCleanup
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Shared

import javax.inject.Inject
@Ignore
@MicronautTest
class LogControllerSpec extends Specification {

    @Shared @Inject
    EmbeddedServer embeddedServer

    @Shared @AutoCleanup @Inject @Client("/")
    RxHttpClient client

    void "correct methods are called by get"() {
        setup:
        LogService service = Mock(LogService)

        when:
        def body = [
                "method" : "get",
                "request" : ["path" : "/some-id"]
        ]
        def request = HttpRequest.POST("/gateway", body)
        client.toBlocking().exchange(request, Map)

        then:
        1 * service.getLogByLogID()

        when:
        body = [
                "method" : "get",
                "request" : ["path" : ""]
        ]
        request = HttpRequest.POST("/gateway", body)
        client.toBlocking().exchange(request, Map)

        then:
        1 * service.getLogs()

        when:
        body = [
                "method" : "get",
                "request" : ["path" : "?customer_id=customer_1"]
        ]
        request = HttpRequest.POST("/gateway", body)
        client.toBlocking().exchange(request, Map)

        then:
        1 * service.getLogsByCustomer()
    }
}
