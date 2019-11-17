package log.store

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpStatus


@Controller("/gateway")
class GatewayController {

    @Get("/")
    HttpStatus index() {
        return HttpStatus.OK
    }
}