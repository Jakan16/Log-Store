package logstore.unit

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Shared

import javax.inject.Inject

@MicronautTest
class GatewayControllerSpec extends Specification {

    @Shared @Inject
    EmbeddedServer embeddedServer

    @Shared @AutoCleanup @Inject @Client("/")
    RxHttpClient client

    void "test invalid request gives error response"() {
        when:
        def body = ["method" : "I AM INVALID"]
        def request = HttpRequest.POST("/gateway", body)
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map)

        then:
        HttpClientResponseException e = thrown()
        println e.status
        println e.message
        println e.response.body()
        e.status == HttpStatus.BAD_REQUEST
        'error' in e.response.body()
    }
}
