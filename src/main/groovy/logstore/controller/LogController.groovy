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
        LogModel log = TestData.createLog("42")
        Map saved = logService.saveLog(log)
        saved['status'] == RestStatus.CREATED ?
                HttpResponse.created(saved) :
                HttpResponse.serverError(["error" : "something went wrong when saving the log"])
    }

    HttpResponse getLog(Map request) {
        String pathVariable = request['path'].substring(1)
        Map response = logService.getLogById(pathVariable)
        response.status == RestStatus.OK ?
                HttpResponse.ok(response.content) :
                HttpResponse.serverError(["error" : "something went wrong when retrieving the logs"])
    }

    HttpResponse getLogs(Map request) {
        if(request['path'] && (String) request['path'].startsWith('/')) {
            return getLog(request)
        }
        Map response = logService.getLogs()
        response.status == RestStatus.OK ?
                HttpResponse.ok(["results" : response.num_results, "content" : response.content]) :
                HttpResponse.serverError(["error" : "something went wrong when retrieving the logs"])
    }

    HttpResponse parseFail() {
        HttpResponse.badRequest(["error": "failed to parse request body"])
    }

}
