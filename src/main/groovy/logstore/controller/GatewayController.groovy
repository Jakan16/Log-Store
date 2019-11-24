package logstore.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Post
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException

import javax.inject.Inject

@Controller("/gateway")
class GatewayController {

    @Inject
    LogController logController

    @Post("/")
    HttpResponse gateway(Map requestBody) {
        if(!requestBody['method']) {
            return logController.parseFail()
        }
        switch(requestBody['method']) {
            case "get":
                logController.getLogs((Map) requestBody['request'])
                break
            case "post":
                logController.saveLog((Map) requestBody['request'])
                break
            default:
                logController.parseFail()
        }
    }

    @Error(exception = UnsatisfiedRouteException)
    HttpResponse invalidRoute(Exception e) {
        HttpResponse.badRequest(["error" : e.message])
    }
}
