package logstore.controller

import io.micronaut.http.HttpResponse
import logstore.bootstrap.TestData
import logstore.domain.LogModel
import logstore.service.LogService
import org.elasticsearch.rest.RestStatus

import javax.inject.Inject

class LogController {

    @Inject
    LogService logService

    HttpResponse saveLog(Map request) {
        LogModel log = TestData.createLog("1")
        RestStatus saved = logService.saveLog(log)
        saved == RestStatus.CREATED ?
                HttpResponse.created(saved) :
                HttpResponse.serverError(["error" : "something went wrong when saving the log"])
    }

    HttpResponse getLogs(Map request) {
        Map response = logService.getLogs()
        response.status == RestStatus.OK ?
                HttpResponse.ok(["results" : response.num_results, "content" : response.content]) :
                HttpResponse.serverError(["error" : "something went wrong when retrieving the logs"])
    }

    HttpResponse parseFail() {
        HttpResponse.badRequest(["error": "failed to parse request body"])
    }

}
